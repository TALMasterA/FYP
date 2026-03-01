package com.example.fyp.core.security

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

/**
 * Certificate pinning configuration for OkHttp clients.
 * Pins TLS certificates for known API hosts to prevent MITM attacks.
 *
 * **How to update pins:**
 * 1. Obtain the SHA-256 fingerprint of each server's leaf or intermediate certificate.
 * 2. Add / replace the pin strings below.
 * 3. Always include at least one backup pin per host.
 *
 * Usage:
 * ```
 * val client = OkHttpClient.Builder()
 *     .applyPinning()
 *     .build()
 * ```
 */
object CertificatePinning {

    /**
     * Builds a [CertificatePinner] with pins for the project's API hosts.
     *
     * Each host should have at least two pins (primary + backup) so that
     * a certificate rotation does not cause an outage.
     */
    fun buildPinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // Firebase Functions default host
            .add(
                "us-central1-*.cloudfunctions.net",
                // Primary pin — replace with real SHA-256 hash of your certificate
                "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=",
                // Backup pin
                "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC="
            )
            // Azure Cognitive Services
            .add(
                "*.cognitiveservices.azure.com",
                "sha256/DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD=",
                "sha256/EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE="
            )
            .build()
    }

    /**
     * Extension function to apply certificate pinning to an [OkHttpClient.Builder].
     *
     * @return the same builder for chaining
     */
    fun OkHttpClient.Builder.applyPinning(): OkHttpClient.Builder =
        certificatePinner(buildPinner())
}
