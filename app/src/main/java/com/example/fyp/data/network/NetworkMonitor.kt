package com.example.fyp.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.fyp.core.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FIX 2.4: Network connectivity monitor that provides real-time network status.
 *
 * Observes system connectivity changes and exposes:
 * - [isOnline] — snapshot of current network state
 * - [observeNetworkState] — Flow that emits true/false on connectivity changes
 *
 * Usage in ViewModels:
 * ```
 * val isOnline by networkMonitor.observeNetworkState().collectAsState(true)
 * ```
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isOnline = MutableStateFlow(checkCurrentConnectivity())

    /** Snapshot of current connectivity — true if any network is available. */
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    /**
     * Observe network state changes as a Flow.
     * Emits `true` when network becomes available, `false` when lost.
     */
    fun observeNetworkState(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
                trySend(true)
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                val hasInternet = capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                _isOnline.value = hasInternet
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (e: Exception) {
            AppLogger.e("NetworkMonitor", "Failed to register network callback", e)
            trySend(true) // Assume online if we can't check
        }

        awaitClose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                AppLogger.d("NetworkMonitor", "Unregister callback failed: ${e.message}")
            }
        }
    }.distinctUntilChanged()

    private fun checkCurrentConnectivity(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
