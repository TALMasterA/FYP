package com.translator.TalknLearn.data.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Unit tests for [RequestIdInterceptor] using a synthetic [Interceptor.Chain]
 * (no network, no mockwebserver dep).
 */
class RequestIdInterceptorTest {

    private fun fakeChain(initial: Request): TestChain = TestChain(initial)

    @Test
    fun adds_x_request_id_header_when_missing() {
        val chain = fakeChain(Request.Builder().url("https://example.test/ping").build())
        RequestIdInterceptor().intercept(chain)

        val header = chain.proceeded?.header(RequestIdInterceptor.HEADER)
        assertNotNull("X-Request-ID header missing", header)
        assertEquals(header, UUID.fromString(header).toString())
    }

    @Test
    fun preserves_caller_supplied_x_request_id() {
        val explicit = "custom-correlation-123"
        val chain = fakeChain(
            Request.Builder()
                .url("https://example.test/ping")
                .header(RequestIdInterceptor.HEADER, explicit)
                .build()
        )
        RequestIdInterceptor().intercept(chain)
        assertEquals(explicit, chain.proceeded?.header(RequestIdInterceptor.HEADER))
    }

    @Test
    fun new_uuid_per_request() {
        val interceptor = RequestIdInterceptor()
        val c1 = fakeChain(Request.Builder().url("https://example.test/a").build())
        val c2 = fakeChain(Request.Builder().url("https://example.test/b").build())
        interceptor.intercept(c1)
        interceptor.intercept(c2)
        val first = c1.proceeded?.header(RequestIdInterceptor.HEADER)
        val second = c2.proceeded?.header(RequestIdInterceptor.HEADER)
        assertNotNull(first); assertNotNull(second)
        assertTrue("expected distinct request IDs", first != second)
    }

    private class TestChain(private val initial: Request) : Interceptor.Chain {
        var proceeded: Request? = null
        override fun request(): Request = initial
        override fun proceed(request: Request): Response {
            proceeded = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(204)
                .message("No Content")
                .body("".toResponseBody("text/plain".toMediaType()))
                .build()
        }
        override fun call() = throw UnsupportedOperationException()
        override fun connection() = null
        override fun connectTimeoutMillis() = 0
        override fun withConnectTimeout(timeout: Int, unit: TimeUnit) = this
        override fun readTimeoutMillis() = 0
        override fun withReadTimeout(timeout: Int, unit: TimeUnit) = this
        override fun writeTimeoutMillis() = 0
        override fun withWriteTimeout(timeout: Int, unit: TimeUnit) = this
    }
}
