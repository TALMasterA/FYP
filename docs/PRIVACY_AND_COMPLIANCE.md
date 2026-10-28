# Privacy & Compliance

This document records the privacy posture of the FYP Android client and its
Firebase backend. It is the source of truth for data categories, retention
windows, processors, lawful basis, and the right-to-erasure procedure.

## 1. Data Categories Collected

| Category | Source | Storage |
|----------|--------|---------|
| Account identifiers (uid, email, optional displayName) | Firebase Auth | Firebase Auth + `users/{uid}/profile/{info,public}` |
| Username (canonical + display) | User input | `usernames/{canonical}`, `users/{uid}/profile/info` |
| App settings (locale, theme, notification toggles) | User input | `users/{uid}/profile/settings` |
| Learning content (history, word banks, learning sheets, quiz attempts/stats, generated quizzes, favorites, custom words) | User actions | `users/{uid}/{history,word_banks,learning_sheets,quiz_attempts,quiz_stats,generated_quizzes,quiz_versions,favorites,custom_words,…}` |
| Friends graph + chat | User actions | `users/{uid}/friends`, `users/{uid}/shared_inbox`, `users/{uid}/sessions` |
| Coin/award ledger | App-driven | `users/{uid}/coin_awards`, `users/{uid}/last_awarded_quiz`, `users/{uid}/user_stats` |
| FCM push tokens | Device | `users/{uid}/fcm_tokens` (server-side); `EncryptedSharedPreferences` (device cache) |
| Audio capture for speech-to-text | Microphone | Streamed to Azure Speech; not persisted by the app |
| Image capture for OCR | Camera/Gallery | Sent to Azure Computer Vision; not persisted by the app |
| Translation requests | User input | Sent to Azure Translator; cached locally in `TranslationCache` DataStore |
| AI prompts (quiz generation, chat assist) | User input | Sent to Azure OpenAI; not persisted server-side beyond the response |

## 2. Retention Windows

| Asset | Window | Mechanism |
|-------|--------|-----------|
| FCM device token cache (device) | Cleared on signOut/account-deletion | `SessionDataCleaner` + `FcmNotificationService.removeTokenOnSignOut` |
| FCM tokens (server) | 60 days inactive | Cloud Function pruning; see `docs/CLOUD_FUNCTIONS_API.md` |
| Login / password-reset rate-limit counters | 30 days sliding window | `core/security/RateLimiter` (in-memory, LRU-capped) |
| Translation + language-detection caches | Cleared on signOut/account-deletion | `SessionDataCleaner.clearSessionData()` |
| OkHttp HTTP cache (`50 MB`) | Cleared on signOut/account-deletion | `okHttpClient.cache.evictAll()` |
| Word-bank DataStore | Cleared on `AuthState.LoggedOut` | `AppViewModel` → `wordBankCacheDataStore.invalidateAllForUser` |
| Audit logs (`AuditLogger`) | Logcat-only (not persisted) | n/a |
| Firestore subcollection rotation | Until user deletes | manual or via account deletion |

## 3. Processors

| Processor | Purpose | Region |
|-----------|---------|--------|
| Google Firebase (Auth, Firestore, Cloud Functions, FCM, App Check) | Identity, primary data store, push delivery | per Firebase project setting |
| Microsoft Azure Translator | Text translation | configured Azure region |
| Microsoft Azure Speech | Speech-to-text and text-to-speech | configured Azure region |
| Microsoft Azure Computer Vision | OCR text extraction | configured Azure region |
| Microsoft Azure OpenAI | Quiz generation and chat assistance | configured Azure region |

All Azure traffic is brokered through Cloud Functions; the Android client
never holds long-lived Azure keys. See `docs/CLOUD_FUNCTIONS_API.md` for the
callable contract.

## 4. Lawful Basis

- **Performance of contract**: account identifiers, learning content, friends
  graph, settings — required to deliver the requested service.
- **Legitimate interests**: rate-limit counters, audit logs, FCM token
  rotation — required to keep the service secure and reliable.
- **Consent**: speech and image capture are only invoked after the user
  presses an explicit affordance (mic/camera button) and the corresponding OS
  runtime permission has been granted.

## 5. Right to Erasure (Article 17 / 1-tap delete)

The user can erase their account at any time from
`Settings → Profile → Delete My Account & Data`
(`screens/settings/ProfileScreen` →
`ProfileViewModel.deleteAccount`).

The deletion path is enforced by **two regression guards**:

1. `AccountDeletionGuardTest` asserts every user subcollection in the
   server-side `deleteAccountAndData` callable (currently 18 subcollections,
   3 profile docs, 2 top-level lookup groups) is included before Auth deletion.
2. `BackupRulesGuardTest` asserts backup and device-transfer XML excludes still
   cover `secure_prefs.xml`, DataStore caches, and the HTTP cache.
3. `SessionDataCleanerRegistrationTest` asserts the on-device cleanup
   target list (currently `secureStorage`, `okHttpClient`, `translationCache`,
   `languageDetectionCache`) is complete.

Server-side deletion order is: Firestore subcollections → profile docs →
top-level lookup docs → `users/{uid}` → Firebase Auth user. Device-side,
`SessionDataCleaner` runs on logout AND on successful deletion so no stale cache
survives.

Confirmation email is queued after successful Auth deletion only when the
deployment explicitly enables it with `ACCOUNT_DELETION_EMAIL_ENABLED=true` and
has a Firebase Trigger Email-compatible `mail` collection processor configured.
The queued mail document includes `ttlAt`; enable Firestore TTL on that field so
email-delivery metadata is short-lived.

WordBank cleanup is performed separately by `AppViewModel` on
`AuthState.LoggedOut`. The FCM token is revoked by
`FcmNotificationService.removeTokenOnSignOut`.

## 6. Data Protection Officer (DPO) Contact

Replace the placeholder below with the project owner's contact at
deployment time:

```
DPO contact: <add email here>
```

## 7. Change Procedure

If you add a new per-user Firestore subcollection, on-device cache, or
remote processor, you must update **all** of:

1. `fyp-backend/functions/src/accountDeletion.ts` (`deleteAccountAndData` wipe)
2. `AccountDeletionGuardTest.EXPECTED_SUBCOLLECTIONS` (server-side guard)
3. `SessionDataCleaner` constructor + `clearSessionData()` (device wipe)
4. `SessionDataCleanerRegistrationTest.EXPECTED_CLEANUP_DEPS` (device guard)
5. The relevant table in this document
