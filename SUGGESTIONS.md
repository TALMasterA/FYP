# App Improvement Suggestions

### 17. 🔍 Quiz coin award anti-cheat relies on client-side history count

**Problem:** The coin eligibility check in `CoinEligibility.kt` and `LearningSheetViewModel.kt` compares the current history count against the count at quiz generation time. However, the history count is read client-side. A sophisticated user could manipulate local state or timing to earn undeserved coins.

**Impact:** The existing system is reasonable for most users, but moving the verification to a Cloud Function would make it tamper-proof.

**Suggested fix:** Move the coin award eligibility check to a Firebase Cloud Function that reads the history count server-side before awarding coins.