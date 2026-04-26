package com.translator.TalknLearn.debug

import com.google.firebase.appcheck.debug.InternalDebugSecretProvider
import com.google.firebase.components.Component
import com.google.firebase.components.ComponentRegistrar
import com.translator.TalknLearn.BuildConfig

class DebugAppCheckSecretRegistrar : ComponentRegistrar {
    override fun getComponents(): List<Component<*>> = listOf(
        Component.builder(InternalDebugSecretProvider::class.java)
            .factory {
                InternalDebugSecretProvider {
                    BuildConfig.APP_CHECK_DEBUG_TOKEN.ifBlank { null }
                }
            }
            .build()
    )
}
