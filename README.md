# FYP (Developing Gen-AI learning part)
 Develop in Android Studio, Android ONLY.

 Tools: Android Studio, Firebase, Github.

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
 Firebase log in is required.

 Using Firebase for login authentication. control, able to create accounts with email/password. 
 Translation history will be recorded to Firebase Firestore when the user logs into an account.
 The history is use for develop the learning part of the app. 
 Using gen ai API to generate learning materials.

 Using Firebase Cloud functions to protect API keys (backend).

--------------------------------------------------------------
 
 Commands:
 
 git pull --ff-only  :  For update the main branch after pushed work on another computer (Same branch)





