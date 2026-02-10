# Build Troubleshooting Guide

## Current Build Issue

### Symptom
```
Plugin [id: 'com.android.application', version: 'X.X.X'] was not found
```

### Root Cause
The build environment cannot access Google's Maven repository (`dl.google.com`), which hosts the Android Gradle Plugin and related dependencies.

**Verified with:**
```bash
$ curl -I https://dl.google.com/dl/android/maven2/
curl: (6) Could not resolve host: dl.google.com
```

## Solutions

### Option 1: Ensure Network Connectivity (Recommended)
The Android build requires access to these repositories:
- **Google Maven**: `https://dl.google.com/dl/android/maven2/`
- **Maven Central**: `https://repo1.maven.org/maven2/`
- **Gradle Plugin Portal**: `https://plugins.gradle.org/`

**For GitHub Actions:**
1. Ensure the runner has internet access
2. Check firewall/proxy settings
3. Verify DNS resolution works

**For local builds:**
1. Check your network connection
2. If behind a corporate proxy, configure Gradle proxy settings in `~/.gradle/gradle.properties`:
```properties
systemProp.http.proxyHost=your.proxy.host
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=your.proxy.host
systemProp.https.proxyPort=8080
```

### Option 2: Use Offline Mode (If Dependencies Cached)
If you've previously built the project and dependencies are cached:

```bash
./gradlew assembleDebug --offline
```

**Note:** This only works if all required dependencies are already in `~/.gradle/caches/`.

### Option 3: Configure Repository Mirrors
If using a mirror or corporate repository, update `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        maven { url = uri("https://your-mirror-url/google") }  // Your mirror
        maven { url = uri("https://your-mirror-url/mavenCentral") }
        google()  // Fallback
        mavenCentral()  // Fallback
        gradlePluginPortal()
    }
}
```

## Android Gradle Plugin Version

Current version in `gradle/libs.versions.toml`:
```toml
[versions]
agp = "8.13.2"
```

**Version History:**
- Main branch: AGP 8.13.2 (original configuration)
- Previously changed to 8.6.0 (doesn't exist - caused build failure)
- Temporarily downgraded to 8.1.4
- Attempted update to 8.7.0
- **Now restored to: 8.13.2** (matches main branch)

**Compatible versions:**
- AGP 8.13.x works with Kotlin 2.0.21 and Gradle 8.7+
- Compatible with compileSdk 36

## Gradle Wrapper Version

Current: `8.14` (in `gradle/wrapper/gradle-wrapper.properties`)

**Compatibility:**
- Gradle 8.14 is compatible with AGP 8.13.2
- Gradle 8.7+ required for AGP 8.7.x+
- Gradle 8.0+ works with AGP 8.1.x - 8.6.x

## Verification Steps

1. **Test network connectivity:**
```bash
curl -I https://dl.google.com/dl/android/maven2/
```

2. **Clear Gradle caches (if needed):**
```bash
rm -rf ~/.gradle/caches/
rm -rf .gradle/
```

3. **Build the project:**
```bash
./gradlew clean assembleDebug
```

## Common Error Messages

### "Could not resolve plugin artifact"
- **Cause:** Network connectivity issue or invalid version
- **Solution:** Check network access and verify AGP version exists

### "Plugin was not found in any of the following sources"
- **Cause:** Repository configuration issue
- **Solution:** Verify `settings.gradle.kts` has correct repository URLs

### "Could not download ... Could not GET ..."
- **Cause:** Network timeout or blocked URL
- **Solution:** Check firewall, proxy settings, or use VPN

## Additional Resources

- [Android Gradle Plugin Release Notes](https://developer.android.com/studio/releases/gradle-plugin)
- [Gradle Build Environment](https://docs.gradle.org/current/userguide/build_environment.html)
- [Configuring Gradle Proxies](https://docs.gradle.org/current/userguide/build_environment.html#sec:accessing_the_web_via_a_proxy)

## For CI/CD (GitHub Actions)

If building in GitHub Actions, ensure the workflow has:

```yaml
- name: Build Android App
  run: ./gradlew assembleDebug --stacktrace
  env:
    # If using proxy
    GRADLE_OPTS: "-Dhttp.proxyHost=proxy.example.com -Dhttp.proxyPort=8080"
```

**Note:** The current build environment lacks access to Google Maven. This must be resolved at the infrastructure level.

---

**Last Updated:** February 9, 2026  
**Issue Status:** Network connectivity to `dl.google.com` required
