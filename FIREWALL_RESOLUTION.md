# Firewall Issue Resolution

## Problem
Firewall rules were blocking connections to `dl.google.com`, preventing Gradle from downloading Android SDK and Google dependencies during the build process.

### Error Details
```
Firewall rules blocked me from connecting to one or more addresses (expand for details)
I tried to connect to the following addresses, but was blocked by firewall rules:
dl.google.com
```

## Solution Applied
A custom allowlist was configured to permit connections to Google's download servers.

## Verification (2026-02-16)

### ✅ Successfully Tested
1. **Gradle Distribution Download**
   - Downloaded Gradle 8.14 from `services.gradle.org` 
   - No connection issues encountered

2. **Gradle Tasks Execution**
   - Successfully ran `./gradlew tasks --no-daemon`
   - All Android build tasks are available
   - Dependency resolution is working

3. **Google Repository Access**
   - Can now connect to `dl.google.com`
   - Android SDK and dependencies can be downloaded
   - Maven Google repository is accessible

### Build Output Confirmation
```
Welcome to Gradle 8.14!

> Task :tasks

------------------------------------------------------------
Tasks runnable from root project 'FYP'
------------------------------------------------------------

Android tasks
-------------
androidDependencies - Displays the Android dependencies of the project.
signingReport - Displays the signing info for the base and test modules
sourceSets - Prints out all the source sets defined in this project.

Build tasks
-----------
assemble - Assemble main outputs for all the variants.
build - Assembles and tests this project.
...
```

## Current Build Status

The build now progresses successfully through dependency resolution and stops only at the Firebase configuration step due to missing `google-services.json` file, which is expected in CI/CD environments without Firebase credentials. This is **unrelated** to the firewall issue.

### What's Working
- ✅ Network connectivity to Google servers
- ✅ Gradle wrapper download
- ✅ Dependency resolution
- ✅ Android plugin initialization
- ✅ Kotlin compilation setup

### Expected Limitation (Not a Bug)
- ⚠️ Firebase configuration requires `google-services.json` (not available in CI)
- This is a deployment/credentials issue, not a firewall issue

## Conclusion

**The firewall connectivity issue has been fully resolved.** The custom allowlist is working correctly, and the build process can now access all required Google repositories and services.

For local development or production builds, ensure `google-services.json` is present in the `app/` directory for Firebase features to work.
