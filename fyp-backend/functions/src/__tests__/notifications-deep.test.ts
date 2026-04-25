/**
 * Deep branch tests for notifications.ts trigger logic.
 */

const createdHandlers: Record<string, any> = {};
const updatedHandlers: Record<string, any> = {};

const mockGetFriendRequests = jest.fn();
const mockGetSpamMessages = jest.fn();
const mockGetTokens = jest.fn();
const mockGetSenderPublicProfile = jest.fn();
const mockSendEachForMulticast = jest.fn();
const mockBatchDelete = jest.fn();
const mockBatchCommit = jest.fn().mockResolvedValue(undefined);

function createQuery(getMock: jest.Mock): any {
  return {
    where: jest.fn().mockImplementation(() => createQuery(getMock)),
    orderBy: jest.fn().mockImplementation(() => createQuery(getMock)),
    limit: jest.fn().mockImplementation(() => ({
      get: getMock,
    })),
    get: getMock,
  };
}

const mockFirestore = {
  collection: jest.fn((name: string) => {
    if (name === "friend_requests") {
      return createQuery(mockGetFriendRequests);
    }

    if (name === "chats") {
      return {
        doc: jest.fn(() => ({
          collection: jest.fn((sub: string) => {
            if (sub !== "messages") throw new Error(`Unexpected chats subcollection: ${sub}`);
            return createQuery(mockGetSpamMessages);
          }),
        })),
      };
    }

    if (name === "users") {
      return {
        doc: jest.fn((userId: string) => ({
          collection: jest.fn((sub: string) => {
            if (sub === "fcm_tokens") {
              return {
                get: mockGetTokens,
                doc: jest.fn((token: string) => ({
                  path: `users/${userId}/fcm_tokens/${token}`,
                })),
              };
            }

            if (sub === "profile") {
              return {
                doc: jest.fn((docId: string) => {
                  if (docId !== "public") throw new Error(`Unexpected profile doc: ${docId}`);
                  return {get: mockGetSenderPublicProfile};
                }),
              };
            }

            throw new Error(`Unexpected users subcollection: ${sub}`);
          }),
        })),
      };
    }

    throw new Error(`Unexpected collection: ${name}`);
  }),
  batch: jest.fn(() => ({
    delete: mockBatchDelete,
    commit: mockBatchCommit,
  })),
};

jest.mock("firebase-functions/v2/firestore", () => ({
  onDocumentCreated: jest.fn((opts: any, handler: any) => {
    createdHandlers[opts.document] = handler;
    return handler;
  }),
  onDocumentUpdated: jest.fn((opts: any, handler: any) => {
    updatedHandlers[opts.document] = handler;
    return handler;
  }),
}));

jest.mock("firebase-admin", () => ({
  messaging: jest.fn(() => ({
    sendEachForMulticast: mockSendEachForMulticast,
  })),
  firestore: {
    Timestamp: {
      fromDate: jest.fn((d: Date) => d),
    },
  },
}));

jest.mock("../helpers.js", () => ({
  getFirestore: jest.fn(() => mockFirestore),
  checkWriteRateLimit: jest.fn().mockResolvedValue(true),
  logUid: jest.fn((uid: string) => uid),
  logChat: jest.fn((chatId: string) => chatId),
}));

jest.mock("../logger.js", () => ({
  logger: {
    info: jest.fn(),
    warn: jest.fn(),
    error: jest.fn(),
  },
}));

import "../notifications.js";
import {logger} from "../logger.js";

