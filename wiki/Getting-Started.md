# Getting Started

This guide will help you set up the FYP Translation & Learning App development  
environment and get the app running on your local machine.

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software

1. **Android Studio** (Latest stable version)
   - Download from [developer.android.com](https://developer.android.com/studio)
   - Minimum version: Arctic Fox or later

2. **JDK 11 or Higher**
   - Android Studio typically includes JDK
   - Verify installation: `java -version`

3. **Android SDK**
   - API Level 24 (Android 7.0) minimum
   - API Level 34 (Android 14) recommended
   - Install via Android Studio SDK Manager

4. **Git**
   - Download from [git-scm.com](https://git-scm.com/)
   - Verify installation: `git --version`

### Optional Tools

- **GitHub CLI**: For easier PR management
  ```bash
  # Windows
  winget install --id GitHub.cli
  
  # Verify installation
  gh --version
  ```

- **Firebase CLI**: For backend development
  ```bash
  npm install -g firebase-tools
  firebase login
  ```

---

## 🔧 Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/TALMasterA/FYP.git
cd FYP
```

### 2. Firebase Configuration

The app requires Firebase for authentication, database, and backend functions.

#### Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing
3. Add an Android app with package name: `com.example.fyp`
4. Download `google-services.json`
5. Place it in the `app/` folder:
   ```
   FYP/
   └── app/
       └── google-services.json
   ```

#### Enable Firebase Services

1. **Authentication**
   - Enable Email/Password provider
   - Configure sign-in methods

2. **Firestore Database**
   - Create database in production mode
   - Set up security rules

3. **Cloud Functions**
   - Initialize Cloud Functions
   - Configure Firebase project

---

### 3. Azure Services Setup (Optional)

For full functionality, you need Azure Speech and Translation services:

1. Create an Azure account at [portal.azure.com](https://portal.azure.com/)
2. Create a **Speech Service** resource
3. Create a **Translator** resource
4. Note down your API keys and regions

These keys are configured in Firebase Cloud Functions.

---

### 4. Open Project in Android Studio

1. Launch **Android Studio**
2. Select **Open an Existing Project**
3. Navigate to the cloned `FYP` folder
4. Click **OK**

Android Studio will:
- Sync Gradle dependencies (may take a few minutes)
- Index the project files
- Download required SDKs if missing

---

### 5. Build the Project

#### Via Command Line
```bash
# From project root
./gradlew compileDebugKotlin
```

#### Via Android Studio
- **Build → Make Project** (Ctrl+F9 / Cmd+F9)
- Wait for build to complete

---

## 📱 Running the App

### Option A: Physical Android Device (Recommended)

1. **Enable Developer Options**
   - Go to: Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to: Settings → System → Developer Options

2. **Enable USB Debugging**
   - Toggle "USB Debugging" in Developer Options
   - ⚠️ **Security Note**: Disable when not testing (banking app security)

3. **Connect Device**
   - Connect device to computer via USB
   - Accept "Allow USB debugging" prompt
   - Device should appear in Android Studio's device dropdown

4. **Run the App**
   - Click the green "Run" button
   - Or press: Shift+F10 (Windows/Linux) / Ctrl+R (Mac)

### Option B: Android Emulator

1. **Create Virtual Device**
   - In Android Studio: Tools → Device Manager
   - Click "Create Device"
   - Choose device (e.g., Pixel 5)
   - Select system image (API 34 recommended)
   - Click "Finish"

2. **Run on Emulator**
   - Select emulator from device dropdown
   - Click "Run" button

> **Note**: Emulator may not fully support microphone features.  
> Physical device recommended for speech recognition testing.

---

## 🧪 Verify Installation

After running the app, you should be able to:

- ✅ See the login screen
- ✅ Create a new account (requires Firebase setup)
- ✅ Navigate between screens
- ✅ Access guest mode (limited features)
- ✅ Test basic translation features

---

## 🔍 Common Setup Issues

### Issue: `google-services.json not found`

**Solution**: Download from Firebase Console and place in `app/` folder.

---

### Issue: `SDK location not found`

**Solution**: Create `local.properties` in project root:
```properties
sdk.dir=/path/to/Android/Sdk
```

Or sync project: File → Sync Project with Gradle Files

---

### Issue: Gradle Sync Failed

**Solutions**:
1. Check internet connection
2. Invalidate caches: File → Invalidate Caches / Restart
3. Update Gradle wrapper:
   ```bash
   ./gradlew wrapper --gradle-version=8.2
   ```

---

### Issue: Build Errors

**Solutions**:
1. Clean build: Build → Clean Project
2. Rebuild: Build → Rebuild Project
3. Check JDK version: File → Project Structure → SDK Location

---

## 📂 Project Files Overview

After setup, your project structure should look like:

```
FYP/
├── app/
│   ├── build.gradle.kts          # App dependencies
│   ├── google-services.json      # Firebase config (you add this)
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/example/fyp/
├── fyp-backend/
│   └── functions/                # Cloud Functions (TypeScript)
├── build.gradle.kts              # Project-level build config
├── settings.gradle.kts
└── README.md
```

---

## 🎓 Next Steps

Now that you have the app running:

1. **Explore the codebase**: Check out [Project Architecture](Architecture.md)
2. **Understand features**: Read [Features Overview](Features.md)
3. **Start developing**: Follow [Development Guide](Development.md)
4. **Set up backend**: See [Backend Setup](Backend.md)

---

## 🆘 Need Help?

- Check [Troubleshooting Guide](Troubleshooting.md)
- Review [FAQ](FAQ.md)
- Open an issue on [GitHub](https://github.com/TALMasterA/FYP/issues)

---

**Next**: [Project Architecture →](Architecture.md)
