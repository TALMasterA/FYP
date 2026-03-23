# LeakCanary

## What is it?

**LeakCanary** is a memory leak detection library for Android apps by Square. It appears as a separate app icon during development to monitor memory and display leak reports.

## Key Points

- Only included in **debug builds** (not production)
- Automatically detects memory leaks (Activities, Fragments, Views, ViewModels)
- Shows notifications with detailed leak traces when issues are found
- Safe and used by thousands of professional Android developers

## Configuration

Located in `app/build.gradle.kts`:

```kotlin
dependencies {
    debugImplementation(libs.leakcanary.android)
}
```

## Usage

1. Use your app normally in debug mode
2. If a leak is detected, tap the notification to view details
3. Fix the leak in your code (cancel coroutines, remove listeners, release resources)

## Learn More

- [Official Documentation](https://square.github.io/leakcanary/)
- [GitHub Repository](https://github.com/square/leakcanary)
