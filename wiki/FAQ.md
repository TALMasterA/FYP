# Frequently Asked Questions

Common questions about the FYP Translation & Learning App.

---

## 📱 General Questions

### What is FYP?

FYP is a mobile translation and language learning app that combines real-time speech  
recognition with AI-powered learning features. It helps users translate conversations  
and learn languages through interactive quizzes and personalized learning materials.

---

### What platforms are supported?

Currently **Android only**. Minimum Android 7.0 (API 24), recommended Android 14 (API 34).

iOS version is not available at this time.

---

### Is the app free?

Yes! The app is free to use with all core features available. There are optional  
in-app purchases using coins you earn through quizzes to unlock:
- Premium color palettes
- Expanded history storage

---

### Do I need to create an account?

No, but it's recommended. You can use guest mode with these limitations:
- No cloud sync (data stored locally only)
- One free UI language change
- No access to learning system and quizzes
- No coin system

Creating an account gives you:
- Cloud sync across devices
- Unlimited UI language changes
- Learning sheets and quizzes
- Coin rewards
- Full feature access

---

## 🎤 Translation Features

### What languages are supported?

**Speech Recognition & Translation**:
- English (US, UK, AU, CA)
- Mandarin Chinese (Simplified, Traditional)
- Cantonese
- Japanese, Korean
- Spanish, French, German, Italian, Portuguese
- And more (100+ via Azure Translator)

**UI Languages** (16+):
- English, Spanish, French, German, Italian
- Portuguese, Dutch, Russian, Polish
- Chinese (Simplified & Traditional)
- Japanese, Korean, Arabic, Hindi, Thai

---

### What's the difference between Discrete and Continuous modes?

**Discrete Mode**:
- One-off translations
- Tap and hold to record
- Best for: Quick phrases, vocabulary lookup
- Auto-detect available
- Each translation is separate

**Continuous Mode**:
- Real-time conversation
- Continuous listening
- Best for: Conversations, language practice
- Auto-switches between two languages
- Saves as conversation sessions

---

### Does it work offline?

**Partially**:
- ✅ View cached translations
- ✅ Browse synced history
- ✅ Switch between cached UI languages
- ❌ New translations (requires internet)
- ❌ Speech recognition (requires internet)
- ❌ Generate learning materials

Translation and speech recognition require internet connection for Azure API calls.

---

### How accurate is the translation?

Translation accuracy depends on:
- **Azure Translator API**: Industry-leading translation quality
- **Context**: Short phrases translate better than complex sentences
- **Language pair**: Common pairs (English-Spanish) work better
- **Audio quality**: Clear speech = better recognition

**For best results**:
- Speak clearly and at normal pace
- Use proper grammar
- Provide context when needed
- Review translations for nuance

---

## 📚 Learning System

### How do learning sheets work?

Learning sheets are AI-generated study materials based on your translation history:

1. You translate words/phrases normally
2. App tracks your translations by language pair
3. When you have 10+ records, you can generate a sheet
4. AI analyzes your translations and creates organized learning content
5. Content includes vocabulary, examples, and cultural notes

---

### How often can I regenerate learning sheets?

You can regenerate sheets:
- Anytime with 10+ new translation records
- After making significant additions to history
- When you want updated content

There's no hard time limit, but you need meaningful new content (10+ translations)  
for regeneration to work effectively.

---

### How do I earn coins?

**Primary method**: Complete quizzes
- Take quiz based on learning sheet
- Score 5+ correct answers (50%+)
- Earn coins based on score (10/10 = most coins)
- Higher scores = more coins

**Requirements**:
- Must be logged in (not guest)
- Must have learning sheet generated
- Need 10+ new translations since last coin award
- Quiz must match current sheet version

---

### Can I lose coins?

No, coins can only be:
- **Earned**: Through quiz completion
- **Spent**: On color palettes, history expansion

Once earned, coins stay in your account even if you:
- Retake quizzes (no penalty)
- Regenerate sheets
- Delete translations

---

### What can I buy with coins?

**Color Palettes**:
- Ocean Blue: 50 coins
- Forest Green: 100 coins
- Sunset Orange: 150 coins
- Royal Purple: 200 coins

**History Expansion**:
- Expand from 100 to 150 records: 100 coins

More items may be added in future updates.

---

## 🔐 Account & Privacy

### Is my data safe?

Yes. Security measures include:
- **Firebase Authentication**: Secure login system
- **Firestore Security Rules**: User data is private
- **Cloud Functions**: API keys never exposed to app
- **No Audio Storage**: Voice recordings not saved
- **Encrypted Transmission**: All data encrypted in transit

---

### Can others see my translations?

No. All user data is private:
- Your translation history is only visible to you
- Learning materials are personal
- Quiz results are private
- No social features that share data

---

### How do I delete my data?

