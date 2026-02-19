package com.example.fyp.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Network cache interceptor for OkHttp
 * Implements HTTP caching to reduce unnecessary network requests
 * 
 * Performance Impact:
 * - Reduces API calls by caching successful GET requests
 * - Improves response times for cached data
 * - Reduces bandwidth usage
 * 
 * Implementation: Performance Improvement #1.4 - Network Request Caching
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Cache successful GET requests for 5 minutes
        return if (request.method == "GET" && response.isSuccessful) {
            response.newBuilder()
                .header("Cache-Control", "public, max-age=300") // 5 minutes
                .removeHeader("Pragma") // Remove no-cache directive
                .build()
        } else {
            response
        }
    }
}
