# Features Overview

Comprehensive documentation of all features in the FYP Translation & Learning App.

---

## 🎤 Translation Features

### Discrete Translation Mode

**Purpose**: Quick, one-off translations for short phrases and sentences

**Key Features**:
- **Voice Input**: Tap and hold microphone button to record
- **Text Input**: Type directly for text-only translation
- **Auto-Detect**: Automatically detects source language
- **Manual Selection**: Choose from 10+ languages
- **Language Swap**: Quickly swap source ↔ target languages (⇄ button)
- **Text-to-Speech**: Hear pronunciation in target language
- **Auto-Save**: Translations automatically saved to history

**Supported Languages**:
- English (US, UK, AU, CA)
- Mandarin (Simplified & Traditional Chinese)
- Cantonese
- Japanese
- Korean
- Spanish, French, German, Italian, Portuguese
- And more...

**Usage Flow**:
1. Select source and target languages (or use auto-detect)
2. Tap microphone and speak OR type text
3. View translation instantly
4. Tap speaker icon to hear pronunciation
5. Translation auto-saved to history

---

### Continuous Conversation Mode

**Purpose**: Real-time translation for multi-turn conversations

**Key Features**:
- **Live Conversation**: Continuous listening with automatic speaker detection
- **Session Management**: Conversations grouped into named sessions
- **Dual Voice Support**: Automatic language switching between speakers
- **Visual Bubbles**: Chat-like interface with speaker identification
- **Session History**: Review past conversations
- **Session Rename**: Customize session names for organization

**How It Works**:
1. Select two languages for conversation
2. Start listening
3. App automatically detects which language is being spoken
4. Translates and displays in conversation bubble format
5. Saves entire conversation as a session

**Use Cases**:
- Language learning practice
- Tourist conversations
- Business meetings
- Language exchange sessions

---

## 📚 Learning System

### AI-Generated Learning Sheets

**Purpose**: Create personalized learning materials from translation history

**Features**:
- **Smart Generation**: Uses most frequent translations
- **Language Pair Based**: Separate sheets for each language pair
- **Organized Content**: Words categorized and explained
- **AI-Powered**: Leverages generative AI for natural explanations
- **Manual Refresh**: Regenerate with updated translations

**Generation Criteria**:
- Minimum 10 translation records for language pair
- Must be logged-in user (not guest)
- One sheet per language pair
- Can regenerate every 24 hours

**Content Structure**:
- Introduction to language pair
- Common words and phrases from your history
- Example sentences
- Cultural notes

---

### Interactive Quizzes

**Purpose**: Test knowledge and earn coins through interactive quizzes

**Features**:
- **AI-Generated Questions**: Based on your learning sheet
- **Multiple Choice**: 4 options per question
- **Instant Feedback**: See correct answers immediately
- **Coin Rewards**: Earn coins for correct answers
- **Anti-Cheat System**: 5-layer verification ensures fair play
- **Regeneration**: Create new quizzes from updated sheets

**Quiz Details**:
- 10 questions per quiz
- Mix of vocabulary and comprehension
- Results summary with score
- Coins awarded based on performance

**Coin Rewards**:
- Earn coins for correct answers
- First attempt awards most coins
- Regenerated quizzes give reduced rewards
- Maximum daily limit prevents abuse

---

### Anti-Cheat Mechanisms

The coin system includes 5-layer verification:

1. **Version Deduplication**: Same sheet version = same quiz
2. **Sheet-Quiz Sync**: Quiz version must match sheet version
3. **Minimum Progress**: Require 10+ new records before regeneration
4. **Last Awarded Tracking**: Prevent duplicate awards
5. **Integrity Check**: Sheet count must exceed quiz count

---

## 💰 Coin System

### Earning Coins

**Primary Method**: Complete quizzes
- Earn coins for correct answers
- Better scores = more coins
- First-time quizzes award more

**Typical Rewards**:
- 10/10 correct: ~100 coins
- 7/10 correct: ~50 coins
- 5/10 correct: ~25 coins

### Spending Coins

**Color Palettes**:
- Ocean Blue: 50 coins
- Forest Green: 100 coins
- Sunset Orange: 150 coins
- Royal Purple: 200 coins

**History Expansion**:
- Expand from 100 to 150 records: 100 coins
- One-time purchase, permanent expansion

---

## 📖 History & Organization

### Translation History

**Features**:
- **Dual Tabs**: Separate views for discrete vs continuous
- **Search & Filter**: Keyword and language filtering
- **Favorites**: Star important translations
- **Deletion**: Remove individual records or entire sessions
- **Expandable Storage**: 100 records default, expand to 150 with coins

**Discrete History**:
- List view with source → target
- Sortable by date
- Quick access to favorites
- Replay text-to-speech

