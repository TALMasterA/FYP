# Cloud Functions API Reference

Callable functions are Firebase Cloud Functions v2 (`onCall`) and currently use the platform default region (`us-central1`) because no explicit callable region is configured in code.
The backend also exposes an HTTP readiness endpoint (`onRequest`) for deployment checks.
Notification triggers and scheduled jobs are explicitly configured to run in `us-central1`.

---

## Translation & Speech

### `getSpeechToken`
Returns an Azure Speech SDK token for client-side speech recognition.

**Auth:** Required
**Request:** _(no parameters)_
**Response:**
```json
{ "token": "string", "region": "string" }
```

---

### `translateText`
Translates a single text string.

**Auth:** Required
**Request:**
| Field  | Type   | Required | Description                |
|--------|--------|----------|----------------------------|
| `text` | string | yes      | Text to translate (max 5000 chars) |
| `to`   | string | yes      | Target language code       |
| `from` | string | no       | Source language code (auto-detect if omitted) |

`from`/`to` must use supported app language codes (BCP-47 style, e.g. `en-US`).
Backward compatibility currently accepts legacy `en` and normalizes it to `en-US`.

**Response:**
```json
{ "translatedText": "string" }
```

When `from` is omitted (auto-detect mode), the response also includes:
```json
{
  "translatedText": "string",
  "detectedLanguage": {
    "language": "en",
    "score": 0.98
  }
}
```

**Error Codes:**
- `invalid-argument` — Invalid/missing language parameters or invalid upstream request
- `resource-exhausted` — Azure Translator rate limit exceeded (HTTP 429)
- `unavailable` — Upstream service/network temporarily unavailable
- `failed-precondition` — Translator service authentication/configuration issue

---

### `translateTexts`
Batch-translates multiple texts in a single API call. Server internally chunks into 100-element Azure API requests.

**Auth:** Not required (guest access allowed with stricter rate limit)
**Rate Limit:**
- Authenticated users: 20 requests per 10 minutes (per user)
- Guests: 1 request per hour (per IP address)

**Request:**
| Field   | Type     | Required | Description                  |
|---------|----------|----------|------------------------------|
| `texts` | string[] | yes      | Array of texts (max 800)     |
| `to`    | string   | yes      | Target language code         |
| `from`  | string   | no       | Source language code         |

`from`/`to` use the same validation and backward-compatibility normalization as `translateText`.

**Response:**
```json
{ "translatedTexts": ["string", "string", ...] }
```

**Error Codes:**
- `invalid-argument` — Invalid/missing language parameters or payload too large
- `resource-exhausted` — Server-side rate limit exceeded **or** Azure Translator rate limit (HTTP 429)
- `unavailable` — Upstream service/network temporarily unavailable
- `failed-precondition` — Translator service authentication/configuration issue

---

### `detectLanguage`
Detects the language of a given text.

**Auth:** Required
**Request:**
| Field  | Type   | Required | Description            |
|--------|--------|----------|------------------------|
| `text` | string | yes      | Text to detect         |

**Response:**
```json
{
  "language": "string",
  "score": 0.99,
  "isTranslationSupported": true,
  "alternatives": [{ "language": "string", "score": 0.5 }]
}
```

**Error Codes:**
- `unavailable` — Upstream service/network temporarily unavailable
- `failed-precondition` — Translator service authentication/configuration issue
- `invalid-argument` — Invalid upstream request

---

## AI Learning Content

### `generateLearningContent`
Generates AI-powered learning content via Azure OpenAI.

**Auth:** Required
**Rate Limit:** 10 requests per hour per user
**Timeout:** 300 seconds
**Request:**
| Field        | Type   | Required | Description                 |
|--------------|--------|----------|-----------------------------|
| `deployment` | string | yes      | Azure OpenAI deployment name |
| `prompt`     | string | yes      | The generation prompt        |

**Response:**
```json
{ "content": "string" }
```

**Error Codes:**
- `resource-exhausted` — Rate limit exceeded
- `failed-precondition` — Secrets not configured or auth failed
- `not-found` — Deployment not found
- `unavailable` — Service temporarily unavailable

**Operational Notes:**
- GenAI config is validated before request execution (required values, HTTPS base URL, and API version format).
- Rate-limit checks are fail-closed for Firestore read errors and malformed stored rate-limit payloads.

---

## Health & Readiness

### `healthcheck` (HTTP)
Readiness endpoint for backend configuration validation.

**Type:** HTTP function (`onRequest`)
**Method:** `GET`
**Auth:** Not required

**Success Response (200):**
```json
{
  "status": "ok",
  "timestamp": "2026-03-20T12:34:56.000Z",
  "checks": {
    "genAiConfig": "ok"
  }
}
```

