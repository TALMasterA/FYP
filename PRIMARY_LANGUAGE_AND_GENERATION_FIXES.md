# Bug Fixes: Primary Language Sync & Generation Error Handling

## Issues Fixed

### 1. ✅ Primary Language Not Updating in Profile Column
**Problem**: When user changes primary language in settings, the `primaryLanguage` field in the public profile (`users/{uid}/profile/public`) was not being updated. This means friends couldn't see the updated primary language.

**Root Cause**: 
- `EnsurePublicProfileExistsUseCase` only updated `lastActiveAt` when profile existed
- `SettingsViewModel.updatePrimaryLanguage()` only updated user settings, not the public profile
- The two profile systems (settings and public profile) were not synced

**Solution Implemented**:
1. **Updated `EnsurePublicProfileExistsUseCase`**:
   - Now fetches current primary language from settings on every call
   - Updates public profile with current primary language (not just lastActiveAt)
   - Ensures sync whenever user logs in or app calls this use case

2. **Updated `SettingsViewModel.updatePrimaryLanguage()`**:
   - Now updates BOTH settings and public profile atomically
   - If settings update succeeds, also updates `users/{uid}/profile/public`
   - Fails gracefully if either update fails

**Files Modified**:
- `EnsurePublicProfileExistsUseCase.kt` - Sync primary language on login/session
- `SettingsViewModel.kt` - Sync primary language on explicit change

---

### 2. ✅ Generation "Error: INTERNAL" for Older App Versions
**Problem**: Testers using older app versions encountered "Error: INTERNAL" when trying to generate learning content. This was due to:
- Vague error messages that didn't help users understand the issue
- No version compatibility checking
- Generic error handling that masked the real problem

**Root Cause**:
- Firebase Cloud Functions updated but old app versions expected different response format
- Generic "internal error" message didn't indicate version mismatch
- No validation of secrets or configuration
- Network errors not handled separately from service errors

**Solution Implemented**:

#### A. Enhanced Client-Side Error Handling (`CloudGenAiClient.kt`):
```kotlin
// Now catches specific Firebase Functions error codes:
- UNAUTHENTICATED → "Authentication required. Please log in again."
- PERMISSION_DENIED → "Permission denied. Check account status."
- RESOURCE_EXHAUSTED → "Rate limit exceeded. Wait before trying again."
- DEADLINE_EXCEEDED → "Request timed out. Try again."
- UNAVAILABLE → "Service temporarily unavailable. Try again later."
- INTERNAL → "Server error. Ensure you're using latest app version."
```

**Better empty response handling**:
- Validates response format is correct
- Checks content is not null or empty
- Throws descriptive error instead of returning empty string

#### B. Enhanced Server-Side Error Handling (Cloud Functions):
```typescript
// Added validation:
1. Prompt length validation (max 10,000 chars)
2. Secrets configuration validation
3. Network error handling (separate from API errors)
4. HTTP status code specific messages:
   - 401/403 → Authentication failed
   - 429 → Rate limit exceeded
   - 404 → Model deployment not found (update app)
   - 500+ → Service unavailable
5. JSON parsing error handling
6. Empty content validation

// Better logging:
- Logs deployment name on errors
- Logs error details without exposing secrets
- Logs response preview for debugging
```

**Files Modified**:
- `CloudGenAiClient.kt` - Enhanced error handling with specific error codes
- `functions/src/index.ts` - Improved validation and error messages

---

## Technical Details

### Primary Language Sync Flow

#### On Login/Session Start:
```
1. User logs in
2. AppViewModel calls EnsurePublicProfileExistsUseCase
3. Fetch current settings → Get primaryLanguageCode
4. If profile exists:
   - Update lastActiveAt timestamp
   - Update primaryLanguage to current settings value ← NEW
5. If profile doesn't exist:
   - Create profile with current primaryLanguage
```

#### On Manual Language Change:
```
1. User changes language in Settings
2. SettingsViewModel.updatePrimaryLanguage() called
3. Update settings: setPrimaryLanguage(uid, newCode)
4. Update public profile: friendsRepo.updatePublicProfile(uid, {"primaryLanguage": newCode}) ← NEW
5. Both must succeed for update to be confirmed
```

### Generation Error Handling Flow

