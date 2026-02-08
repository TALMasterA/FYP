# Troubleshooting

Common issues and solutions for the FYP Translation & Learning App.

---

## �� Setup & Installation Issues

### Issue: Cannot Find google-services.json

**Problem**: Build fails with "File google-services.json is missing"

**Solution**:
1. Download `google-services.json` from Firebase Console
2. Place file in `app/` directory (NOT `app/src/`)
3. Sync Gradle: File → Sync Project with Gradle Files

**Correct File Location**:
```
FYP/
└── app/
    └── google-services.json  ← HERE
```

---

### Issue: Gradle Sync Failed

**Problem**: "Gradle sync failed" error in Android Studio

**Solutions**:

1. **Check Internet Connection**: Gradle downloads dependencies

2. **Invalidate Caches**:
   - File → Invalidate Caches / Restart
   - Select "Invalidate and Restart"

3. **Update Gradle Wrapper**:
   ```bash
   ./gradlew wrapper --gradle-version=8.2
   ```

4. **Clean and Rebuild**:
   - Build → Clean Project
   - Build → Rebuild Project

5. **Check JDK Version**:
   - File → Project Structure → SDK Location
   - Ensure JDK 11+ is selected

---

### Issue: SDK Location Not Found

**Problem**: "SDK location not found" error

**Solution**:  
Create `local.properties` in project root:

```properties
sdk.dir=/path/to/Android/Sdk
```

**Finding SDK Path**:
- Windows: `C:\Users\<username>\AppData\Local\Android\Sdk`
- Mac: `/Users/<username>/Library/Android/sdk`
- Linux: `/home/<username>/Android/Sdk`

Or in Android Studio: File → Project Structure → SDK Location

---

### Issue: Build Errors After Cloning

**Problem**: Multiple build errors after git clone

**Solutions**:

1. **Ensure Prerequisites**: Android Studio, JDK 11+, Android SDK
2. **Add google-services.json** (see above)
3. **Sync Gradle**: File → Sync Project with Gradle Files
4. **Clean Build**:
   ```bash
   ./gradlew clean build
   ```

---

## 📱 Runtime Issues

### Issue: App Crashes on Startup

**Problem**: App crashes immediately when opening

**Debugging Steps**:

1. **Check Logcat**:
   - View → Tool Windows → Logcat
   - Look for red error messages
   - Common causes: Missing Firebase config, network issues

2. **Clear App Data**:
   - Settings → Apps → FYP → Storage → Clear Data
   - Reinstall app

3. **Check Firebase Connection**:
   - Verify Firebase project is active
   - Check Firestore rules allow read/write
   - Ensure Authentication is enabled

---

### Issue: Microphone Not Working

**Problem**: Cannot record voice input

**Solutions**:

1. **Check Permissions**:
   - Settings → Apps → FYP → Permissions
   - Ensure "Microphone" is allowed

2. **Request Permission in App**:
   - App will prompt on first use
   - If denied, manually enable in settings

3. **Test Microphone**:
   - Use another app (Voice Recorder) to test
   - If other apps work, it's an app issue

4. **Restart App**: Sometimes fixes recognition issues

**Fallback**: Use text input instead

---

### Issue: Translation Not Appearing

**Problem**: Translation request completes but no result shown

**Causes & Solutions**:

1. **No Internet Connection**:
   - Check WiFi/data connection
   - API calls require internet

2. **Firebase Not Connected**:
   - Check Firebase Console for project status
   - Verify Cloud Functions are deployed

3. **API Quota Exceeded**:
   - Check Firebase Console → Functions → Usage
   - May need to upgrade plan

4. **Invalid Language Code**:
   - Ensure selected languages are supported

---

### Issue: Voice Playback Not Working

**Problem**: Text-to-speech not playing audio

**Solutions**:

1. **Check Device Volume**: Ensure media volume is up
2. **Check Permissions**: May need storage permission
3. **Restart App**: Fixes most TTS issues
4. **Azure Token Expired**: Token valid for 10 minutes, app auto-refreshes
5. **Language Not Supported**: Check Azure Speech supported languages

---

### Issue: App Lag or Freezing

**Problem**: App becomes slow or unresponsive

**Solutions**:

1. **Restart App**: First try
2. **Clear Cache**:
   - Settings → Apps → FYP → Storage → Clear Cache
   - (NOT Clear Data, which deletes settings)
3. **Check Network**: Slow internet causes UI delays
4. **Reduce History**: Delete old translation records
5. **Update App**: Ensure latest version

---

## 🔥 Firebase Issues

### Issue: Authentication Failed

**Problem**: Cannot log in or sign up

**Solutions**:

1. **Check Email Format**: Must be valid email
2. **Password Requirements**: Minimum 6 characters
3. **Firebase Auth Enabled**:
   - Firebase Console → Authentication
   - Ensure Email/Password provider is enabled
