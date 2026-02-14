package com.example.fyp.model

import org.junit.Assert.*
import org.junit.Test

class SpeechResultTest {

    @Test
    fun `Success contains text`() {
        val result = SpeechResult.Success("Hello world")
        assertTrue(result is SpeechResult.Success)
        assertEquals("Hello world", result.text)
    }

    @Test
    fun `Success can have empty text`() {
        val result = SpeechResult.Success("")
        assertTrue(result is SpeechResult.Success)
        assertEquals("", result.text)
    }

    @Test
    fun `Error contains message`() {
        val result = SpeechResult.Error("Network error")
        assertTrue(result is SpeechResult.Error)
        assertEquals("Network error", result.message)
    }

    @Test
    fun `Success and Error are different types`() {
        val success: SpeechResult = SpeechResult.Success("text")
        val error: SpeechResult = SpeechResult.Error("error")
        
        assertNotEquals(success, error)
        assertTrue(success is SpeechResult.Success)
        assertTrue(error is SpeechResult.Error)
    }

    @Test
    fun `Success equality based on text`() {
        val result1 = SpeechResult.Success("Hello")
        val result2 = SpeechResult.Success("Hello")
        val result3 = SpeechResult.Success("World")
        
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `Error equality based on message`() {
        val error1 = SpeechResult.Error("Network error")
        val error2 = SpeechResult.Error("Network error")
        val error3 = SpeechResult.Error("Timeout")
        
        assertEquals(error1, error2)
        assertNotEquals(error1, error3)
    }
}
