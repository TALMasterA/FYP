# FYP (Developing Gen-AI learning part)
 Develop in Android Studio, Android ONLY.

 --------------------------------------------------------------

 Register to try this app!

 Link: https://appdistribution.firebase.dev/i/5ebf3d592700b0f7

 --------------------------------------------------------------

 Tools: Android Studio, Firebase, GitHub.

 Caution:
 When testing the app, connect the phone and the Computer with the USB.
 Enable USB debugging in the phone's Developer Options.
 Turn off when not testing, or you may not be able to access your online bank.
 You can also use android studio phone emulator for testing, if you don't have android phone.

 For now, to add new UI text to the UI language translation scope, you need to:
 1. Add the key to enum class in UiTexts.kt,
 2. Add the English text to val BaseUiTexts, you will manage the UI description here. 
 3. Apply the Ui text composable.

 Following the MVVM (Model–View–ViewModel) structure.

 Require your own Firebase project and the related SDK for testing. (google-services.json).
 Using android studio to open the project will required you to add the above file to app folder by yourself.
 Firebase login is required.

 Using Firebase for login authentication. control, able to create accounts with email/password. 
 Translation history will be recorded to Firebase Firestore when the user logs into an account.
 The history is use for develop the learning part of the app. 
 Using gen ai API to generate learning materials.
 Quiz is available per learning sheets, generate when sheet content updated.

 Using Firebase Cloud functions to protect API keys (backend).

--------------------------------------------------------------
 
 Commands:
 
 git pull --ff-only : For updating the main branch after pushing work on another computer (Same branch)

 firebase deploy --only functions : After updating the index.ts (backend main file) function(s), deploy it to take effect







