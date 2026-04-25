package com.example.fyp.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

/**
 * Adds an `X-Request-ID` header to every outgoing OkHttp request so that
 * client-issued requests can be correlated with backend log entries when
 * triaging incidents (see `docs/APP_SUGGESTIONS.md` §5.1).
 *
 * The ID is a fresh random UUID per request. If a caller has already set the
 * header explicitly (e.g. for retries), the existing value is preserved.
 */
class RequestIdInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = if (original.header(HEADER) != null) {
            original
        } else {
            original.newBuilder()
                .header(HEADER, UUID.randomUUID().toString())
                .build()
        }
        return chain.proceed(request)
    }

    companion object {
        const val HEADER = "X-Request-ID"
    }
}
