/**
 * Push notification Cloud Functions (FCM).
 *
 * Firestore-triggered functions that send FCM notifications for
 * chat messages, friend requests, request acceptances, and shared inbox items.
 */
import {onDocumentCreated, onDocumentUpdated} from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";
import {getFirestore, checkWriteRateLimit} from "./helpers.js";
import {logger} from "./logger.js";

const MAX_MESSAGE_PREVIEW_LENGTH = 100;
const MAX_FRIEND_REQUESTS_PER_HOUR = 5;
const MAX_CHAT_MESSAGES_PER_MINUTE = 10;
const CHAT_RATE_WINDOW_MS = 60 * 1000; // 1 minute

// ---- Spam detection constants ----
const SPAM_RECENT_MESSAGES_WINDOW = 5; // check the last N messages
const SPAM_DUPLICATE_THRESHOLD = 3; // N identical messages in a row = spam
const SPAM_LINK_FLOOD_THRESHOLD = 3; // N links in a single message = spam
const LINK_PATTERN = /https?:\/\/[^\s]+/gi;

/**
 * Lightweight server-side spam pattern check.
 * Returns true if the message looks like spam (repeated messages or link flooding).
 */
async function isSpamMessage(
  chatId: string,
  senderId: string,
  content: string
): Promise<boolean> {
  // Check 1: Link flooding — too many URLs in a single message
  const links = content.match(LINK_PATTERN);
  if (links && links.length >= SPAM_LINK_FLOOD_THRESHOLD) {
    logger.warn("Spam detected: link flooding", {chatId, senderId, linkCount: links.length});
    return true;
  }

  // Check 2: Repeated identical messages from the same sender
  try {
    const recentSnap = await getFirestore()
      .collection("chats").doc(chatId).collection("messages")
      .where("senderId", "==", senderId)
      .orderBy("createdAt", "desc")
      .limit(SPAM_RECENT_MESSAGES_WINDOW)
      .get();

    if (!recentSnap.empty) {
      const duplicateCount = recentSnap.docs
        .filter((doc) => doc.data().content === content)
        .length;
      if (duplicateCount >= SPAM_DUPLICATE_THRESHOLD) {
        logger.warn("Spam detected: repeated messages", {chatId, senderId, duplicateCount});
        return true;
      }
    }
  } catch (err: any) {
    // Don't block notifications if spam check fails
    logger.error("Spam check failed", {message: err?.message, chatId});
  }
  return false;
}

/**
 * Fetch a user's FCM tokens, send a multicast data message, and clean up invalid tokens.
 *
 * Centralises the token-fetch -> send -> cleanup logic that is shared by all
 * push-notification triggers (chat, friend request, shared inbox).
 */
async function sendFcmToUser(
  receiverId: string,
  fcmData: Record<string, string>,
  priority: "high" | "normal",
  logTag: string
): Promise<void> {
  const tokensSnap = await getFirestore()
    .collection("users").doc(receiverId)
    .collection("fcm_tokens")
    .get();

  if (tokensSnap.empty) {
    logger.info(`${logTag}: no FCM tokens for receiver=${receiverId}`);
    return;
  }

  const tokens: string[] = tokensSnap.docs
    .map((doc) => doc.data().token as string)
    .filter((t) => !!t);

  if (tokens.length === 0) return;

  const fcmMessage: admin.messaging.MulticastMessage = {
    tokens,
    data: fcmData,
    android: {priority},
  };

  const response = await admin.messaging().sendEachForMulticast(fcmMessage);
  logger.info(
    `${logTag}: sent to ${tokens.length} tokens, ` +
    `success=${response.successCount}, failure=${response.failureCount}`
  );

  const invalidTokens: string[] = [];
  response.responses.forEach((resp, idx) => {
    if (!resp.success) {
      const errCode = resp.error?.code ?? "";
      if (
        errCode === "messaging/registration-token-not-registered" ||
        errCode === "messaging/invalid-registration-token"
      ) {
        invalidTokens.push(tokens[idx]);
      } else {
        logger.warn(`${logTag}: token[${idx}] error: ${resp.error?.message}`);
      }
    }
  });

  if (invalidTokens.length > 0) {
    const batch = getFirestore().batch();
    for (const token of invalidTokens) {
      const ref = getFirestore()
        .collection("users").doc(receiverId)
        .collection("fcm_tokens").doc(token);
      batch.delete(ref);
    }
    await batch.commit();
    logger.info(`${logTag}: removed ${invalidTokens.length} invalid tokens`);
  }
}

