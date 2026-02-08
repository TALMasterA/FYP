# Backend Setup

Firebase Cloud Functions setup and deployment guide for the FYP app backend.

---

## 🔥 Overview

The backend consists of Firebase Cloud Functions written in TypeScript that handle:
- Azure Speech token generation
- Translation API calls
- Language detection
- AI-generated learning materials
- AI-generated quizzes

**Why Cloud Functions?**
- **Security**: API keys never exposed to client
- **Scalability**: Auto-scales with demand
- **Serverless**: No server management
- **Integration**: Seamless Firebase integration

---

## 📋 Prerequisites

### Required Accounts

1. **Firebase Project**
   - Create at [console.firebase.google.com](https://console.firebase.google.com/)
   - Enable Blaze (Pay-as-you-go) plan for Cloud Functions

2. **Azure Account**
   - Sign up at [portal.azure.com](https://portal.azure.com/)
   - Create Speech Service resource
   - Create Translator resource

3. **Generative AI API**
   - Access to generative AI endpoint (Azure OpenAI or similar)

### Required Software

```bash
# Node.js (v18 or higher)
node --version

# Firebase CLI
npm install -g firebase-tools
firebase --version

# Authenticate with Firebase
firebase login
```

---

## 🚀 Initial Setup

### 1. Clone and Navigate

```bash
git clone https://github.com/TALMasterA/FYP.git
cd FYP/fyp-backend/functions
```

### 2. Install Dependencies

```bash
npm install
```

This installs:
- `firebase-functions` - Cloud Functions SDK
- `firebase-admin` - Admin SDK for Firestore
- `node-fetch` - HTTP client
- TypeScript and ESLint

### 3. Configure Firebase Project

```bash
# From fyp-backend directory
cd ..
firebase use --add
```

Select your Firebase project and give it an alias (e.g., `production`).

---

## 🔐 Setting Up Secrets

Cloud Functions use Firebase secrets to securely store API keys.

### 1. Azure Speech Service

```bash
# Set speech key
firebase functions:secrets:set AZURE_SPEECH_KEY
# When prompted, paste your key

# Set speech region (e.g., eastus, westeurope)
firebase functions:secrets:set AZURE_SPEECH_REGION
# When prompted, enter region
```

### 2. Azure Translator

```bash
# Set translator key
firebase functions:secrets:set AZURE_TRANSLATOR_KEY

# Set translator region
firebase functions:secrets:set AZURE_TRANSLATOR_REGION
```

### 3. Generative AI API

```bash
# Set base URL
firebase functions:secrets:set GENAI_BASE_URL

# Set API version
firebase functions:secrets:set GENAI_API_VERSION

# Set API key
firebase functions:secrets:set GENAI_API_KEY
```

---

## 📝 Cloud Functions Overview

### Available Functions

Located in `fyp-backend/functions/src/index.ts`:

#### 1. `getSpeechToken`
**Purpose**: Generate Azure Speech SDK authentication token  
**Called by**: Android app on startup and when token expires  
**Secrets**: `AZURE_SPEECH_KEY`, `AZURE_SPEECH_REGION`

#### 2. `translateText`
**Purpose**: Translate text using Azure Translator API  
**Called by**: Translation features, UI language translation  
**Secrets**: `AZURE_TRANSLATOR_KEY`, `AZURE_TRANSLATOR_REGION`

#### 3. `detectLanguage`
**Purpose**: Detect language of input text  
**Called by**: Auto-detect feature in discrete mode  
**Secrets**: `AZURE_TRANSLATOR_KEY`, `AZURE_TRANSLATOR_REGION`

#### 4. `generateLearningMaterial`
**Purpose**: Generate AI-powered learning sheets from history  
**Called by**: Learning system  
**Secrets**: `GENAI_BASE_URL`, `GENAI_API_VERSION`, `GENAI_API_KEY`  
**Timeout**: 540 seconds (9 minutes)  
**Memory**: 512 MiB

#### 5. `generateQuiz`
**Purpose**: Generate quiz from learning sheet  
**Called by**: Quiz system  
**Secrets**: `GENAI_BASE_URL`, `GENAI_API_VERSION`, `GENAI_API_KEY`  
**Timeout**: 540 seconds (9 minutes)  
**Memory**: 512 MiB

---

## 🚀 Deployment

### Deploy All Functions

```bash
cd fyp-backend
firebase deploy --only functions
```

This will:
1. Compile TypeScript to JavaScript
2. Upload code to Firebase
3. Configure function settings
4. Deploy to production

### Deploy Specific Function

```bash
# Deploy only one function
firebase deploy --only functions:translateText

# Deploy multiple specific functions
firebase deploy --only functions:translateText,functions:getSpeechToken
```

---

## 🧪 Testing

### Local Emulator

```bash
cd fyp-backend
firebase emulators:start
```

This starts:
- Functions emulator on `http://localhost:5001`
- Firestore emulator on `http://localhost:8080`

**Connect Android App to Emulator**:
```kotlin
// In your DI module
if (BuildConfig.DEBUG) {
    FirebaseFunctions.getInstance().useEmulator("10.0.2.2", 5001)
    FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
}
```

---

## 📊 Monitoring

### View Logs

```bash
# Real-time logs
firebase functions:log

# Specific function
firebase functions:log --only translateText
```

### Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Navigate to Functions
4. View invocation count, execution time, error rate, memory usage

---

## ⚙️ Configuration

### Function Settings

```typescript
export const myFunction = onCall(
  {
    secrets: [MY_SECRET],
    timeoutSeconds: 60,        // Max: 540 (9 min)
    memory: "256MiB",          // Options: 128MB - 8GB
    maxInstances: 100,         // Limit concurrent instances
    minInstances: 0            // Keep warm instances
  },
  async (request) => { }
);
```

### Global Settings

```typescript
setGlobalOptions({
  maxInstances: 10,  // Limit for all functions
  region: "us-central1"
});
```

---

## 💰 Cost Optimization

**Current Configuration**:
```typescript
setGlobalOptions({maxInstances: 10});
```

This limits concurrent function instances to control costs.

**Tips**:
1. Optimize memory allocation
2. Set appropriate timeouts
3. Cache results client-side
4. Monitor usage in Firebase Console

---

## 🔒 Security

### Authentication

All functions require authentication:
```typescript
function requireAuth(auth: unknown) {
  if (!auth) {
    throw new HttpsError("unauthenticated", "Login required.");
  }
}
```

### Input Validation

Use validation helpers:
```typescript
// Required string
const text = requireString(request.data.text, "text");

// Optional string
const from = optionalString(request.data.from);

// Required array with max length
const items = requireArray(request.data.items, "items", 100);
```

---

## 🐛 Troubleshooting

### Common Issues

**"Function not found"**
- Ensure deployed: `firebase deploy --only functions`
- Check function name spelling
- Verify Firebase project ID

**"Unauthenticated"**
- User not logged in
- Invalid auth token
- Token expired

**"Permission denied"**
- Check Firestore security rules
- Verify user has required permissions

**"Timeout"**
- Increase `timeoutSeconds` in function config
- Optimize function code
- Check external API response times

**"Secret not found"**
- Verify secret exists: `firebase functions:secrets:access`
- Redeploy function after setting secret

---

**Next**: [API Reference →](API-Reference.md)