describe("notifications deep branches", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("deletes friend request when server-side rate limit is exceeded", async () => {
    const handler = createdHandlers["friend_requests/{requestId}"];
    const mockDeleteRequest = jest.fn().mockResolvedValue(undefined);

    mockGetFriendRequests.mockResolvedValueOnce({size: 6});

    await handler({
      params: {requestId: "req-1"},
      data: {
        data: () => ({
          status: "PENDING",
          fromUserId: "sender-1",
          toUserId: "receiver-1",
          fromUsername: "Sender",
        }),
        ref: {delete: mockDeleteRequest},
      },
    });

    expect(mockDeleteRequest).toHaveBeenCalledTimes(1);
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("sends accepted-request notification on PENDING->ACCEPTED transition", async () => {
    const handler = updatedHandlers["friend_requests/{requestId}"];

    mockGetTokens.mockResolvedValueOnce({
      empty: false,
      docs: [{data: () => ({token: "tok-1"})}],
    });

    mockSendEachForMulticast.mockResolvedValueOnce({
      successCount: 1,
      failureCount: 0,
      responses: [{success: true}],
    });

    await handler({
      params: {requestId: "req-2"},
      data: {
        before: {data: () => ({status: "PENDING"})},
        after: {
          data: () => ({
            status: "ACCEPTED",
            fromUserId: "receiver-1",
            toUsername: "FriendName",
          }),
        },
      },
    });

    expect(mockSendEachForMulticast).toHaveBeenCalledWith(
      expect.objectContaining({
        tokens: ["tok-1"],
        android: {priority: "normal"},
        data: expect.objectContaining({
          type: "request_accepted",
          friendUsername: "FriendName",
        }),
      })
    );
  });

  it("removes invalid FCM tokens after shared inbox send", async () => {
    const handler = createdHandlers["users/{userId}/shared_inbox/{itemId}"];

    mockGetTokens.mockResolvedValueOnce({
      empty: false,
      docs: [
        {data: () => ({token: "tok-1"})},
        {data: () => ({token: "tok-2"})},
      ],
    });

    mockSendEachForMulticast.mockResolvedValueOnce({
      successCount: 1,
      failureCount: 1,
      responses: [
        {success: true},
        {
          success: false,
          error: {
            code: "messaging/invalid-registration-token",
            message: "bad token",
          },
        },
      ],
    });

    await handler({
      params: {userId: "receiver-2", itemId: "item-1"},
      data: {
        data: () => ({
          status: "PENDING",
          fromUsername: "Sharer",
          type: "WORD",
        }),
      },
    });

    expect(mockBatchDelete).toHaveBeenCalledWith(
      expect.objectContaining({path: "users/receiver-2/fcm_tokens/tok-2"})
    );
    expect(mockBatchCommit).toHaveBeenCalledTimes(1);
  });

  it("uses profile fallback and truncates long chat preview", async () => {
    const handler = createdHandlers["chats/{chatId}/messages/{messageId}"];
    const longMessage = "x".repeat(120);

    mockGetSpamMessages.mockResolvedValueOnce({
      empty: false,
      docs: [{data: () => ({content: "different content"})}],
    });

    mockGetSenderPublicProfile.mockResolvedValueOnce({
      data: () => ({username: "ProfileName"}),
    });

    mockGetTokens.mockResolvedValueOnce({
      empty: false,
      docs: [{data: () => ({token: "tok-chat"})}],
    });

    mockSendEachForMulticast.mockResolvedValueOnce({
      successCount: 1,
      failureCount: 0,
      responses: [{success: true}],
    });

    await handler({
      params: {chatId: "chat-1", messageId: "m-1"},
      data: {
        data: () => ({
          senderId: "sender-3",
          receiverId: "receiver-3",
          senderUsername: "",
          content: longMessage,
          type: "TEXT",
        }),
      },
    });

    expect(mockSendEachForMulticast).toHaveBeenCalledWith(
      expect.objectContaining({
        android: {priority: "high"},
        data: expect.objectContaining({
          senderUsername: "ProfileName",
          messagePreview: `${"x".repeat(100)}…`,
        }),
      })
    );
  });

  it("flags repeated-message spam and suppresses send", async () => {
    const handler = createdHandlers["chats/{chatId}/messages/{messageId}"];

    mockGetSpamMessages.mockResolvedValueOnce({
      empty: false,
      docs: [
        {data: () => ({content: "dup"})},
        {data: () => ({content: "dup"})},
        {data: () => ({content: "dup"})},
      ],
    });

    await handler({
      params: {chatId: "chat-dup", messageId: "m-dup"},
      data: {
        data: () => ({
          senderId: "sender-dup",
          receiverId: "receiver-dup",
          senderUsername: "Sender",
          content: "dup",
          type: "TEXT",
        }),
      },
    });

    expect(logger.warn).toHaveBeenCalledWith(
      "Spam detected: repeated messages",
      expect.objectContaining({chatId: "chat-dup"})
    );
    expect(mockSendEachForMulticast).not.toHaveBeenCalled();
  });

  it("continues notification flow when spam-check query fails", async () => {
    const handler = createdHandlers["chats/{chatId}/messages/{messageId}"];

    mockGetSpamMessages.mockRejectedValueOnce(new Error("query failed"));
    mockGetTokens.mockResolvedValueOnce({empty: true, docs: []});

    await handler({
      params: {chatId: "chat-err", messageId: "m-err"},
      data: {
        data: () => ({
          senderId: "s-1",
          receiverId: "r-1",
          senderUsername: "Sender",
          content: "hello",
          type: "TEXT",
        }),
      },
    });

    expect(logger.error).toHaveBeenCalledWith(
      "Spam check failed",
      expect.objectContaining({chatId: "chat-err"})
    );
    expect(logger.info).toHaveBeenCalledWith("sendChatNotification: no FCM tokens for receiver=r-1");
  });

  it("logs non-invalid token errors without deleting tokens", async () => {
    const handler = createdHandlers["users/{userId}/shared_inbox/{itemId}"];

    mockGetTokens.mockResolvedValueOnce({
      empty: false,
      docs: [{data: () => ({token: "tok-warning"})}],
    });

    mockSendEachForMulticast.mockResolvedValueOnce({
      successCount: 0,
      failureCount: 1,
      responses: [{success: false, error: {code: "messaging/internal-error", message: "oops"}}],
    });

    await handler({
      params: {userId: "receiver-warn", itemId: "item-warn"},
      data: {
        data: () => ({status: "PENDING", fromUsername: "Sharer", type: "QUIZ"}),
      },
    });

    expect(logger.warn).toHaveBeenCalledWith("sendSharedInboxNotification: token[0] error: oops");
    expect(mockBatchDelete).not.toHaveBeenCalled();
  });

  it("continues friend request notification when rate-limit query fails", async () => {
    const handler = createdHandlers["friend_requests/{requestId}"];

    mockGetFriendRequests.mockRejectedValueOnce(new Error("rate check fail"));
    mockGetTokens.mockResolvedValueOnce({empty: true, docs: []});

    await handler({
      params: {requestId: "req-rate-err"},
      data: {
        data: () => ({
          status: "PENDING",
          fromUserId: "sender-rate",
          toUserId: "receiver-rate",
          fromUsername: "Sender",
        }),
      },
    });

    expect(logger.error).toHaveBeenCalledWith(
      "Friend request rate limit check failed",
      expect.objectContaining({senderId: "sender-rate"})
    );
    expect(logger.info).toHaveBeenCalledWith(
      "sendFriendRequestNotification: no FCM tokens for receiver=receiver-rate"
    );
  });

  it("logs and swallows friend-request send failures", async () => {
    const handler = createdHandlers["friend_requests/{requestId}"];

    mockGetFriendRequests.mockResolvedValueOnce({size: 1});
    mockGetTokens.mockResolvedValueOnce({
      empty: false,
      docs: [{data: () => ({token: "tok-friend"})}],
    });
    mockSendEachForMulticast.mockRejectedValueOnce(new Error("friend send failed"));

    await handler({
      params: {requestId: "req-friend-fail"},
      data: {
        data: () => ({
          status: "PENDING",
          fromUserId: "sender-friend",
          toUserId: "receiver-friend",
          fromUsername: "Sender",
        }),
      },
    });

    expect(logger.error).toHaveBeenCalledWith(
      "sendFriendRequestNotification: error",
      expect.objectContaining({message: "friend send failed"})
    );
  });

  it("logs and swallows errors in accepted notification send path", async () => {
    const handler = updatedHandlers["friend_requests/{requestId}"];

    mockGetTokens.mockResolvedValueOnce({
      empty: false,
      docs: [{data: () => ({token: "tok-acc"})}],
    });
    mockSendEachForMulticast.mockRejectedValueOnce(new Error("send failed"));

    await handler({
      params: {requestId: "req-acc-fail"},
      data: {
        before: {data: () => ({status: "PENDING"})},
        after: {data: () => ({status: "ACCEPTED", fromUserId: "receiver-acc", toUsername: "Friend"})},
      },
    });

    expect(logger.error).toHaveBeenCalledWith(
      "sendRequestAcceptedNotification: error",
      expect.objectContaining({requestId: "req-acc-fail"})
    );
  });

  it("logs and swallows errors in shared inbox notification send path", async () => {
    const handler = createdHandlers["users/{userId}/shared_inbox/{itemId}"];

    mockGetTokens.mockResolvedValueOnce({
      empty: false,
      docs: [{data: () => ({token: "tok-shared"})}],
    });
    mockSendEachForMulticast.mockRejectedValueOnce(new Error("shared send failed"));

    await handler({
      params: {userId: "receiver-shared", itemId: "item-shared"},
      data: {
        data: () => ({status: "PENDING", fromUsername: "Sharer", type: "WORD"}),
      },
    });

    expect(logger.error).toHaveBeenCalledWith(
      "sendSharedInboxNotification: error",
      expect.objectContaining({userId: "receiver-shared", itemId: "item-shared"})
    );
  });
});