/**
 * Sends an FCM push notification to the recipient when a new chat message is created.
 *
 * Trigger: Firestore document create at chats/{chatId}/messages/{messageId}
 */
export const sendChatNotification = onDocumentCreated(
  {
    document: "chats/{chatId}/messages/{messageId}",
    region: "us-central1",
  },
  async (event) => {
    const data = event.data?.data();
    if (!data) return;

    const senderId: string = data.senderId ?? "";
    const receiverId: string = data.receiverId ?? "";
    const content: string = data.content ?? "";
    const senderUsernameFromMessage: string = data.senderUsername ?? "";
    const type: string = data.type ?? "TEXT";

    if (!senderId || !receiverId) return;

    // Don't notify for shared-item messages (they have their own inbox flow)
    if (type !== "TEXT") return;

    // Server-side spam check — skip notification for spam messages
    if (await isSpamMessage(event.params.chatId, senderId, content)) return;

    // Server-side rate limiting — suppress notification for rapid message senders
    const chatAllowed = await checkWriteRateLimit(
      senderId, "chat", MAX_CHAT_MESSAGES_PER_MINUTE, CHAT_RATE_WINDOW_MS
    );
    if (!chatAllowed) {
      logger.warn("Chat message rate limit exceeded, suppressing notification", {
        senderId,
        chatId: event.params.chatId,
      });
      return;
    }

    try {
      let senderUsername = senderUsernameFromMessage;
      if (!senderUsername) {
        // Backward compatibility for existing clients until all message writes include senderUsername.
        const senderProfileSnap = await getFirestore()
          .collection("users").doc(senderId)
          .collection("profile").doc("public")
          .get();
        senderUsername = senderProfileSnap.data()?.username ?? "Friend";
      }

      const messagePreview = content.length > MAX_MESSAGE_PREVIEW_LENGTH
        ? content.substring(0, MAX_MESSAGE_PREVIEW_LENGTH) + "\u2026"
        : content;

      await sendFcmToUser(
        receiverId,
        {
          type: "new_message",
          senderId,
          senderUsername,
          messagePreview,
        },
        "high",
        "sendChatNotification"
      );
    } catch (err: any) {
      logger.error("sendChatNotification: unexpected error", {
        message: err?.message,
        chatId: event.params.chatId,
      });
    }
  }
);

/**
 * Sends an FCM push notification to the recipient when a new friend request is created.
 * Also enforces server-side rate limiting (max 5 requests/hour) to prevent spam.
 *
 * Trigger: Firestore document create at friend_requests/{requestId}
 */
export const sendFriendRequestNotification = onDocumentCreated(
  {
    document: "friend_requests/{requestId}",
    region: "us-central1",
  },
  async (event) => {
    const data = event.data?.data();
    if (!data) return;

    // Only process new PENDING requests
    if (data.status !== "PENDING") return;

    const senderId: string = data.fromUserId ?? "";
    const receiverId: string = data.toUserId ?? "";
    const senderUsername: string = data.fromUsername ?? "Someone";

    if (!senderId || !receiverId) return;

    // Server-side rate limiting: count PENDING requests from this sender in the last hour
    try {
      const oneHourAgo = new Date(Date.now() - 60 * 60 * 1000);
      const recentRequests = await getFirestore()
        .collection("friend_requests")
        .where("fromUserId", "==", senderId)
        .where("status", "==", "PENDING")
        .where("createdAt", ">", admin.firestore.Timestamp.fromDate(oneHourAgo))
        .get();

      // This query includes the just-created request that triggered this function.
      // Using "> MAX" therefore allows exactly MAX requests/hour and blocks the next one.
      if (recentRequests.size > MAX_FRIEND_REQUESTS_PER_HOUR) {
        logger.warn("Friend request rate limit exceeded, deleting request", {
          senderId,
          requestId: event.params.requestId,
          recentCount: recentRequests.size,
        });
        // Delete the offending request
        await event.data?.ref.delete();
        return;
      }
    } catch (err: any) {
      // Don't block notifications if rate-limit check fails
      logger.error("Friend request rate limit check failed", {
        message: err?.message,
        senderId,
      });
    }

    try {
      await sendFcmToUser(
        receiverId,
        {
          type: "friend_request",
          senderUsername,
        },
        "normal",
        "sendFriendRequestNotification"
      );
    } catch (err: any) {
      logger.error("sendFriendRequestNotification: error", {message: err?.message});
    }
  }
);