**Failure Response (500):**
```json
{
  "status": "error",
  "timestamp": "2026-03-20T12:34:56.000Z",
  "checks": {
    "genAiConfig": "error"
  },
  "message": "Service is misconfigured"
}
```

**Invalid Method (405):**
```json
{
  "status": "error",
  "message": "Method not allowed"
}
```

---

## Coins & Shop

### `awardQuizCoins`
Awards coins for a quiz attempt with server-side anti-cheat verification.

**Auth:** Required
**Request:**
| Field                            | Type   | Required | Description                     |
|----------------------------------|--------|----------|---------------------------------|
| `attemptId`                      | string | yes      | Unique attempt identifier       |
| `primaryLanguageCode`            | string | yes      | Source language (`xx` or `xx-XX`, e.g. `en`, `en-US`) |
| `targetLanguageCode`             | string | yes      | Target language (e.g. "zh-HK") |
| `generatedHistoryCountAtGenerate`| number | yes      | History count at quiz generation |
| `totalScore`                     | number | yes      | Score (max 50)                  |

**Response:**
```json
{ "awarded": true, "coinsAwarded": 10, "newTotal": 150 }
```
or
```json
{ "awarded": false, "reason": "already_awarded" }
```

**Rejection reasons:** `zero_score`, `already_awarded`, `no_quiz_version`, `invalid_quiz_version`, `version_mismatch`, `insufficient_records`

---

### `spendCoins`
Processes a shop purchase using coins.

**Auth:** Required
**Request (history_expansion):**
| Field          | Type   | Required | Description             |
|----------------|--------|----------|-------------------------|
| `purchaseType` | string | yes      | `"history_expansion"`   |

**Request (palette_unlock):**
| Field          | Type   | Required | Description                                      |
|----------------|--------|----------|--------------------------------------------------|
| `purchaseType` | string | yes      | `"palette_unlock"`                                |
| `paletteId`    | string | yes      | One of: ocean, sunset, lavender, rose, mint, crimson, amber, indigo, emerald, coral |

**Response:**
```json
{ "success": true, "newBalance": 990 }
```
or
```json
{ "success": false, "reason": "insufficient_coins" }
```

---

## Push Notifications (Firestore Triggers)

These are not callable functions — they fire automatically on Firestore document events.

| Function                           | Trigger                                          | Description                        |
|------------------------------------|--------------------------------------------------|------------------------------------|
| `sendChatNotification`             | `chats/{chatId}/messages/{messageId}` created    | Notifies recipient of new message; enforces 10/min rate limit (notification suppressed on exceed) |
| `sendFriendRequestNotification`    | `friend_requests/{requestId}` created            | Notifies recipient of new request; enforces 5/hour rate limit (request deleted on exceed) |
| `sendRequestAcceptedNotification`  | `friend_requests/{requestId}` updated            | Notifies sender when request accepted |
| `sendSharedInboxNotification`      | `users/{userId}/shared_inbox/{itemId}` created   | Notifies recipient of new shared item |
| `enforceFeedbackRateLimit`         | `feedback/{feedbackId}` created                  | Enforces 3/hour feedback rate limit (feedback deleted on exceed) |
| `syncQuizVersionFromLearningSheet` | `learning_sheets/{sheetId}` created/updated      | Syncs quiz version for anti-cheat validation |

---

## Scheduled Maintenance

| Function               | Schedule              | Description                              |
|------------------------|-----------------------|------------------------------------------|
| `pruneStaleTokens`     | Daily at 03:00 UTC    | Removes FCM tokens older than 60 days    |
| `pruneStaleRateLimits` | Sundays at 04:00 UTC  | Removes rate-limit docs inactive >30 days |
| `repairFriendsData`    | Sundays at 05:00 UTC  | Deletes legacy `CANCELLED` friend requests and repairs malformed `user_search` / legacy profile discoverability fields |

---

## Friend System Backend Contract

Friend operations (send/accept/reject/cancel/remove) are currently **Firestore-driven** from the Android app, not callable Cloud Functions.

- Unfriend deletes two mirror documents in one client batch:
  - `users/{currentUserId}/friends/{friendId}`
  - `users/{friendId}/friends/{currentUserId}`
- Firestore rules explicitly allow counterpart `create` (accept flow) and counterpart `delete` (reciprocal unfriend cleanup), while keeping arbitrary field updates owner-only.

This is the current production contract until/unless a dedicated callable friend-management API is introduced.

---

## Language Code Mapping

The backend normalises app language codes to Azure Translator API codes:

| App Code | Azure Code |
|----------|------------|
| `zh-HK`  | `yue`      |
| `zh-TW`  | `zh-Hant`  |
| `zh-CN`  | `zh-Hans`  |
| Others   | Pass-through |
