# FYP (Using Azure API to develop translation function)
 Develop in Android Studio, Android ONLY.

 Caution:
 When testing the app, connect the phone and the Computer with the USB.
 Enable USB debugging in the phone's Developer Options.
 Turn off when not testing, or you may not be able to access your online bank.

 Remember to enter your key in Azure Speech Service in build.gradle.kts(:app), and Sync to test.

 If BuildConfig is in red, try syncing and then clearing and rebuilding. If it's still red, just run it.

 For now, to add new UI text to UI language translate scope, you need to 1. Add the key to enum class in UiTexts.kt, 2. Add the english text to val BaseUiTexts, you will manage the UI description here, 3. Apply the Ui text composable.



