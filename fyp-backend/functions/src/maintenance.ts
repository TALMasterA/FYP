/**
 * Scheduled maintenance Cloud Functions.
 *
 * Periodic cleanup tasks: pruning stale FCM tokens and
 * expired rate-limit documents.
 */
import {onSchedule} from "firebase-functions/v2/scheduler";
import * as admin from "firebase-admin";
import {getFirestore} from "./helpers.js";
import {logger} from "./logger.js";

/**
 * Prune FCM tokens older than 60 days to prevent unbounded growth.
 * Runs daily at 3 AM UTC.
 * Users are processed in cursor-based pages of 500 to avoid loading the entire
 * users collection into memory at once.
 */
export const pruneStaleTokens = onSchedule(
  {
    schedule: "0 3 * * *",
    timeZone: "UTC",
    region: "us-central1",
  },
  async () => {
    const SIXTY_DAYS_MS = 60 * 24 * 60 * 60 * 1000;
    const cutoff = admin.firestore.Timestamp.fromDate(
      new Date(Date.now() - SIXTY_DAYS_MS)
    );
    const firestore = getFirestore();
    const PAGE_SIZE = 500;
    let pruned = 0;
    let lastDoc: admin.firestore.QueryDocumentSnapshot | null = null;

    // Paginate through users to avoid loading all at once
    while (true) {
      let query = firestore.collection("users").select().limit(PAGE_SIZE);
      if (lastDoc) query = query.startAfter(lastDoc);

      const usersSnap = await query.get();
      if (usersSnap.empty) break;
      lastDoc = usersSnap.docs[usersSnap.docs.length - 1];

      for (const userDoc of usersSnap.docs) {
        const tokensSnap = await firestore
          .collection("users").doc(userDoc.id)
          .collection("fcm_tokens")
          .where("updatedAt", "<", cutoff)
          .get();

        if (!tokensSnap.empty) {
          const batch = firestore.batch();
          tokensSnap.docs.forEach((doc) => batch.delete(doc.ref));
          await batch.commit();
          pruned += tokensSnap.size;
        }
      }

      if (usersSnap.size < PAGE_SIZE) break;
    }

    logger.info(`pruneStaleTokens: removed ${pruned} stale tokens`);
  }
);

/**
 * Prune rate_limit documents for users who have been inactive for >30 days.
 * Keeps storage costs low by removing one document per inactive user.
 * Runs weekly on Sundays at 4 AM UTC.
 * Uses cursor-based pagination to avoid exceeding the 500-operation batch limit.
 */
export const pruneStaleRateLimits = onSchedule(
  {
    schedule: "0 4 * * 0",
    timeZone: "UTC",
    region: "us-central1",
  },
  async () => {
    const THIRTY_DAYS_MS = 30 * 24 * 60 * 60 * 1000;
    const cutoff = admin.firestore.Timestamp.fromDate(
      new Date(Date.now() - THIRTY_DAYS_MS)
    );
    const firestore = getFirestore();
    const PAGE_SIZE = 500;
    let pruned = 0;
    let lastDoc: admin.firestore.QueryDocumentSnapshot | null = null;

    while (true) {
      let query = firestore
        .collection("rate_limits")
        .where("updatedAt", "<", cutoff)
        .limit(PAGE_SIZE);
      if (lastDoc) query = query.startAfter(lastDoc);

      const snap = await query.get();
      if (snap.empty) break;
      lastDoc = snap.docs[snap.docs.length - 1];

      const batch = firestore.batch();
      snap.docs.forEach((doc) => batch.delete(doc.ref));
      await batch.commit();
      pruned += snap.size;

      if (snap.size < PAGE_SIZE) break;
    }

    if (pruned === 0) {
      logger.info("pruneStaleRateLimits: nothing to prune");
    } else {
      logger.info(`pruneStaleRateLimits: removed ${pruned} stale rate-limit docs`);
    }
  }
);
