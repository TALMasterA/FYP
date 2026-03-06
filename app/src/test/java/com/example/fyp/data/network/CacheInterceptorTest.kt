package com.example.fyp.data.network

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CacheInterceptorTest {

    private val interceptor = CacheInterceptor()

    private fun buildResponse(
        request: Request,
        code: Int = 200,
        headers: Map<String, String> = emptyMap()
    ): Response {
        var builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("OK")
        for ((name, value) in headers) {
            builder = builder.header(name, value)
        }
        return builder.build()
    }

    private fun mockChain(request: Request, response: Response): Interceptor.Chain {
        val chain = mock<Interceptor.Chain>()
        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(any())).thenReturn(response)
        return chain
    }

    // ── GET requests ────────────────────────────────────────────────

    @Test
    fun `successful GET adds Cache-Control header`() {
        val request = Request.Builder().url("https://example.com/data").get().build()
        val response = buildResponse(request, code = 200)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertEquals("public, max-age=300", result.header("Cache-Control"))
    }

    @Test
    fun `successful GET removes Pragma header`() {
        val request = Request.Builder().url("https://example.com/data").get().build()
        val response = buildResponse(request, code = 200, headers = mapOf("Pragma" to "no-cache"))
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertNull(result.header("Pragma"))
    }

    // ── Non-GET requests ────────────────────────────────────────────

    @Test
    fun `POST request does not get Cache-Control header added`() {
        val request = Request.Builder()
            .url("https://example.com/data")
            .post(okhttp3.RequestBody.create(null, "body"))
            .build()
        val response = buildResponse(request, code = 200)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertNull(result.header("Cache-Control"))
    }

    @Test
    fun `PUT request does not get Cache-Control header added`() {
        val request = Request.Builder()
            .url("https://example.com/data")
            .put(okhttp3.RequestBody.create(null, "body"))
            .build()
        val response = buildResponse(request, code = 200)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertNull(result.header("Cache-Control"))
    }

    @Test
    fun `DELETE request does not get Cache-Control header added`() {
        val request = Request.Builder()
            .url("https://example.com/data")
            .delete(okhttp3.RequestBody.create(null, "body"))
            .build()
        val response = buildResponse(request, code = 200)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertNull(result.header("Cache-Control"))
    }

    // ── Failed GET requests ─────────────────────────────────────────

    @Test
    fun `failed GET (404) does not get Cache-Control header`() {
        val request = Request.Builder().url("https://example.com/missing").get().build()
        val response = buildResponse(request, code = 404)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertNull(result.header("Cache-Control"))
    }

    @Test
    fun `failed GET (500) does not get Cache-Control header`() {
        val request = Request.Builder().url("https://example.com/error").get().build()
        val response = buildResponse(request, code = 500)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertNull(result.header("Cache-Control"))
    }

    @Test
    fun `failed GET (401) does not get Cache-Control header`() {
        val request = Request.Builder().url("https://example.com/auth").get().build()
        val response = buildResponse(request, code = 401)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertNull(result.header("Cache-Control"))
    }

    // ── Response code preserved ─────────────────────────────────────

    @Test
    fun `interceptor preserves successful response code`() {
        val request = Request.Builder().url("https://example.com/data").get().build()
        val response = buildResponse(request, code = 200)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertEquals(200, result.code)
    }

    @Test
    fun `interceptor preserves failed response code`() {
        val request = Request.Builder().url("https://example.com/data").get().build()
        val response = buildResponse(request, code = 404)
        val chain = mockChain(request, response)

        val result = interceptor.intercept(chain)

        assertEquals(404, result.code)
    }
}
