# 4. Azure OpenAI for content generation (quizzes, learning sheets)

- **Status:** Accepted
- **Date:** 2025-10-10

## Context

The app generates quiz questions and learning-sheet content from a user's word
bank and translation history. Three options were evaluated:

1. **Hand-curated question banks per language pair** — does not scale to 17 UI
   languages × N target languages and does not personalise.
2. **Gemini / Firebase AI Logic** — strong free tier but limited model choice
   on the free Spark plan at the time of selection, and no first-class
   Cloud-Functions-side moderation hooks.
3. **Azure OpenAI** — student credit available, deterministic SLA, regional
   deployment (HK), and direct pairing with Azure Speech (already used for
   STT/TTS) so credentials and observability live in one Azure subscription.

Latency and cost are dominated by the longest-prompt path (learning sheet
generation), so anything cached at the prompt level (item 35) recovers most
of the spend.

## Decision

All AI generation calls run server-side from Cloud Functions callables. The
Android client never holds an Azure OpenAI key. The Azure OpenAI deployment
name, endpoint, and API version are stored in functions runtime config; the
key is injected via Firebase Functions secrets and rotated yearly per
`docs/SECRETS_ROTATION.md`.

Azure Speech (STT, TTS) is also Azure-hosted but used directly from the
Android client because the SDK requires a streaming microphone and would be
prohibitively expensive to proxy through a callable.

## Consequences

### Positive

- One Azure subscription holds Speech + OpenAI; one rotation drill covers
  both.
- Server-side enforcement means App Check + per-uid rate limits apply
  uniformly via `onAppCheckCall`.
- Switching providers later only touches `fyp-backend/functions/src/`.

### Negative / accepted trade-offs

- Azure Speech credentials *do* ship with the client; mitigated by App Check
  enforcement upstream, region restriction, and the fact that the Speech key
  alone cannot read user data.
- Cost guard (item 34) is a *follow-up* — currently rate limits live in
  `firestore.rules` and per-callable middleware; per-uid daily token budgets
  are not yet enforced.

### Follow-ups

- Item 34: per-uid daily token budget + abuse alerts.
- Item 35: shared translation cache.
