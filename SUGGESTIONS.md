# App Improvement Suggestions

> **Status:** Points 1, 2, 3, 5, 6, 10, 11, 12, 14 implemented. Points 4, 7, 8, 9, 13 pending review.  
> Suggestions are ordered from highest impact / easiest to implement to longer-term architectural improvements.

---

## 1. Onboarding & First-Launch Experience ✅ Implemented
**Why:** New users land on the Home screen with no guidance on what the app does or how to start. They may not even realise they need to log in for most features.

- ✅ Add a 3-step swipeable welcome screen (translate, learn, chat with friends) shown only on first launch.
- Add a contextual tooltip / banner on the Home screen pointing to the microphone button with a one-line hint.
- After login, prompt the user to set their primary language and username in a single guided flow instead of burying these in Settings.

---

## 2. Navigation Simplification ✅ Implemented
**Why:** Settings is currently a catch-all hub for Profile, Favorites, Friends, Shop, Voice, Feedback, and System Notes. The hierarchy is not obvious and some important screens (e.g., Friends) are hidden inside Settings.

- ✅ Move Friends and Shared Inbox to the Home screen's main action row or a persistent bottom navigation bar.
- Keep Settings only for app preferences (language, theme, font, notifications).
- ✅ Replace the "Quick Links" card in Settings with a proper bottom navigation bar (Home, Translate, Learn, Friends, Settings) so core screens are always one tap away.

---

## 3. Home Screen Clarity ✅ Implemented
**Why:** The Home screen lists feature cards (Discrete, Continuous, Learning, Word Bank) without enough context for a new user to understand the difference.

- ✅ Add a one-line description beneath each card (already partially done via `HomeDiscreteDescription`, etc.) but make them visible by default instead of collapsible.
- ✅ Rename "Discrete Translation" to "Quick Translate" and "Continuous Conversation" to "Live Conversation" for plain-language clarity.
- ✅ Show a "Welcome back, {name}" greeting when logged in to personalise the screen.

---

## 4. In-App Notification / Badge Improvements
**Why:** Red dot badges on Friends and Shared Inbox are the only signal for new activity. Users may miss important messages.

- Show the unread message count number inside the badge (e.g., "3") rather than just a dot, at least on the Friends button.
- Add a "Mark all as read" shortcut directly on the Friends screen header.
- Surface the most recent unread message as a dismissible banner at the top of the Home screen.

---

## 5. Translation Screen UX ✅ Implemented
**Why:** The speech translation screen requires several taps before anything happens, and error messages are cryptic.

- Auto-detect the spoken language by default so users don't need to set "From" manually.
- Show a real-time waveform animation while recording to confirm the microphone is active.
- Replace raw error codes (e.g., `Azure error: 1007`) with friendly messages (e.g., "No speech detected. Try speaking louder.").
- ✅ Add a "Swap languages" (⇄) button that also swaps the transcript and translation text already on screen.

---

## 6. Learning & Quiz Flow ✅ Partially Implemented
**Why:** The learning sheet generation is opaque — users don't know why content is locked or what the 10-record requirement means until they hit a blocker.

- ✅ Display a progress bar on the Learning screen showing "X / 10 more translations needed to unlock new material".
- After a quiz, show a short summary card ("You scored 8/10 — +8 coins earned!") before returning to the Learning screen.
- Allow users to favourite individual words from the learning sheet directly (not just from History).
- Add a "Practice Mode" that quizzes on favourited words only.

---

## 7. History Screen Improvements
**Why:** The history list is useful but passive. Users cannot act on items easily.

- Allow long-press / swipe-to-reveal actions: Favourite, Copy, Delete — instead of requiring the user to open the item.
- Add a "Share to Friend" shortcut from History items to remove the detour through Friends screen.
- Show the detected source language alongside each entry (it is stored in Firestore but not displayed).

---

## 8. Word Bank Enhancements
**Why:** The Word Bank is a strong differentiator but its purpose is unclear and it requires navigating away from the learning flow to use.

- Surface the Word Bank as a tab within the Learning screen rather than a separate top-level entry.
- Add a "flashcard" swipe mode (know / don't know) as an alternative to reading the list.
- Let users export the Word Bank as a CSV or plain text for use in Anki or other tools.

---

## 9. Friends & Chat UX
**Why:** Chat is hidden three levels deep (Settings → Friends → tap friend → Chat) and lacks basic chat features.

- Show "Online / Last seen" status.
- Add message reactions (emoji) for a quick response without typing.
- Support sending voice messages directly inside the chat (reuse the existing microphone infrastructure).
- Add a "Translate this message" inline option (already partially implemented — make it more discoverable with a long-press menu instead of a separate button).

---

## 10. Performance & Data Efficiency ✅ Partially Implemented
**Why:** Several screens can trigger multiple Firestore reads on navigation, and the app restarts network calls when coming back from background.

- Cache the last 20 chat messages per friend in local Room DB so the chat screen opens instantly even offline.
- ✅ Use Firebase Offline Persistence (enabled in `FYPApplication`) so reads are served from cache when offline and writes are queued until online.
- ✅ Debounce the friend-search query (currently 500 ms) to reduce Firestore reads.
- Combine `pendingFriendRequestCount`, `hasUnreadMessages`, and `hasUnseenSharedItems` into a single Firestore aggregation query instead of three separate listeners where possible.

---

## 11. Accessibility ✅ Partially Implemented
**Why:** Several interactive elements lack proper content descriptions and the app has not been tested with TalkBack.

- ✅ Audit all `Icon` composables and ensure every one has a meaningful `contentDescription`.
- Ensure minimum touch target size (48 dp) for all icon buttons — some badge icons are currently smaller.
- Support system font-size scaling (the app overrides this with its own slider — consider using the system setting as the default).

---

## 12. Database / Firestore Structure ✅ Partially Implemented
**Why:** Some reads are unnecessarily expensive (e.g., reading full chat messages to check unread status).

- Move `totalUnreadMessages` to the top-level `users/{uid}` document as a counter so the Home screen badge only needs one document read instead of a collection query.
- Add a composite index on `chats/{chatId}/messages` for `(receiverId, isRead)` to speed up unread queries on large chat histories.
- ✅ Cap old `fcm_tokens` docs to those updated within the last 60 days — `pruneStaleTokens` Cloud Function runs daily to clean up stale tokens.

---

## 13. Security & Privacy
**Why:** Small improvements reduce attack surface and improve user trust.

- Add a "Privacy Mode" toggle that hides translation history from the screen (useful in public places) — a blurred overlay is sufficient.
- Show the user what personal data is stored and let them download it in one tap (GDPR-friendly).
- Periodically rotate and clean up stale FCM tokens (already partially done in Cloud Functions — extend to prune tokens older than 60 days).

---

## 14. Internationalisation (i18n) Polish ✅ Partially Implemented
**Why:** The app supports UI translation but some hard-coded English strings appear in edge-case error paths.

- ✅ Audit all `errorRaw` strings that are set directly from exception messages (e.g., `e.message ?: "Save failed"`) and replace with `UiTextKey`-driven messages so they get translated. Added `ErrorNotLoggedIn`, `ErrorSaveFailedRetry`, `ErrorLoadFailedRetry`, `ErrorNetworkRetry` keys; replaced raw `e.message` in `removeFriend()`.
- Add Right-to-Left (RTL) layout support for Arabic / Hebrew if those languages are added in the future.

---

*Last updated: 2026-02-24 (Points 1, 2, 3, 5, 6, 10, 11, 12, 14 implemented)*
