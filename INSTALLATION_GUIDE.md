# Installation Guide — FYP Translation & Learning App

## Table of Contents

1. [Overview](#1-overview)
2. [Prerequisites](#2-prerequisites)
3. [Repository Setup](#3-repository-setup)
4. [Android App Setup](#4-android-app-setup)
5. [Firebase Project Setup](#5-firebase-project-setup)
6. [Backend (Cloud Functions) Setup](#6-backend-cloud-functions-setup)
7. [Azure API Setup](#7-azure-api-setup)
8. [Running the App](#8-running-the-app)
9. [Running Tests](#9-running-tests)
10. [Database Information](#10-database-information)
11. [Troubleshooting](#11-troubleshooting)

---

## 1. Overview

This project is an Android-based translation and language learning application with:
- **Frontend:** Android (Kotlin, Jetpack Compose)
- **Backend:** Firebase Cloud Functions (TypeScript / Node.js)
- **Database:** Firebase Firestore (cloud-hosted NoSQL)
- **External APIs:** Azure Speech, Azure Translator, Azure OpenAI

Architecture: MVVM + Clean Architecture with Hilt dependency injection.

---

## 2. Prerequisites

| Tool | Minimum Version | Purpose |
|------|----------------|---------|
| **Android Studio** | Latest stable (Ladybug or newer) | Android IDE |
| **JDK** | 11+ | Kotlin / Gradle compilation |
| **Node.js** | 24.x | Cloud Functions runtime |
| **npm** | 10+ | Backend dependency management |
| **Firebase CLI** | Latest (`npm install -g firebase-tools`) | Deploy & manage Firebase |
| **Git** | 2.30+ | Source control |

**Accounts required:**
- Google account (for Firebase Console)
- Microsoft Azure account (for Speech, Translator, and OpenAI APIs)

---

## 3. Repository Setup

```bash
# Clone the repository
git clone https://github.com/TALMasterA/FYP.git
cd FYP
```

The repository contains:
- `app/` — Android application source code
- `fyp-backend/` — Firebase configuration and Cloud Functions
- `docs/` — Project documentation
- `report-audit/` — Report generation tools and diagrams

---

## 4. Android App Setup

1. **Open the project** in Android Studio:
   - File → Open → select the `FYP` root folder.

2. **Wait for Gradle sync** to complete. Android Studio will download all dependencies automatically.

3. **Build the project:**
   ```bash
   ./gradlew assembleDebug
   ```

---

## 5. Firebase Project Setup

1. **Create a Firebase project** at [Firebase Console](https://console.firebase.google.com/).

2. **Register an Android app:**
   - Package name: `com.example.fyp`

3. **Enable Firebase services:**
   - **Authentication:** Enable Email/Password sign-in provider.
   - **Firestore Database:** Create in production mode. The security rules are in `fyp-backend/firestore.rules`.
   - **Cloud Functions:** Requires Blaze (pay-as-you-go) plan.
   - **Crashlytics:** Enable in Firebase Console.
   - **Cloud Messaging (FCM):** Enable for push notifications.
   - **App Distribution** (optional): For distributing test builds.

4. **Deploy Firestore rules and indexes:**
   ```bash
   cd fyp-backend
   firebase login
   firebase use <your-project-id>
   firebase deploy --only firestore:rules
   firebase deploy --only firestore:indexes
   ```

5. **Firestore security rules** are defined in `fyp-backend/firestore.rules` — they enforce owner-only access, type validation, and block-system enforcement.

6. **Firestore indexes** are defined in `fyp-backend/firestore.indexes.json` — required for compound queries on quiz attempts, user search, and friend requests.

---

## 6. Backend (Cloud Functions) Setup

1. **Install dependencies:**
   ```bash
   cd fyp-backend/functions
   npm install
   ```

2. **Set Firebase secrets** (API keys for Azure services):
   ```bash
   firebase functions:secrets:set AZURE_SPEECH_KEY
   firebase functions:secrets:set AZURE_SPEECH_REGION
   firebase functions:secrets:set AZURE_TRANSLATOR_KEY
   firebase functions:secrets:set AZURE_TRANSLATOR_REGION
   firebase functions:secrets:set GENAI_BASE_URL
   firebase functions:secrets:set GENAI_API_VERSION
   firebase functions:secrets:set GENAI_API_KEY
   ```
   Each command will prompt you to enter the corresponding value.

3. **Build and deploy:**
   ```bash
   npm run build
   cd ..
   firebase deploy --only functions
   ```

4. **Verify deployment** in Firebase Console → Functions tab. Key functions include:
   - `translateText` — Text translation via Azure
   - `getSpeechToken` — Azure Speech token provider
   - `generateLearningContent` — AI-powered learning sheet generation
   - `generateQuiz` — Quiz generation from learning sheets
   - `generateWordBank` — Word bank generation
   - `awardCoins` — Coin system with anti-cheat
   - `sendFriendRequest`, `acceptFriendRequest` — Friend system
   - `sendChatMessage` — Real-time chat with auto-translation

---

## 7. Azure API Setup

### 7.1 Azure Speech Service
1. Create a **Speech Services** resource in [Azure Portal](https://portal.azure.com/).
2. Note the **Key** and **Region** (e.g., `eastasia`).
3. Set via Firebase secrets: `AZURE_SPEECH_KEY`, `AZURE_SPEECH_REGION`.

### 7.2 Azure Translator
1. Create a **Translator** resource in Azure Portal.
2. Note the **Key** and **Region**.
3. Set via Firebase secrets: `AZURE_TRANSLATOR_KEY`, `AZURE_TRANSLATOR_REGION`.

### 7.3 Azure OpenAI
1. Create an **Azure OpenAI** resource and deploy a model (e.g., GPT-4).
2. Note the **Endpoint URL**, **API Version**, and **Key**.
3. Set via Firebase secrets: `GENAI_BASE_URL`, `GENAI_API_VERSION`, `GENAI_API_KEY`.

---

## 8. Running the App

### On a Physical Device
1. Enable **Developer Options** and **USB Debugging** on your Android device.
2. Connect via USB.
3. In Android Studio, select the device and click **Run** (or `Shift+F10`).

### On an Emulator
1. In Android Studio → Device Manager → Create a virtual device.
2. Select a device profile (e.g., Pixel 6) with **API 26+**.
3. Click **Run**.

### First Launch
- The app shows an onboarding screen on first launch.
- Register an account with email/password.
- Note: Registration may be disabled during development periods (controlled via backend).

---

## 9. Running Tests

### Android Unit Tests
```bash
# Run all unit tests (2,496 tests across 191 test files)
./gradlew testDebugUnitTest

# Run a specific test class
./gradlew testDebugUnitTest --tests "com.example.fyp.domain.learning.GenerationEligibilityTest"
```

### Backend Tests
```bash
cd fyp-backend/functions

# Run all tests (186 tests across 14 test files)
npm test

# Run with coverage
npm run test:coverage
```

### CI Pipeline
The project includes GitHub Actions CI (`.github/workflows/ci.yml`) that runs:
- Android unit tests
- Debug APK build
- Backend lint, build, and Jest coverage (50% threshold)

---

## 10. Database Information

This project uses **Firebase Firestore** — a cloud-hosted NoSQL document database. There is **no local database file** to install or configure separately.

### Key Points:
- **No database dump is required for submission.** The database is created automatically when the Firebase project is set up and the app writes its first data.
- **Database schema** is defined implicitly by the application code and enforced by Firestore security rules (`fyp-backend/firestore.rules`).
- **Indexes** are defined in `fyp-backend/firestore.indexes.json` and deployed via Firebase CLI.
- **Security rules** enforce authentication, ownership, type validation, rate limits, and block-system constraints.

### Firestore Collections Structure:
| Collection Path | Purpose |
|----------------|---------|
| `users/{uid}/history` | Translation records |
| `users/{uid}/sessions` | Continuous conversation sessions |
| `users/{uid}/profile/settings` | User preferences |
| `users/{uid}/profile/public` | Public profile (username) |
| `users/{uid}/learning_sheets/{pair}` | AI-generated learning content |
| `users/{uid}/quiz_attempts/{id}` | Quiz attempts and scores |
| `users/{uid}/generated_quizzes/{pair}` | Cached quiz questions |
| `users/{uid}/user_stats/coins` | Coin balance |
| `users/{uid}/favorites/{id}` | Bookmarked translations |
| `users/{uid}/word_banks/{pair}` | Generated vocabulary |
| `users/{uid}/custom_words/{id}` | User-defined vocabulary |
| `users/{uid}/friends/{friendUid}` | Friend relationships |
| `users/{uid}/shared_inbox/{id}` | Shared items from friends |
| `usernames/{username}` | Username registry |
| `user_search/{uid}` | Searchable user index |
| `friend_requests/{id}` | Friend request records |
| `chats/{chatId}/messages/{id}` | Chat messages |
| `rate_limits/{id}` | Server-side rate limiting |
| `feedback/{id}` | User feedback submissions |

---

## 11. Troubleshooting

| Issue | Solution |
|-------|----------|
| Gradle sync fails | Ensure JDK 11+ is configured in Android Studio. File → Settings → Build → Gradle → Gradle JDK. |
| Cloud Functions deploy fails | Ensure you are on the Blaze plan. Run `firebase login` and `firebase use <project-id>`. |
| Azure API errors | Verify secrets are set correctly: `firebase functions:secrets:access AZURE_SPEECH_KEY`. |
| App crashes on launch | Check Logcat for missing Firebase configuration. Ensure your Firebase project is set up correctly. |
| Tests fail | Run `./gradlew clean` then retry. Ensure no stale build artifacts. |
| Node.js version mismatch | The backend requires Node.js 24. Use `nvm` or `fnm` to manage versions. |
