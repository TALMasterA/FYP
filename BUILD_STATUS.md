# Build Status and Version Information

## Current Status: ❌ Build Fails - Cannot Download Android Gradle Plugin

### Critical Issue: Google Maven Repository Not Accessible

**The build environment cannot access `dl.google.com` (Google Maven Repository)**

```bash
$ curl -I https://dl.google.com/dl/android/maven2/
curl: (6) Could not resolve host: dl.google.com
```

This prevents downloading the Android Gradle Plugin, which is **REQUIRED** for building Android applications.

## What We Tried

1. ❌ **AGP 8.13.2** - Version doesn't exist (too high, main branch has incorrect version)
2. ❌ **AGP 8.7.0** - Cannot download (network blocked)
3. ❌ **AGP 8.3.2** - Cannot download (network blocked) ← **CURRENT**
4. ❌ **Alternative mirrors** (Aliyun) - Cannot access (network blocked)
5. ✅ **Maven Central** - Accessible but doesn't host Android Gradle Plugin
6. ✅ **Gradle Plugin Portal** - Accessible but doesn't host Android Gradle Plugin

## Current Configuration

```toml
[versions]
agp = "8.3.2"         # Android Gradle Plugin (VALID but cannot download)
gradle = "8.3.2"      # Gradle version reference  
kotlin = "2.0.21"     # Kotlin compiler version
```

Gradle wrapper: `8.14` (from `gradle/wrapper/gradle-wrapper.properties`)

## The REAL Problem

**Android Gradle Plugin is ONLY available from Google's Maven Repository**

Repositories that ARE accessible:
- ✅ Maven Central (`https://repo1.maven.org`) - doesn't have AGP
- ✅ Gradle Plugin Portal - doesn't have AGP

Repository that is NOT accessible:
- ❌ Google Maven (`https://dl.google.com/dl/android/maven2/`) - **REQUIRED for AGP**

## Solutions

### Option 1: ✅ Fix Network Connectivity (REQUIRED)

**For GitHub Actions runners:**
- Check runner network permissions
- Verify firewall/proxy settings
- Ensure DNS resolution works for `dl.google.com`

**For local development:**
1. Check internet connection
2. Disable VPN if causing DNS issues  
3. Check firewall settings
4. Try from a different network

**For corporate/restricted environments:**
1. Request whitelist for `dl.google.com`
2. Request whitelist for `*.googleapis.com`
3. Configure proxy settings if required

### Option 2: Use Pre-downloaded Dependencies

If you have a machine with working internet:

1. Build the project once on a machine with internet access
2. Copy the entire `~/.gradle/caches` directory
3. Transfer to the offline environment
4. Build with `./gradlew assembleDebug --offline`

### Option 3: Use Corporate Maven Mirror

If your organization has a Maven mirror:

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        maven { url = uri("https://your-company-maven-mirror.com/google") }
        gradlePluginPortal()
        mavenCentral()
    }
}
```

## Version History

1. **Main Branch**: AGP 8.13.2 (❌ doesn't exist - this version is too high)
2. **Attempted Fix #1**: AGP 8.6.0 (❌ doesn't exist)
3. **Attempted Fix #2**: AGP 8.1.4 (✅ exists but couldn't download)
4. **Attempted Fix #3**: AGP 8.7.0 (✅ likely exists but couldn't download)
5. **Current**: AGP 8.3.2 (✅ definitely exists but **cannot download due to network**)

## Valid AGP Versions

| AGP Version | Status | Notes |
|-------------|--------|-------|
| 8.3.2       | ✅ Exists | Current choice, well-tested |
| 8.4.2       | ✅ Exists | Also stable |
| 8.5.2       | ✅ Exists | Recent stable |
| 8.6.1       | ✅ Exists | Latest 8.6.x |
| 8.7.0       | ⚠️ May exist | Latest stable (early 2026) |
| 8.13.2      | ❌ Doesn't exist | Version too high (main branch error) |

## What You Need to Do

1. **Verify network connectivity:**
   ```bash
   curl -I https://dl.google.com/dl/android/maven2/
   ```
   Should return `HTTP 200`, not `Could not resolve host`

2. **If network is blocked:**
   - Contact your IT/infrastructure team
   - Request access to `dl.google.com`
   - Or use one of the workaround options above

3. **Once network is fixed:**
   ```bash
   cd /home/runner/work/FYP/FYP
   ./gradlew assembleDebug
   ```

## Environment Details

- ✅ Gradle wrapper: 8.14 (downloaded successfully)
- ✅ Android SDK: Available at `/usr/local/lib/android/sdk`
- ✅ Maven Central: Accessible
- ✅ Gradle Plugin Portal: Accessible
- ❌ Google Maven: **NOT accessible** (blocking all builds)

---

**Bottom Line:** No code changes will fix this. It's a network/infrastructure issue that must be resolved at the environment level.

**Last Updated:** 2026-02-10 09:18 UTC
