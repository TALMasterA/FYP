/**
 * Server-authoritative account deletion callable.
 *
 * The Android client re-authenticates the user locally, then calls this endpoint
 * so Firestore cleanup and Firebase Auth deletion happen with Admin privileges.
 */
import {HttpsError} from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import {onAppCheckCall} from "./functionWrappers.js";
import {getFirestore, requireAuth} from "./helpers.js";
import {logger} from "./logger.js";

const USER_SUBCOLLECTIONS = [
  "history",
  "word_banks",
  "learning_sheets",
  "quiz_attempts",
  "quiz_stats",
  "generated_quizzes",
  "quiz_versions",
  "favorites",
  "custom_words",
  "sessions",
  "coin_awards",
  "last_awarded_quiz",
  "user_stats",
  "friends",
  "shared_inbox",
  "favorite_sessions",
  "blocked_users",
  "fcm_tokens",
];

const PROFILE_DOCS = ["settings", "info", "public"];
const DELETE_BATCH_SIZE = 450;
const MAIL_COLLECTION = process.env.ACCOUNT_DELETION_MAIL_COLLECTION || "mail";
const MAIL_TTL_DAYS = 7;

function accountDeletionEmailEnabled(): boolean {
  return process.env.ACCOUNT_DELETION_EMAIL_ENABLED === "true";
}

async function deleteCollection(ref: admin.firestore.CollectionReference): Promise<number> {
  let deleted = 0;

  while (true) {
    const snap = await ref.limit(DELETE_BATCH_SIZE).get();
    if (snap.empty) break;

    const batch = getFirestore().batch();
    snap.docs.forEach((doc) => batch.delete(doc.ref));
    await batch.commit();
    deleted += snap.size;
  }

  return deleted;
}

async function deleteUserSubcollections(uid: string): Promise<number> {
  const userRef = getFirestore().collection("users").doc(uid);
  let deleted = 0;

  for (const collectionName of USER_SUBCOLLECTIONS) {
    deleted += await deleteCollection(userRef.collection(collectionName));
  }

  return deleted;
}

async function deleteProfileDocs(uid: string): Promise<void> {
  const profileRef = getFirestore().collection("users").doc(uid).collection("profile");

  await Promise.all(
    PROFILE_DOCS.map((docId) => profileRef.doc(docId).delete())
  );
}

async function deleteUsernameLookupDocs(uid: string): Promise<number> {
  const firestore = getFirestore();
  let deleted = 0;

  const usernameSnap = await firestore
    .collection("usernames")
    .where("userId", "==", uid)
    .limit(DELETE_BATCH_SIZE)
    .get();

  for (const doc of usernameSnap.docs) {
    await doc.ref.delete();
    deleted++;
  }

  await firestore.collection("user_search").doc(uid).delete();
  deleted++;

  return deleted;
}

async function queueDeletionConfirmationEmail(email: string | undefined): Promise<boolean> {
  if (!email || !accountDeletionEmailEnabled()) return false;

  const ttlAt = admin.firestore.Timestamp.fromDate(
    new Date(Date.now() + MAIL_TTL_DAYS * 24 * 60 * 60 * 1000)
  );

  await getFirestore().collection(MAIL_COLLECTION).add({
    to: [email],
    message: {
      subject: "TalknLearn account deleted",
      text: "Your TalknLearn account and app data have been deleted. If you did not request this, contact support immediately.",
    },
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    ttlAt,
    type: "account_deletion_confirmation",
  });

  return true;
}

export const deleteAccountAndData = onAppCheckCall(
  {
    timeoutSeconds: 540,
    memory: "512MiB",
  },
  async (request) => {
    requireAuth(request.auth);
    const uid = request.auth?.uid;
    if (!uid) {
      throw new HttpsError("unauthenticated", "Login required.");
    }

    let email: string | undefined;
    try {
      const userRecord = await admin.auth().getUser(uid);
      email = userRecord.email;
    } catch (error: any) {
      logger.error("deleteAccountAndData: failed to read Auth user", {
        message: error?.message,
      });
      throw new HttpsError("not-found", "Account not found.");
    }

    try {
      const deletedSubcollectionDocs = await deleteUserSubcollections(uid);
      await deleteProfileDocs(uid);
      const deletedLookupDocs = await deleteUsernameLookupDocs(uid);
      await getFirestore().collection("users").doc(uid).delete();
      await admin.auth().deleteUser(uid);

      let confirmationEmailQueued = false;
      try {
        confirmationEmailQueued = await queueDeletionConfirmationEmail(email);
      } catch (emailError: any) {
        logger.warn("deleteAccountAndData: confirmation email queue failed", {
          message: emailError?.message,
        });
      }

      logger.info("deleteAccountAndData completed", {
        deletedSubcollectionDocs,
        deletedLookupDocs,
        confirmationEmailQueued,
      });

      return {success: true, confirmationEmailQueued};
    } catch (error: any) {
      logger.error("deleteAccountAndData failed", {
        message: error?.message,
      });
      throw new HttpsError("internal", "Account deletion failed. Please try again or contact support.");
    }
  }
);
