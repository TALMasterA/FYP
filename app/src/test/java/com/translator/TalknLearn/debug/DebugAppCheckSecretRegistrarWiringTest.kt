package com.translator.TalknLearn.debug

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DebugAppCheckSecretRegistrarWiringTest {

    @Test
    fun debug_manifest_registers_app_check_secret_provider_component() {
        val manifest = File("src/debug/AndroidManifest.xml").readText()
        val registrar = File(
            "src/debug/java/com/translator/TalknLearn/debug/DebugAppCheckSecretRegistrar.kt"
        ).readText()

        assertTrue(
            "Debug manifest must register the App Check secret registrar through Firebase Component Discovery",
            manifest.contains("com.google.firebase.components.ComponentDiscoveryService") &&
                manifest.contains("com.translator.TalknLearn.debug.DebugAppCheckSecretRegistrar")
        )
        assertTrue(
            "Registrar must provide InternalDebugSecretProvider from the generated debug BuildConfig token",
            registrar.contains("InternalDebugSecretProvider") &&
                registrar.contains("BuildConfig.APP_CHECK_DEBUG_TOKEN")
        )
    }
}
