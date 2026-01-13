# FYP (Developing login function and database implement)
 Develop in Android Studio, Android ONLY.

 Caution:
 When testing the app, connect the phone and the Computer with the USB.
 Enable USB debugging in the phone's Developer Options.
 Turn off when not testing, or you may not be able to access your online bank.

 For now, to add new UI text to UI language translate scope, you need to:
 1. Add the key to enum class in UiTexts.kt,
 2. Add the english text to val BaseUiTexts, you will manage the UI description here, 
 3. Apply the Ui text composable.

 Following MVVM (Model–View–ViewModel) structure.

 Using Firebase for log in auth. control, able to create accounts with email/password. 
 Translation history will be recorded to Firebase Firestore when the user log in an account.
 The history is planned to use for develop the learning part of the app. 
 Using gen ai API to generate learning materials.

 Using FireBase Cloud functions to protect API keys.




