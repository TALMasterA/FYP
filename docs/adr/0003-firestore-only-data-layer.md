# 3. Firestore-only data layer (no Room / SQLite)

- **Status:** Accepted
- **Date:** 2025-09-15

## Context

The app's data model — translation history, custom word banks, quiz attempts,
favorites, friends and chats, coin wallet, feedback — is intrinsically
multi-device per user. A user signs in on one phone, runs a quiz on another,
and expects coins, history, and friends to be consistent.

Building a Room cache that mirrors Firestore would have meant writing a
sync engine (conflict resolution, soft-delete tombstones, offline reconnect)
on top of the work Firestore already does for free. The free-tier (Spark)
quotas are sufficient for a project at FYP scale, so cost was not a forcing
function.

## Decision

The Android client talks directly to Firestore for all persistent state, with
Firestore's built-in offline cache providing local availability. There is no
Room database in the app. Per-session UI caches (DataStore, secure prefs)
exist only for non-authoritative state (theme, current UI language, last
selected language pair).

Server-authoritative mutations (coin awards, account deletion) go through
Cloud Functions callables wrapped by `onAppCheckCall`
(`fyp-backend/functions/src/functionWrappers.ts`).

## Consequences

### Positive

- Multi-device consistency comes for free.
- Right-to-erasure (`deleteAccountAndData`) only has to wipe Firestore + Auth,
  not also a Room schema.
- Security rules in `firestore.rules` are the single authoritative access
  policy.

### Negative / accepted trade-offs

- Reads cost network round-trips when the cache is cold; perceived latency on
  the History screen is bounded by Firestore's first sync.
- Firestore composite indexes must be declared in `firestore.indexes.json`
  and kept in sync with new query shapes.
- No SQL — complex aggregations either run on the client or are precomputed
  by a Cloud Function.

### Follow-ups

- Item 35: shared translation cache (`translation_cache/{src}_{tgt}_{hash}`)
  to cut Azure spend without adding a client-side DB.
