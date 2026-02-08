# API Reference

Firebase Cloud Functions API documentation for the FYP app.

---

## 🌐 Overview

All backend APIs are implemented as Firebase Cloud Functions (HTTPS Callable Functions).  
They require Firebase Authentication and are accessed via the Firebase SDK.

**Authentication**: Firebase ID Token (automatic via SDK)  
**Request Format**: JSON  
**Response Format**: JSON

---

## 🔐 Authentication

All functions require user authentication via Firebase SDK.

### Client Setup

```kotlin
val functions = Firebase.functions
val result = functions
    .getHttpsCallable("functionName")
    .call(requestData)
    .await()
```

---

## 📡 API Functions

### 1. getSpeechToken

Get Azure Speech SDK authentication token.

**Purpose**: Obtain temporary token for client-side speech recognition

**Request**: No parameters required

**Response**:
```typescript
{
  token: string,    // Azure Speech token (valid 10 minutes)
  region: string    // Azure region (e.g., "eastus")
}
```

**Example**:
```kotlin
val result = functions
    .getHttpsCallable("getSpeechToken")
    .call()
    .await()
    
val data = result.getData<Map<String, String>>()
val token = data["token"]
val region = data["region"]
```

**Errors**:
- `unauthenticated`: User not logged in
- `internal`: Azure API error

---

### 2. translateText

Translate text using Azure Translator API.

**Purpose**: Translate text from source language to target language(s)

**Request**:
```typescript
{
  text: string,           // Text to translate (required)
  to: string | string[],  // Target language(s) (required)
  from?: string           // Source language (optional, auto-detect if omitted)
}
```

**Response**:
```typescript
{
  translations: Array<{
    to: string,           // Target language code
    text: string          // Translated text
  }>,
  detectedLanguage?: {
    language: string,     // Detected language code
    score: number         // Confidence (0-1)
  }
}
```

**Example - Single Translation**:
```kotlin
val request = mapOf(
    "text" to "Hello, world!",
    "to" to "es"
)

val result = functions
    .getHttpsCallable("translateText")
    .call(request)
    .await()
```

**Example - Multiple Targets**:
```kotlin
val request = mapOf(
    "text" to "Good morning",
    "to" to listOf("es", "fr", "de")
)
```

**Errors**:
- `unauthenticated`: User not logged in
- `invalid-argument`: Missing or invalid parameters
- `resource-exhausted`: Text too long (max 10,000 characters)
- `internal`: Azure API error

**Supported Languages**: 100+ (see [Azure Translator docs](https://docs.microsoft.com/en-us/azure/cognitive-services/translator/language-support))

---

### 3. detectLanguage

Detect the language of input text.

**Purpose**: Identify language of text (used for auto-detect feature)

**Request**:
```typescript
{
  text: string  // Text to analyze (required)
}
```

**Response**:
```typescript
{
  language: string,    // Detected language code (e.g., "en", "es", "ja")
  score: number        // Confidence score (0-1)
}
```

**Example**:
```kotlin
val request = mapOf("text" to "Bonjour, comment allez-vous?")

val result = functions
    .getHttpsCallable("detectLanguage")
    .call(request)
    .await()
    
val data = result.getData<Map<String, Any>>()
val language = data["language"] as String  // "fr"
val confidence = data["score"] as Double   // 0.99
```

**Errors**:
- `unauthenticated`: User not logged in
- `invalid-argument`: Missing text parameter
- `internal`: Azure API error

---

### 4. generateLearningMaterial

Generate AI-powered learning sheet from translation history.

**Purpose**: Create structured learning content based on user's translations

**Request**:
```typescript
{
  sourceLang: string,     // Source language code
  targetLang: string,     // Target language code
  translations: Array<{   // Translation history (max 100)
    source: string,
    target: string,
    timestamp: number
  }>
}
```

**Response**:
```typescript
{
  content: string,        // Formatted learning material (Markdown)
  recordCount: number,    // Number of translations used
  version: number         // Sheet version number
}
```

**Example**:
```kotlin
val request = mapOf(
    "sourceLang" to "en",
    "targetLang" to "es",
    "translations" to listOf(
        mapOf("source" to "Hello", "target" to "Hola", "timestamp" to 1234567890),
        // ... more translations
    )
)

val result = functions
    .getHttpsCallable("generateLearningMaterial")
    .call(request)
    .await()
```

**Errors**:
- `unauthenticated`: User not logged in
- `invalid-argument`: Missing parameters or invalid data
- `resource-exhausted`: Too many translations (max 100)
- `deadline-exceeded`: Generation timeout (max 540 seconds)
- `internal`: AI API error

**Timeout**: 540 seconds (9 minutes)  
**Memory**: 512 MiB

---

### 5. generateQuiz

Generate quiz from learning sheet.

**Purpose**: Create interactive quiz based on learning material

**Request**:
```typescript
{
  content: string,      // Learning sheet content
  sourceLang: string,   // Source language code
  targetLang: string    // Target language code
}
```

**Response**:
```typescript
{
  quiz: {
    questions: Array<{
      question: string,
      options: string[],      // 4 options
      correctAnswer: number   // Index of correct option (0-3)
    }>,
    totalQuestions: number    // Always 10
  }
}
```

**Example**:
```kotlin
val request = mapOf(
    "content" to learningSheetContent,
    "sourceLang" to "en",
    "targetLang" to "es"
)

val result = functions
    .getHttpsCallable("generateQuiz")
    .call(request)
    .await()
```

**Quiz Format**:
- 10 multiple-choice questions
- 4 options per question (A, B, C, D)
- Mix of vocabulary and comprehension
- Based on learning sheet content

**Errors**:
- `unauthenticated`: User not logged in
- `invalid-argument`: Missing or invalid parameters
- `deadline-exceeded`: Generation timeout
- `internal`: AI API error or parsing failure

**Timeout**: 540 seconds (9 minutes)  
**Memory**: 512 MiB

---

## 🔧 Error Handling

### Error Codes

| Code | Description |
|------|-------------|
| `unauthenticated` | User not logged in |
| `permission-denied` | User lacks required permissions |
| `invalid-argument` | Invalid request parameters |
| `not-found` | Requested resource not found |
| `resource-exhausted` | Rate limit or quota exceeded |
| `deadline-exceeded` | Operation timeout |
| `internal` | Internal server error |
| `unavailable` | Service unavailable |

### Client-Side Error Handling

```kotlin
try {
    val result = functions
        .getHttpsCallable("translateText")
        .call(request)
        .await()
    // Handle success
} catch (e: FirebaseFunctionsException) {
    when (e.code) {
        FirebaseFunctionsException.Code.UNAUTHENTICATED -> {
            // Redirect to login
        }
        FirebaseFunctionsException.Code.INVALID_ARGUMENT -> {
            // Show validation error
        }
        FirebaseFunctionsException.Code.INTERNAL -> {
            // Show generic error
        }
        else -> {
            // Show generic error
        }
    }
}
```

---

## 📊 Rate Limits

### Firebase Quotas

**Free Tier (Spark)**:
- 2M invocations/month
- 400K GB-seconds compute
- 200K CPU-seconds

**Blaze Plan**:
- $0.40 per million invocations (after free tier)
- Compute charged based on memory/time

### Best Practices

1. **Cache Aggressively**: Cache translations locally
2. **Batch Requests**: Translate multiple targets at once
3. **Debounce User Input**: Wait for user to stop typing

---

**Next**: [Troubleshooting →](Troubleshooting.md)