**Currently**:
- **Individual Records**: Delete from History screen
- **Sessions**: Delete entire continuous conversations
- **Account Deletion**: Contact support (feature planned)

For complete data removal, contact repository maintainer.

---

### Can I export my data?

Not currently available, but planned for future version.

When implemented, you'll be able to export:
- Translation history (CSV/JSON)
- Learning materials (Markdown)
- Quiz results

---

## ⚙️ Settings & Customization

### How do I change the app language?

1. Go to Settings
2. Tap "UI Language"
3. Select desired language
4. Wait 3-5 seconds for download (first time)
5. App automatically updates

**Note**: Guest users get one free change. Login for unlimited.

---

### Can I adjust text size?

Yes! Settings → Font Size

**Options**: 80%, 90%, 100%, 110%, 120%, 130%, 140%, 150%

Affects all text throughout the app.

---

### How do I change the voice for speech?

1. Settings → Voice Settings
2. Select language
3. Choose from available voices
4. Preview voice
5. Save

Different voices available per language. Powered by Azure Speech Service.

---

### What are color palettes?

Color palettes are themes that change the app's color scheme:
- **Default** (free)
- **Ocean Blue, Forest Green, Sunset Orange, Royal Purple** (purchasable)

Once unlocked with coins, switch between them freely in Settings → Theme.

---

## 📊 History & Data

### How many translations can I save?

- **Default**: 100 records
- **Expanded**: 150 records (costs 100 coins)

When limit is reached, oldest records are automatically deleted to make room.

---

### Can I recover deleted translations?

No. Deletion is permanent. There's a confirmation dialog to prevent accidental deletion.

For important translations, use the **Favorites** feature.

---

### What are favorites?

Favorites let you bookmark important translations:
- Star any translation to favorite it
- Access from Favorites screen
- Unlimited storage (no 100-record limit)
- Synced to cloud (if logged in)
- Can search and filter

---

### How do conversation sessions work?

In Continuous Mode:
- Each conversation is saved as a "session"
- Sessions have auto-generated names (timestamp-based)
- You can rename sessions for organization
- View session history in History → Continuous tab
- Delete entire sessions if needed

---

## 🔧 Technical Questions

### Why does the app need microphone permission?

For voice input features:
- Discrete mode voice recording
- Continuous conversation listening

If denied, you can still:
- Type text for translation
- View history
- Use learning features

---

### Why does translation take so long sometimes?

Translation speed depends on:
- **Internet speed**: API calls require network
- **Server load**: Azure API response time
- **Text length**: Longer text takes more time
- **First request**: Initial API call is slower

**Typical times**:
- Short phrase: 1-2 seconds
- Learning material generation: 30-60 seconds
- Quiz generation: 30-60 seconds

---

### Can I use the app without Firebase?

No. The app requires Firebase for:
- User authentication
- Cloud data storage (Firestore)
- Backend API calls (Cloud Functions)

You need to set up your own Firebase project for development.

---

## 🐛 Common Issues

### App keeps crashing

See [Troubleshooting - App Crashes](Troubleshooting.md#issue-app-crashes-on-startup)

**Common fixes**:
- Restart app
- Clear app cache
- Reinstall app
- Check internet connection

---

### Microphone not working

See [Troubleshooting - Microphone](Troubleshooting.md#issue-microphone-not-working)

**Quick fix**:
- Check app permissions in device settings
- Restart app

---

### Translation not appearing

See [Troubleshooting - Translation](Troubleshooting.md#issue-translation-not-appearing)

**Quick fix**:
- Check internet connection
- Retry translation

---

### Can't log in

See [Troubleshooting - Authentication](Troubleshooting.md#issue-authentication-failed)

**Common causes**:
- Wrong email/password
- Account doesn't exist
- Network issues

---

## 📞 Support

### Where can I get help?

1. **Check Documentation**:
   - [Getting Started](Getting-Started.md)
   - [Troubleshooting](Troubleshooting.md)
   - This FAQ

2. **Search Existing Issues**: [GitHub Issues](https://github.com/TALMasterA/FYP/issues)

3. **Open New Issue**: Report bugs, ask questions, request features

---

### How do I report bugs?

1. Go to [GitHub Issues](https://github.com/TALMasterA/FYP/issues)
2. Click "New Issue"
3. Include:
   - Steps to reproduce
   - Expected vs actual behavior
   - Android version
   - Device model
   - Screenshots (if applicable)
   - Error messages from Logcat

---

## 📚 More Information

- **[Home](Home.md)** - Wiki homepage
- **[Getting Started](Getting-Started.md)** - Setup guide
- **[Features](Features.md)** - Complete feature list
- **[Troubleshooting](Troubleshooting.md)** - Problem solving

---

**Last Updated**: February 2026  
**Questions not answered?** [Open an issue](https://github.com/TALMasterA/FYP/issues)