4. **Network Issues**: Requires internet
5. **Account Already Exists**: Try "Reset Password"

---

### Issue: Firestore Permission Denied

**Problem**: "Permission denied" when reading/writing data

**Solutions**:

1. **Check Authentication**: Must be logged in for most operations
2. **Review Firestore Rules**:
   - Firebase Console → Firestore → Rules
   - Ensure rules allow authenticated users

**Example Rules**:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null 
                        && request.auth.uid == userId;
    }
  }
}
```

---

### Issue: Cloud Functions Timeout

**Problem**: Function call times out or takes too long

**Causes**:
- Learning material generation (can take 30-60 seconds)
- Quiz generation (can take 30-60 seconds)
- Network issues

**Solutions**:

1. **Wait Longer**: AI generation takes time (up to 2 minutes)
2. **Check Internet**: Slow connection = slow function
3. **Retry**: Temporary Azure API issues
4. **Check Firebase Console**: Functions → Logs for errors

---

## 💾 Data Issues

### Issue: Translation History Not Syncing

**Problem**: History not appearing or not syncing across devices

**Solutions**:

1. **Check Login Status**:
   - Guest mode data is local only
   - Must be logged in for cloud sync

2. **Wait for Sync**: May take few seconds after login

3. **Check Firestore Connection**:
   - Firebase Console → Firestore
   - Verify data exists in `users/{uid}/history`

4. **Clear and Re-sync**: Logout and login again

---

### Issue: Favorites Not Saving

**Problem**: Favorited items not persisting

**Solutions**:

1. **Check Authentication**: Must be logged in
2. **Network Connection**: Requires internet to save
3. **Firestore Rules**: Ensure favorites collection is writable
4. **Check for Errors**: View Logcat for error messages

---

### Issue: Learning Sheets Won't Generate

**Problem**: "Generate Learning Material" doesn't work

**Requirements**:
- Must be logged in (not guest)
- Minimum 10 translation records for language pair
- Internet connection

**Solutions**:

1. **Add More Translations**: Need at least 10 records
2. **Check Internet**: AI generation requires API calls
3. **Wait**: Generation takes 30-60 seconds
4. **Check Logs**: Firebase Console → Functions → Logs
5. **Retry**: Temporary AI API issues happen

---

### Issue: Quiz Won't Award Coins

**Problem**: Completed quiz but didn't receive coins

**Causes** (see Coin System for details):
1. Already awarded for this quiz version
2. Quiz version doesn't match sheet version
3. Insufficient new records (need 10+)
4. Already awarded at current record count
5. Integrity check failed

**Solutions**:

1. **Check Eligibility**: Need 10+ new translations since last quiz
2. **Regenerate Quiz**: Add translations, then regenerate
3. **Check Transaction History**: Profile → Coin Balance
4. **Improve Score**: Need 5+ correct answers (50%)

---

## 🎨 UI Issues

### Issue: UI Language Not Changing

**Problem**: Selected language but UI still in English

**Solutions**:

1. **Wait for Download**: First language change takes 3-5 seconds
2. **Check Internet**: Translation requires API call
3. **Guest Mode Limitation**:
   - Guests get ONE free UI language change
   - Can switch between cached languages freely
   - Login for unlimited
4. **Restart App**: Forces UI refresh

---

### Issue: Text Overflowing or Cut Off

**Problem**: Text doesn't fit in UI elements

**Solutions**:

1. **Adjust Font Size**:
   - Settings → Font Size
   - Reduce to 90% or 80%

2. **Report Issue**: Some languages have longer text

3. **Rotate Device**: Landscape may show more

---

### Issue: Theme Not Applying

**Problem**: Changed color palette but no effect

**Solutions**:

1. **Unlock First**: Palette must be unlocked (purchase with coins)
2. **Restart App**: Theme changes require restart
3. **Check Balance**: Ensure coins were deducted
4. **Re-select**: Go to Settings → Theme and re-select

---

## 🐛 Debug Mode

### Enable Debug Logging

```kotlin
// In Application class or MainActivity
if (BuildConfig.DEBUG) {
    Firebase.firestore.setLoggingEnabled(true)
}
```

### View Logcat

Android Studio → Logcat → Filter by:
- Package: `com.example.fyp`
- Level: `Error`
- Specific tag

### Useful ADB Commands

```bash
# View app logs
adb logcat | grep com.example.fyp

# Clear app data
adb shell pm clear com.example.fyp

# Uninstall app
adb uninstall com.example.fyp

# Install APK
adb install -r app-debug.apk
```

---

## 📞 Getting Help

If problems persist:

1. **Check FAQ**: [FAQ Page](FAQ.md)
2. **Search Issues**: [GitHub Issues](https://github.com/TALMasterA/FYP/issues)
3. **Open New Issue**: Include:
   - Android version
   - Device model
   - Steps to reproduce
   - Error messages from Logcat
   - Screenshots

---

**Next**: [FAQ →](FAQ.md)
