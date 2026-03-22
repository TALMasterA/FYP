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
import {
  SIXTY_DAYS_MS,
  THIRTY_DAYS_MS,
  FIRESTORE_BATCH_PAGE_SIZE,
  FIRESTORE_BATCH_PAGE_SIZE_SMALL,
} from "./constants.js";

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
    const cutoff = admin.firestore.Timestamp.fromDate(
      new Date(Date.now() - SIXTY_DAYS_MS)
    );
    const firestore = getFirestore();
    let pruned = 0;
    let lastDoc: admin.firestore.QueryDocumentSnapshot | null = null;

    // Paginate through users to avoid loading all at once
    while (true) {
      let query = firestore.collection("users").select().limit(FIRESTORE_BATCH_PAGE_SIZE);
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

      if (usersSnap.size < FIRESTORE_BATCH_PAGE_SIZE) break;
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
    const cutoff = admin.firestore.Timestamp.fromDate(
      new Date(Date.now() - THIRTY_DAYS_MS)
    );
    const firestore = getFirestore();
    let pruned = 0;
    let lastDoc: admin.firestore.QueryDocumentSnapshot | null = null;

    while (true) {
      let query = firestore
        .collection("rate_limits")
        .where("updatedAt", "<", cutoff)
        .limit(FIRESTORE_BATCH_PAGE_SIZE);
      if (lastDoc) query = query.startAfter(lastDoc);

      const snap = await query.get();
      if (snap.empty) break;
      lastDoc = snap.docs[snap.docs.length - 1];

      const batch = firestore.batch();
      snap.docs.forEach((doc) => batch.delete(doc.ref));
      await batch.commit();
      pruned += snap.size;

      if (snap.size < FIRESTORE_BATCH_PAGE_SIZE) break;
    }

    if (pruned === 0) {
      logger.info("pruneStaleRateLimits: nothing to prune");
    } else {
      logger.info(`pruneStaleRateLimits: removed ${pruned} stale rate-limit docs`);
    }
  }
);

/**
 * Repair legacy friend-system data drift:
 * 1) Deletes obsolete friend_requests docs with status=CANCELLED.
 * 2) Deletes expired PENDING friend requests (expiresAt < now).
 * 3) Repairs malformed user_search docs (blank/missing username_lowercase,
 *    or stale discoverability when username is blank).
 *
 * Runs weekly on Sundays at 5 AM UTC.
 */
export const repairFriendsData = onSchedule(
  {
    schedule: "0 5 * * 0",
    timeZone: "UTC",
    region: "us-central1",
  },
  async () => {
    const firestore = getFirestore();
    const now = admin.firestore.Timestamp.fromDate(new Date());

    // ---- 1) Remove obsolete CANCELLED friend request docs ----
    let cancelledDeleted = 0;
    while (true) {
      const cancelledSnap = await firestore
        .collection("friend_requests")
        .where("status", "==", "CANCELLED")
        .limit(FIRESTORE_BATCH_PAGE_SIZE_SMALL)
        .get();

      if (cancelledSnap.empty) break;

      const batch = firestore.batch();
      cancelledSnap.docs.forEach((doc) => batch.delete(doc.ref));
      await batch.commit();
      cancelledDeleted += cancelledSnap.size;

      if (cancelledSnap.size < FIRESTORE_BATCH_PAGE_SIZE_SMALL) break;
    }

    // ---- 2) Delete expired PENDING friend requests (expiresAt < now) ----
    // FIX: Client filters expired requests but server should also clean them up
    // to prevent storage waste and ensure expired requests are not accidentally
    // processed by other functions.
    let expiredDeleted = 0;
    let lastExpiredDoc: admin.firestore.QueryDocumentSnapshot | null = null;

    while (true) {
      let query = firestore
        .collection("friend_requests")
        .where("status", "==", "PENDING")
        .where("expiresAt", "<", now)
        .limit(FIRESTORE_BATCH_PAGE_SIZE_SMALL);
      if (lastExpiredDoc) query = query.startAfter(lastExpiredDoc);

      const expiredSnap = await query.get();
      if (expiredSnap.empty) break;
      lastExpiredDoc = expiredSnap.docs[expiredSnap.docs.length - 1];

      const batch = firestore.batch();
      expiredSnap.docs.forEach((doc) => batch.delete(doc.ref));
      await batch.commit();
      expiredDeleted += expiredSnap.size;

      if (expiredSnap.size < FIRESTORE_BATCH_PAGE_SIZE_SMALL) break;
    }

    // ---- 3) Repair malformed user_search docs ----
    let repairedUserSearchDocs = 0;
    let forcedPrivateProfiles = 0;
    let lastDoc: admin.firestore.QueryDocumentSnapshot | null = null;

    while (true) {
      let query = firestore.collection("user_search").limit(FIRESTORE_BATCH_PAGE_SIZE_SMALL);
      if (lastDoc) query = query.startAfter(lastDoc);

      const snap = await query.get();
      if (snap.empty) break;
      lastDoc = snap.docs[snap.docs.length - 1];

      const batch = firestore.batch();

      for (const doc of snap.docs) {
        const data = doc.data() || {};
        const username = typeof data.username === "string" ? data.username.trim() : "";
        const usernameLower = typeof data.username_lowercase === "string" ? data.username_lowercase : "";
        const isDiscoverable = typeof data.isDiscoverable === "boolean" ? data.isDiscoverable : false;

        const patch: Record<string, unknown> = {};
        let needsPatch = false;

        if (username) {
          const normalizedLower = username.toLowerCase();
          if (usernameLower !== normalizedLower) {
            patch.username_lowercase = normalizedLower;
            needsPatch = true;
          }
        } else {
          // No username => must not be discoverable in search.
          if (isDiscoverable) {
            patch.isDiscoverable = false;
            needsPatch = true;
          }
          if (usernameLower) {
            patch.username_lowercase = admin.firestore.FieldValue.delete();
            needsPatch = true;
          }

          // We only read the public profile for blank-username rows because those
          // are the legacy drift cases where discoverability flags can be inconsistent.
          // Also normalize legacy public profile discoverability flags.
          const publicRef = firestore
            .collection("users").doc(doc.id)
            .collection("profile").doc("public");
          const publicSnap = await publicRef.get();
          if (publicSnap.exists) {
            const publicData = publicSnap.data() || {};
            const profileUsername = typeof publicData.username === "string" ? publicData.username.trim() : "";
            const hasLegacyDiscoverable = Object.prototype.hasOwnProperty.call(publicData, "discoverable");
            const profilePatch: Record<string, unknown> = {};
            let patchProfile = false;

            if (!profileUsername) {
              if (publicData.isDiscoverable !== false) {
                profilePatch.isDiscoverable = false;
                patchProfile = true;
              }
            }

            if (hasLegacyDiscoverable) {
              profilePatch.discoverable = admin.firestore.FieldValue.delete();
              patchProfile = true;
            }

            if (patchProfile) {
              batch.set(publicRef, profilePatch, {merge: true});
              forcedPrivateProfiles++;
            }
          }
        }

        if (needsPatch) {
          batch.set(doc.ref, patch, {merge: true});
          repairedUserSearchDocs++;
        }
      }

      await batch.commit();
      if (snap.size < FIRESTORE_BATCH_PAGE_SIZE_SMALL) break;
    }

    logger.info(
      "repairFriendsData completed",
      {cancelledDeleted, expiredDeleted, repairedUserSearchDocs, forcedPrivateProfiles}
    );
  }
);
