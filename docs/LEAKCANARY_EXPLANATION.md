# About LeakCanary

## What is LeakCanary?

**LeakCanary** is a memory leak detection library for Android apps developed by Square. When you install your app in **debug mode**, LeakCanary automatically installs as a separate app on your device to help you find and fix memory leaks.

## Why Does It Appear?

LeakCanary appears as a separate app icon because it needs to:
1. **Monitor your app's memory** in the background
2. **Display notifications** when memory leaks are detected
3. **Show detailed reports** of memory leaks in a separate UI

## Is It Safe?

**Yes, it's completely safe!** LeakCanary:
- ✅ Only appears in **debug builds** (not production)
- ✅ Automatically removes itself in **release builds**
- ✅ Helps developers find and fix memory problems
- ✅ Used by thousands of professional Android developers
- ✅ Open source and maintained by Square (trusted company)

## What Does It Do?

LeakCanary watches your app for common memory leak patterns:

### Memory Leaks It Detects:
1. **Activity Leaks** - Activities that should be destroyed but are still in memory
2. **Fragment Leaks** - Fragments not properly cleaned up
3. **View Leaks** - Views holding references after they should be released
4. **ViewModel Leaks** - ViewModels not properly scoped
5. **Bitmap Leaks** - Large images not released from memory

### When Leaks Are Found:
1. 🔴 **Red notification** appears on your device
2. 📊 Detailed leak trace is generated
3. 🔍 Tap notification to see exactly what's leaking
4. 💡 Helps you fix the issue in your code

## Should You Keep It?

### For Developers: **YES! Keep it!**
- Helps identify performance issues early
- Prevents OutOfMemory crashes
- Shows you exactly where leaks occur
- Makes your app more stable

### For End Users: **They won't see it!**
- Only included in debug builds
- Production releases don't include LeakCanary
- End users never see the separate app

## How to Use It

1. **During Development:**
   - Use your app normally
   - If LeakCanary detects a leak, you'll see a notification
   - Tap the notification to view details
   - Fix the leak in your code

2. **What Leaks Look Like:**
   ```
   ┬───
   │ GC Root: System class
   │
   ├─ android.app.ActivityThread instance
   │    Leaking: NO (ActivityThread↓ is not leaking)
   │    ↓ ActivityThread.mActivities
   ├─ android.util.ArrayMap instance
   │    Leaking: UNKNOWN
   │    ↓ ArrayMap[0]
   ├─ YourActivity instance
   │    Leaking: YES (ObjectWatcher was watching this)
   ```

3. **Common Fixes:**
   - Cancel coroutines in `onDestroy()`
   - Remove listeners and observers
   - Release heavy resources
   - Use `WeakReference` when appropriate

## In Your Project

LeakCanary is configured in `app/build.gradle.kts`:

```kotlin
dependencies {
    // Performance Monitoring & Debugging (Debug only)
    debugImplementation(libs.leakcanary.android)
}
```

### Key Points:
- ✅ `debugImplementation` - Only in debug builds
- ✅ Automatically initialized
- ✅ No code changes needed
- ✅ Zero impact on production builds

## Uninstalling LeakCanary (Not Recommended)

If you really want to remove it:

### Option 1: Uninstall the App (Temporary)
```bash
adb uninstall com.squareup.leakcanary.leaksentry
```
*Note: It will return next time you install your debug app*

### Option 2: Remove from Project (Permanent)
Remove this line from `app/build.gradle.kts`:
```kotlin
debugImplementation(libs.leakcanary.android)
```

**⚠️ WARNING:** Removing LeakCanary means you won't be notified of memory leaks, which can lead to:
- App crashes (OutOfMemoryError)
- Poor performance
- Battery drain
- Frozen UI

## Recommendations

### ✅ DO:
- Keep LeakCanary in your project
- Fix leaks when detected
- Test your app thoroughly with LeakCanary running
- Check LeakCanary reports regularly

### ❌ DON'T:
- Remove LeakCanary from debug builds
- Ignore leak notifications
- Ship debug builds to users
- Worry about it appearing on your device

## Learn More

- **Official Website:** https://square.github.io/leakcanary/
- **GitHub:** https://github.com/square/leakcanary
- **Documentation:** https://square.github.io/leakcanary/getting_started/

## Summary

**LeakCanary is your friend!** 🎉

Think of it as a health monitor for your app that:
- 🏥 Diagnoses memory problems
- 🔧 Helps you fix issues
- 📈 Improves app quality
- 🚀 Makes your app faster

The separate app icon is normal and expected during development. It won't appear in production builds that you distribute to users.