#### Client Side:
```
Try:
  1. Call Firebase Function with timeout
  2. Validate response format
  3. Check content exists and not empty
  4. Return content
Catch FirebaseFunctionsException:
  - Map error code to user-friendly message
  - Special message for INTERNAL: "Ensure using latest app version"
Catch Other Exception:
  - Generic error with original message
```

#### Server Side:
```
1. Validate authentication
2. Check rate limit
3. Validate parameters:
   - deployment (required, non-empty)
   - prompt (required, non-empty, max 10K chars)
4. Validate secrets configured
5. Try network call:
   - Catch network errors separately
6. Check HTTP status:
   - 401/403 → Auth error
   - 429 → Rate limit
   - 404 → Deployment not found (version issue!)
   - 500+ → Service unavailable
7. Parse JSON response:
   - Catch parse errors
8. Validate content exists:
   - Throw if empty
9. Return content
```

---

## Deployment Instructions

### Step 1: Deploy Backend Changes
```bash
cd D:\FYP\fyp-backend
firebase deploy --only functions
```

**This will deploy**:
- Enhanced `generateLearningContent` function with better error handling
- More detailed error messages for users
- Validation for secrets and configuration

### Step 2: Rebuild Android App
```bash
cd D:\FYP
gradlew clean
gradlew assembleDebug
```

### Step 3: Test Both Devices
Install new APK on both test devices.

---

## Testing Checklist

### ✅ Test 1: Primary Language Updates in Profile
**Setup**: One test account

1. Log in to account
2. Go to Settings → Change primary language (e.g., en-US → ja-JP)
3. Go to Settings → My Profile
4. **Expected**: Primary Language shows "ja-JP" (or Japanese)
5. Log out and log back in
6. Go to My Profile
7. **Expected**: Still shows "ja-JP"

**Verification**:
- Check Firestore Console: `users/{uid}/profile/public`
- Field `primaryLanguage` should be "ja-JP"

### ✅ Test 2: Friends Can See Updated Language
**Setup**: Two accounts (A and B) that are friends

1. Account A changes primary language to "ko-KR"
2. Account B views Account A's profile (if implemented)
3. **Expected**: Shows Account A's primary language as "ko-KR"

### ✅ Test 3: Generation Error Messages
**Scenario 1: Network Error**
1. Turn off WiFi/data
2. Try to generate learning content
3. **Expected**: "Unable to reach AI service. Check internet connection."

**Scenario 2: Rate Limit**
1. Generate content 10+ times in 1 hour
2. Try again
3. **Expected**: "Rate limit exceeded. Please wait before trying again."

**Scenario 3: Service Unavailable (Simulate)**
1. Try generation when backend is down
2. **Expected**: Better error message, not generic "INTERNAL"

### ✅ Test 4: Successful Generation
1. Ensure using latest app version
2. Generate learning content
3. **Expected**: Content generates successfully
4. No "INTERNAL" errors

### ✅ Test 5: Old App Version (If possible)
1. Install old APK on one device
2. Try to generate content
3. **Expected**: Error message mentions "update app to latest version"
   (If using INTERNAL error code, should suggest updating)

---

## Error Messages Reference

### Client-Side Error Codes

| Firebase Error Code | User-Friendly Message |
|---------------------|----------------------|
| UNAUTHENTICATED | "Authentication required. Please log in again." |
| PERMISSION_DENIED | "Permission denied. Please check your account status." |
| RESOURCE_EXHAUSTED | "Rate limit exceeded. Please wait before trying again." |
| DEADLINE_EXCEEDED | "Request timed out. The generation took too long. Please try again." |
| UNAVAILABLE | "Service temporarily unavailable. Please try again later." |
| INTERNAL | "Server error occurred. Please ensure you're using the latest app version and try again." |
| Other | "Generation failed: {original error message}" |

### Server-Side HTTP Status Codes

| Status Code | Error Type | Message |
|-------------|-----------|---------|
| 401/403 | Authentication | "AI service authentication failed. Please contact support." |
| 404 | Not Found | "AI model deployment not found. Please update your app to the latest version." |
| 429 | Rate Limit | "AI service rate limit exceeded. Please try again in a few minutes." |
| 500+ | Server Error | "AI service is temporarily unavailable. Please try again later." |
| Network Error | Network | "Unable to reach AI service. Please check your internet connection and try again." |
| Parse Error | Internal | "Invalid response from AI service. Please try again." |
| Empty Content | Internal | "No content generated. Please try again." |

---

## Database Schema