/**
 * Sends an FCM push notification to the original requester when their friend
 * request is accepted.
 *
 * Trigger: Firestore document update at friend_requests/{requestId}
 *
 * Only fires when the status transitions from PENDING to ACCEPTED.
 */
export const sendRequestAcceptedNotification = onDocumentUpdated(
  {
    document: "friend_requests/{requestId}",
    region: "us-central1",
  },
  async (event) => {
    const before = event.data?.before?.data();
    const after = event.data?.after?.data();
    if (!before || !after) return;

    // Only notify when status transitions from PENDING to ACCEPTED
    if (before.status !== "PENDING" || after.status !== "ACCEPTED") return;

    const receiverId: string = after.fromUserId ?? "";
    const friendUsername: string = after.toUsername ?? "Someone";

    if (!receiverId) return;

    try {
      await sendFcmToUser(
        receiverId,
        {
          type: "request_accepted",
          friendUsername,
        },
        "normal",
        "sendRequestAcceptedNotification"
      );
    } catch (err: any) {
      logger.error("sendRequestAcceptedNotification: error", {
        message: err?.message,
        requestId: event.params.requestId,
      });
    }
  }
);

/**
 * Sends an FCM push notification when a new item is added to a user's shared inbox.
 *
 * Trigger: Firestore document create at users/{userId}/shared_inbox/{itemId}
 */
export const sendSharedInboxNotification = onDocumentCreated(
  {
    document: "users/{userId}/shared_inbox/{itemId}",
    region: "us-central1",
  },
  async (event) => {
    const data = event.data?.data();
    if (!data) return;

    // Only notify for new PENDING items
    if (data.status !== "PENDING") return;

    const receiverId: string = event.params.userId;
    const senderUsername: string = data.fromUsername ?? "A friend";
    const itemType: string = data.type ?? "WORD";

    if (!receiverId) return;

    // Map SharedItemType to a human-readable label for the notification
    const typeLabels: Record<string, string> = {
      WORD: "a word",
      LEARNING_SHEET: "a learning sheet",
      QUIZ: "a quiz",
    };
    const typeLabel = typeLabels[itemType] ?? "an item";

    try {
      await sendFcmToUser(
        receiverId,
        {
          type: "shared_item",
          senderUsername,
          itemType,
          title: typeLabel,
        },
        "normal",
        "sendSharedInboxNotification"
      );
    } catch (err: any) {
      logger.error("sendSharedInboxNotification: error", {
        message: err?.message,
        userId: event.params.userId,
        itemId: event.params.itemId,
      });
    }
  }
);

const MAX_FEEDBACK_PER_HOUR = 3;
const FEEDBACK_RATE_WINDOW_MS = 60 * 60 * 1000; // 1 hour

/**
 * Enforces server-side rate limiting for feedback submissions.
 * If the user exceeds 3 submissions per hour, the feedback document is deleted.
 *
 * Trigger: Firestore document create at feedback/{feedbackId}
 */
export const enforceFeedbackRateLimit = onDocumentCreated(
  {
    document: "feedback/{feedbackId}",
    region: "us-central1",
  },
  async (event) => {
    const data = event.data?.data();
    if (!data) return;

    const userId: string = data.userId ?? "";
    if (!userId) return;

    const allowed = await checkWriteRateLimit(
      userId, "feedback", MAX_FEEDBACK_PER_HOUR, FEEDBACK_RATE_WINDOW_MS
    );
    if (!allowed) {
      logger.warn("Feedback rate limit exceeded, deleting feedback", {
        userId,
        feedbackId: event.params.feedbackId,
      });
      await event.data?.ref.delete();
    }
  }
);
