package com.example.fyp.core

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for FontSizeUtils (validateScale function).
 * Tests the scale clamping logic between MIN_SCALE (0.8) and MAX_SCALE (1.5).
 */
class FontSizeUtilsTest {

    // ── Constants ────────────────────────────────────────────────────

    @Test
    fun `MIN_SCALE is 0_8`() {
        assertEquals(0.8f, MIN_SCALE)
    }

    @Test
    fun `MAX_SCALE is 1_5`() {
        assertEquals(1.5f, MAX_SCALE)
    }

    // ── validateScale ───────────────────────────────────────────────

    @Test
    fun `validateScale returns value within range unchanged`() {
        assertEquals(1.0f, validateScale(1.0f))
    }

    @Test
    fun `validateScale returns MIN_SCALE for values below minimum`() {
        assertEquals(0.8f, validateScale(0.5f))
    }

    @Test
    fun `validateScale returns MAX_SCALE for values above maximum`() {
        assertEquals(1.5f, validateScale(2.0f))
    }

    @Test
    fun `validateScale accepts boundary value MIN_SCALE`() {
        assertEquals(0.8f, validateScale(0.8f))
    }

    @Test
    fun `validateScale accepts boundary value MAX_SCALE`() {
        assertEquals(1.5f, validateScale(1.5f))
    }

    @Test
    fun `validateScale clamps zero to MIN_SCALE`() {
        assertEquals(0.8f, validateScale(0.0f))
    }

    @Test
    fun `validateScale clamps negative to MIN_SCALE`() {
        assertEquals(0.8f, validateScale(-1.0f))
    }

    @Test
    fun `validateScale clamps very large value to MAX_SCALE`() {
        assertEquals(1.5f, validateScale(100.0f))
    }

    @Test
    fun `validateScale handles typical user values`() {
        assertEquals(0.9f, validateScale(0.9f))
        assertEquals(1.0f, validateScale(1.0f))
        assertEquals(1.1f, validateScale(1.1f))
        assertEquals(1.2f, validateScale(1.2f))
        assertEquals(1.3f, validateScale(1.3f))
        assertEquals(1.4f, validateScale(1.4f))
    }

    @Test
    fun `validateScale handles just below MIN_SCALE`() {
        assertEquals(0.8f, validateScale(0.79f))
    }

    @Test
    fun `validateScale handles just above MAX_SCALE`() {
        assertEquals(1.5f, validateScale(1.51f))
    }
}
