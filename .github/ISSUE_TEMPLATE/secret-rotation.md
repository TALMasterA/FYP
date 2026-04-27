---
name: Secret rotation drill
about: Yearly reminder to rotate Firebase Functions and Azure-backed secrets
title: "Secret rotation drill - YYYY"
labels: security, maintenance
assignees: ""
---

## Scope

Rotate and verify every credential listed in `docs/SECRETS_ROTATION.md`.

## Checklist

- [ ] Rotate `AZURE_SPEECH_KEY`
- [ ] Rotate `AZURE_TRANSLATOR_KEY`
- [ ] Rotate `GENAI_API_KEY`
- [ ] Confirm region/config secrets are still correct
- [ ] Deploy Cloud Functions
- [ ] Verify `getSpeechToken`, `translateText`, and `generateLearningContent`
- [ ] Update `docs/SECRETS_ROTATION.md` rotation log
