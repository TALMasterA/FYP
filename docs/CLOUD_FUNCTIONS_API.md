# Cloud Functions API Reference

All functions are Firebase Cloud Functions v2 (`onCall`) deployed to `asia-east1` (default).
Notification triggers and scheduled jobs run in `us-central1`.

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

---

### `translateTexts`
Batch-translates multiple texts in a single API call.

**Auth:** Not required (used for guest UI language switching)
**Request:**
| Field   | Type     | Required | Description                  |
|---------|----------|----------|------------------------------|
| `texts` | string[] | yes      | Array of texts (max 800)     |
| `to`    | string   | yes      | Target language code         |
| `from`  | string   | no       | Source language code         |

**Response:**
```json
{ "translatedTexts": ["string", "string", ...] }
```

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

---

## Coins & Shop

### `awardQuizCoins`
Awards coins for a quiz attempt with server-side anti-cheat verification.

**Auth:** Required
**Request:**
| Field                            | Type   | Required | Description                     |
|----------------------------------|--------|----------|---------------------------------|
| `attemptId`                      | string | yes      | Unique attempt identifier       |
| `primaryLanguageCode`            | string | yes      | Source language (e.g. "en")     |
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

**Rejection reasons:** `zero_score`, `already_awarded`, `no_sheet`, `invalid_sheet`, `version_mismatch`, `insufficient_records`

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
| `sendChatNotification`             | `chats/{chatId}/messages/{messageId}` created    | Notifies recipient of new message  |
| `sendFriendRequestNotification`    | `friend_requests/{requestId}` created            | Notifies recipient of new request; enforces 3/hour rate limit |
| `sendRequestAcceptedNotification`  | `friend_requests/{requestId}` updated            | Notifies sender when request accepted |
| `sendSharedInboxNotification`      | `users/{userId}/shared_inbox/{itemId}` created   | Notifies recipient of new shared item |

---

## Scheduled Maintenance

| Function               | Schedule              | Description                              |
|------------------------|-----------------------|------------------------------------------|
| `pruneStaleTokens`     | Daily at 03:00 UTC    | Removes FCM tokens older than 60 days    |
| `pruneStaleRateLimits` | Sundays at 04:00 UTC  | Removes rate-limit docs inactive >30 days |

---

## Language Code Mapping

The backend normalises app language codes to Azure Translator API codes:

| App Code | Azure Code |
|----------|------------|
| `zh-HK`  | `yue`      |
| `zh-TW`  | `zh-Hant`  |
| `zh-CN`  | `zh-Hans`  |
| Others   | Pass-through |
