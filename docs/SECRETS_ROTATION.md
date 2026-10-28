# Secrets Rotation Runbook

All Cloud Function secrets are managed via Firebase Functions secrets (backed by Google Cloud Secret Manager). This runbook covers how to rotate each secret safely.

## Prerequisites

- Firebase CLI installed and authenticated (`firebase login`)
- Project selected: `firebase use <project-id>`
- Access to Azure Portal for generating new keys

## Secrets Inventory

| Secret Name              | Service                    | Rotation Frequency |
|--------------------------|----------------------------|--------------------|
| `AZURE_SPEECH_KEY`       | Azure Speech Services      | 90 days            |
| `AZURE_SPEECH_REGION`    | Azure Speech Services      | Rarely changes     |
| `AZURE_TRANSLATOR_KEY`   | Azure Translator API       | 90 days            |
| `AZURE_TRANSLATOR_REGION`| Azure Translator API       | Rarely changes     |
| `GENAI_BASE_URL`         | Azure OpenAI               | On endpoint change |
| `GENAI_API_VERSION`      | Azure OpenAI               | On API upgrade     |
| `GENAI_API_KEY`          | Azure OpenAI               | 90 days            |

## Rotation Steps

### 1. Generate a New Key

- **Azure Speech / Translator**: Azure Portal > Resource > Keys and Endpoint > Regenerate Key 2 (keep Key 1 active during rotation).
- **Azure OpenAI**: Azure Portal > OpenAI Resource > Keys and Endpoint > Regenerate Key 2.

### 2. Set the New Secret

```bash
firebase functions:secrets:set AZURE_SPEECH_KEY
# Paste the new key when prompted
```

### 3. Deploy Functions

```bash
cd fyp-backend/functions
npm run deploy
```

Functions automatically pick up the latest secret version on next cold start.

### 4. Verify

- Test a translation via the app or Firebase console: call `translateText`.
- Test speech token via `getSpeechToken`.
- Test AI generation via `generateLearningContent`.
- Check Cloud Functions logs for errors: `firebase functions:log`.

### 5. Revoke the Old Key

Once verified, go back to Azure Portal and regenerate Key 1 (which was the old active key). This invalidates the old key.

### 6. Record the Rotation

Add an entry to the rotation log below after each completed drill. Use
`.github/ISSUE_TEMPLATE/secret-rotation.md` for the yearly reminder issue.

## Emergency: Secret Compromised

1. **Immediately** regenerate both keys in Azure Portal.
2. Set the new key via `firebase functions:secrets:set <SECRET_NAME>`.
3. Deploy: `npm run deploy`.
4. Check logs for any unauthorized usage during the exposure window.

## Rotation Log

- 2026-05-01 — Rotated: `GENAI_API_KEY` (rotated by operator)
- 2026-03-20 — Rotated: `AZURE_SPEECH_KEY`, `AZURE_TRANSLATOR_KEY`, `GENAI_API_KEY` (rotated by operator)

## Notes

- Region secrets (`AZURE_SPEECH_REGION`, `AZURE_TRANSLATOR_REGION`) rarely need rotation — only update if migrating to a different Azure region.
- `GENAI_BASE_URL` and `GENAI_API_VERSION` are configuration values, not credentials, but are stored as secrets for flexibility.
