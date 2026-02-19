# Friends Feature Bug Fixes - Deployment Guide

## Issues Fixed

### 1. **Firestore Permission Errors (PERMISSION_DENIED)**
**Problem**: The app crashed with `PERMISSION_DENIED: Missing or insufficient permissions` when accessing friends data.

**Root Cause**: The Firestore security rules didn't allow authenticated users to read public profiles of other users.

**Fix Applied**: Updated `fyp-backend/firestore.rules` to add specific rules for:
- Reading public profiles: `users/{userId}/profile/public`
- Reading friends lists: `users/{userId}/friends/{friendId}`

**Action Required**: 
```bash
cd fyp-backend
firebase deploy --only firestore:rules
```

### 2. **MyProfile Shows "Profile not found"**
**Problem**: The MyProfile screen displayed "Profile not found" error for all users.

**Root Cause**: The public profile document (`users/{userId}/profile/public`) was never created when users logged in or registered.

**Fix Applied**: 
- Created `EnsurePublicProfileExistsUseCase.kt` - A use case that initializes the public profile
- Created `AppViewModel.kt` - Application-level ViewModel that monitors auth state
- Integrated into `AppNavigation.kt` - Automatically runs when users log in

**How It Works**:
1. When a user logs in, `AppViewModel` detects the auth state change
2. It calls `EnsurePublicProfileExistsUseCase` to check if public profile exists
3. If not, creates a default profile with user's primary language
4. If exists, updates the `lastActiveAt` timestamp

### 3. **Friends Button Crash**
**Problem**: Clicking the Friends button in Settings caused the app to crash.

**Root Cause**: Same as issue #1 - permission errors when trying to load friends data.

**Fix Applied**: Same Firestore rules update as issue #1.

## Files Modified

### Backend (Requires Deployment)
1. `fyp-backend/firestore.rules` - Added security rules for friends feature

### Android App
1. `app/src/main/java/com/example/fyp/domain/friends/EnsurePublicProfileExistsUseCase.kt` - **NEW**
2. `app/src/main/java/com/example/fyp/AppViewModel.kt` - **NEW**
3. `app/src/main/java/com/example/fyp/AppNavigation.kt` - Modified to integrate AppViewModel

## Deployment Steps

### Step 1: Deploy Firestore Rules
```bash
cd D:\FYP\fyp-backend
firebase deploy --only firestore:rules
```

**Verification**: Check Firebase Console > Firestore Database > Rules to confirm new rules are active.

### Step 2: Rebuild and Reinstall Android App
```bash
cd D:\FYP
gradlew clean
gradlew assembleDebug
```

Then reinstall the app on your device/emulator.

### Step 3: Test the Fixes

#### Test Case 1: MyProfile Screen
1. Log in to the app
2. Go to Settings → My Profile
3. **Expected**: Profile displays with User ID, username (empty initially), and primary language
4. **Previous**: "Profile not found" error

#### Test Case 2: Friends Button
1. Log in to the app
2. Go to Settings → Friends
3. **Expected**: Friends screen opens (may be empty if no friends)
4. **Previous**: App crashed

#### Test Case 3: Profile Auto-Creation
1. Create a new user account or use an existing one
2. Log in
3. Navigate to MyProfile
4. **Expected**: Profile is automatically created and displayed
5. Check Firestore Console: `users/{userId}/profile/public` document should exist

## Technical Details

### Firestore Security Rules Changes

**Added Rules:**
```javascript
// Allow authenticated users to read public profiles
match /users/{userId}/profile/public {
  allow read: if request.auth != null;
  allow write: if request.auth != null && request.auth.uid == userId;
}

// Allow users to read their own friends list
match /users/{userId}/friends/{friendId} {
  allow read: if request.auth != null && request.auth.uid == userId;
  allow write: if request.auth != null && request.auth.uid == userId;
}
```

### Public Profile Structure
Location: `users/{userId}/profile/public`
```javascript
{
  "uid": "user123",
  "username": "",  // Empty until user sets it
  "displayName": "",  // Empty until user sets it
  "avatarUrl": "",
  "primaryLanguage": "en-US",
  "learningLanguages": [],
  "isDiscoverable": true,
  "createdAt": Timestamp,
  "lastActiveAt": Timestamp
}
```

## Known Limitations

1. **Username not set by default**: Users will need to manually set their username in the profile settings (feature to be implemented).
2. **Display name empty**: Until the user updates their profile, the display name will be empty.
3. **No profile picture**: Avatar URL is empty by default (image upload feature to be implemented).

## Other Logcat Warnings (Non-Critical)

The following errors in the logcat are **NOT critical** and don't need fixing:

1. `VerityUtils: Failed to measure fs-verity` - System-level warning, not app-related
2. `No package ID 6a found for ID 0x6a0b0013` - System resources issue, not app-related
3. `BufferQueueProducer: Unable to open libpenguin.so` - Samsung-specific library, not app-related
4. `GoogleApiManager: Failed to get service from broker` - Google Play Services issue in emulator
5. `FirebaseAppDistribution: Failed to identify release` - App Distribution feature (not in production)
6. `WindowManager: destroySurfaces` - Normal window lifecycle events
7. `FreecessController: FZ error` - Samsung power management, not app-related

## Troubleshooting

### If MyProfile still shows "Profile not found":
1. Verify Firestore rules are deployed
2. Check Firestore Console for the profile document
3. Log out and log back in to trigger profile creation
4. Check Android Studio Logcat for any error messages from `AppViewModel`

### If Friends button still crashes:
1. Verify Firestore rules are deployed
2. Check that the error message changed (should no longer be PERMISSION_DENIED)
3. Review Logcat for new error messages

### If profile creation fails:
1. Check that user has internet connection
2. Verify Firebase is initialized properly
3. Check Logcat for exceptions from `EnsurePublicProfileExistsUseCase`

## Next Steps (Future Enhancements)

1. **Username Setup Screen**: Add a flow for new users to set their username
2. **Profile Completion**: Encourage users to complete their profile (display name, avatar)
3. **Error Handling**: Add retry logic and user-friendly error messages
4. **Profile Caching**: Cache public profiles to reduce Firestore reads
5. **Background Sync**: Update `lastActiveAt` periodically in the background

## Summary

The main issue was **missing Firestore security rules** and **missing profile initialization logic**. After deploying the updated Firestore rules and reinstalling the app with the new code:

✅ Friends feature will work without permission errors
✅ MyProfile screen will display user information
✅ Profile will be automatically created on first login

**Critical Action**: Deploy the Firestore rules to fix the permission errors!