**Continuous History**:
- Session-based organization
- Conversation bubble view
- Rename sessions
- Delete entire sessions

---

### Favorites System

**Purpose**: Quick access to frequently used translations

**Features**:
- **Star to Save**: Mark translations as favorites
- **Dedicated Screen**: View all favorites in one place
- **Search**: Filter favorites by keyword
- **Quick Actions**: Copy, speak, or unfavorite
- **Unlimited Storage**: No limit on favorites

**Use Cases**:
- Common phrases for travel
- Business vocabulary
- Study materials
- Quick reference

---

### Word Bank

**Purpose**: Auto-generated vocabulary list from translation history

**Features**:
- **Automatic Generation**: Built from your translations
- **Language Organization**: Separate word banks per language
- **Custom Words**: Add your own vocabulary
- **Pronunciation**: Text-to-speech for each word
- **Detail View**: Definitions and usage examples

**Auto Word Bank**:
- Generated from translation history
- Updates when new translations added
- Extracts key vocabulary
- Groups by language

**Custom Word Bank**:
- Manually add words
- Include custom definitions
- Personal notes
- Study aids

---

## ⚙️ Settings & Customization

### User Settings

**Theme Options**:
- **Light Mode**: Bright, high contrast
- **Dark Mode**: Eye-friendly for low light
- **System Default**: Follows device setting

**Color Palettes**:
- **Default**: Standard app colors (free)
- **Unlockable Themes**: Purchase with coins
  - Ocean Blue (50 coins)
  - Forest Green (100 coins)
  - Sunset Orange (150 coins)
  - Royal Purple (200 coins)

**Font Size**:
- Adjustable from 80% to 150%
- Increments of 10%
- Affects all text in app

**Voice Settings**:
- **Per-Language Configuration**: Different voices for each language
- **Voice Selection**: Choose from available Azure voices
- **Preview**: Test voice before selecting

**Language Settings**:
- **UI Language**: 16+ languages supported
- **Primary Language**: Your native language
- **Translation Languages**: Set default source/target

---

### Profile Management

**Profile Features**:
- **Email Display**: Show registered email
- **Account Type**: Guest vs Logged-in
- **Coin Balance**: Current coins with transaction history
- **Statistics**: Usage stats (planned)

**Guest Mode**:
- **Limited Access**: Unlimited translations, limited UI language changes
- **No Cloud Sync**: Data stored locally only
- **Upgrade Prompt**: Encouraged to create account
- **One Free UI Translation**: Can change UI language once

**Logged-In Benefits**:
- **Cloud Sync**: All data synced to Firestore
- **Unlimited Features**: No restrictions
- **Learning System**: Access to sheets and quizzes
- **Coin System**: Earn and spend coins
- **Cross-Device**: Access from multiple devices

---

## 🌐 UI Language Translation

### Multi-Language Support

**Supported UI Languages** (16+):
- English, Spanish, French, German, Italian
- Portuguese, Dutch, Russian, Polish
- Chinese (Simplified & Traditional)
- Japanese, Korean
- Arabic, Hindi, Thai

**How It Works**:
1. User selects desired UI language
2. App calls Cloud Function for translation
3. All UI text translated via Azure Translator
4. Cached locally for instant access
5. Language corrections applied for accuracy

**Guest Limitations**:
- **One Free Change**: First UI language change is free
- **Cached Access**: Can switch between cached languages freely
- **Upgrade Prompt**: Encouraged to create account for unlimited changes

**Language Name Corrections**:
- Ensures proper language names in target language
- Example: "Chinese" becomes "中文" in Chinese UI
- Manual corrections for accuracy

---

## 🎯 Additional Features

### Help System

**In-App Help**:
- **Feature Guides**: Detailed explanations
- **Usage Tips**: Best practices
- **FAQ**: Common questions
- **Emoji Headers**: Easy navigation (🌐, 📚, ⚙️, etc.)

**Help Topics**:
- Translation Modes
- Learning System
- Settings & Customization
- Voice & Speech
- Coin System

---

### Permissions

**Required Permissions**:
- **Microphone**: For voice input (can be denied for text-only)
- **Internet**: For API calls (required)
- **Storage**: For caching (automatic)

**Permission Handling**:
- Graceful degradation if microphone denied
- Clear permission prompts
- Settings deep link for manual grant

---

### Privacy & Security

**Data Protection**:
- All user data in private Firestore collections
- Firestore security rules enforce access control
- API keys protected via Cloud Functions
- Audio used only for recognition, not stored

**User Control**:
- Delete history anytime
- Remove account (planned)
- Export data (planned)

---

**Next**: [Development Guide →](Development.md)