### Public Profile Structure
**Location**: `users/{uid}/profile/public`

```javascript
{
  "uid": "user123abc...",
  "username": "johndoe",
  "displayName": "",
  "avatarUrl": "",
  "primaryLanguage": "en-US",  // ← NOW SYNCS WITH SETTINGS
  "learningLanguages": [],
  "isDiscoverable": true,
  "createdAt": Timestamp,
  "lastActiveAt": Timestamp    // ← ALSO UPDATED ON LOGIN
}
```

**Sync Points**:
1. **On Login**: Both `primaryLanguage` and `lastActiveAt` updated
2. **On Language Change**: `primaryLanguage` updated immediately
3. **On Profile Creation**: Initialized with current settings

### User Settings Structure
**Location**: `users/{uid}/profile/settings`

```javascript
{
  "primaryLanguageCode": "en-US",  // ← SOURCE OF TRUTH
  "fontSizeScale": 1.0,
  "themeMode": "system",
  // ... other settings
}
```

---

## Troubleshooting

### Issue: Primary language still not updating
**Solutions**:
1. Log out and log back in (triggers sync)
2. Check Firestore rules allow updating `users/{uid}/profile/public`
3. Check Logcat for errors in `SettingsViewModel`
4. Verify `FriendsRepository` is injected correctly

### Issue: Still seeing "INTERNAL" errors
**Solutions**:
1. **CRITICAL**: Deploy backend functions: `firebase deploy --only functions`
2. Verify deployment succeeded: Check Firebase Console → Functions
3. Check function logs: Firebase Console → Functions → Logs
4. Ensure secrets are configured: `firebase functions:config:get`
5. Update app to latest version (rebuild and reinstall)

### Issue: Empty content generated
**Solutions**:
1. Check prompt is not too long (max 10,000 chars)
2. Check prompt is valid and not empty
3. Check deployment name is correct
4. Check Azure OpenAI service is accessible
5. Review function logs for Azure API errors

### Issue: Rate limit errors
**Solutions**:
1. Wait 1 hour (rate limit window)
2. Check `rate_limits/{uid}` document in Firestore
3. Clear old timestamps if needed (manually in console)
4. Increase `RATE_LIMIT_MAX_REQUESTS` in functions if needed

---

## Migration Notes

### For Existing Users:
- Public profiles created before this fix will have outdated primary language
- **Automatic fix**: Next time they log in, profile will sync
- **Manual fix**: They can change language in settings, which will trigger sync

### For New Users:
- Profile created on first login with correct primary language
- Subsequent logins keep it in sync
- Language changes immediately reflected in profile

---

## Code Changes Summary

### Files Modified (3):
1. **`EnsurePublicProfileExistsUseCase.kt`**
   - Added primary language sync on profile update
   - Fetches settings on every invocation

2. **`SettingsViewModel.kt`**
   - Added `FriendsRepository` injection
   - Updates public profile when language changes
   - Atomic update of both settings and profile

3. **`CloudGenAiClient.kt`**
   - Enhanced error handling with specific error codes
   - Validates response format and content
   - User-friendly error messages

4. **`functions/src/index.ts`**
   - Comprehensive validation (prompt, secrets, config)
   - Network error handling
   - HTTP status code specific messages
   - Empty content validation
   - Better logging for debugging

**Total**: 4 files modified

---

## Known Limitations

1. **Retroactive Sync**: 
   - Old profiles won't update until next login
   - Consider adding migration script if needed

2. **Network Errors**:
   - If profile update fails, settings still updated
   - May cause temporary desync (fixed on next login)

3. **Error Granularity**:
   - Some Azure API errors still map to generic "unavailable"
   - Could add more specific Azure error code mapping

---

## Future Improvements

1. **Real-time Sync**:
   - Add Cloud Function trigger on settings change
   - Auto-sync public profile when settings updated

2. **Version Checking**:
   - Add app version field to requests
   - Reject old versions with clear upgrade message

3. **Better Offline Handling**:
   - Queue profile updates when offline
   - Sync when back online

4. **Migration Script**:
   - Batch update all existing profiles
   - Sync primary language from settings

---

**Created**: 2026-02-19
**Status**: ✅ Complete - Ready for Deployment
**Priority**: HIGH (Deploy backend first, then rebuild app)

**Deployment Order**:
1. Deploy backend functions (MUST DO FIRST)
2. Rebuild and install app
3. Test on both devices

