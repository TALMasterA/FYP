package com.example.fyp.model

import android.graphics.Rect
import org.junit.Assert.*
import org.junit.Test

class OcrResultTest {

    @Test
    fun `Success contains text`() {
        val result = OcrResult.Success("Hello world")
        assertTrue(result is OcrResult.Success)
        assertEquals("Hello world", result.text)
    }

    @Test
    fun `Success can have empty blocks`() {
        val result = OcrResult.Success("Text", emptyList())
        assertTrue(result is OcrResult.Success)
        assertEquals("Text", result.text)
        assertEquals(0, result.blocks.size)
    }

    @Test
    fun `Success can contain text blocks`() {
        val blocks = listOf(
            TextBlock("Hello", Rect(0, 0, 100, 50), "en"),
            TextBlock("World", Rect(0, 60, 100, 110), "en")
        )
        val result = OcrResult.Success("Hello World", blocks)
        
        assertEquals("Hello World", result.text)
        assertEquals(2, result.blocks.size)
        assertEquals("Hello", result.blocks[0].text)
        assertEquals("World", result.blocks[1].text)
    }

    @Test
    fun `Error contains message`() {
        val result = OcrResult.Error("OCR processing failed")
        assertTrue(result is OcrResult.Error)
        assertEquals("OCR processing failed", result.message)
    }

    @Test
    fun `TextBlock can be created with minimal parameters`() {
        val block = TextBlock("Text")
        assertEquals("Text", block.text)
        assertNull(block.boundingBox)
        assertNull(block.language)
    }

    @Test
    fun `TextBlock can have bounding box and language`() {
        val rect = Rect(10, 20, 100, 80)
        val block = TextBlock("你好", rect, "zh")
        
        assertEquals("你好", block.text)
        assertEquals(rect, block.boundingBox)
        assertEquals("zh", block.language)
    }

    @Test
    fun `Success and Error are different types`() {
        val success: OcrResult = OcrResult.Success("text")
        val error: OcrResult = OcrResult.Error("error")
        
        assertNotEquals(success, error)
        assertTrue(success is OcrResult.Success)
        assertTrue(error is OcrResult.Error)
    }

    @Test
    fun `Success equality based on text and blocks`() {
        val blocks = listOf(TextBlock("Hello"))
        val result1 = OcrResult.Success("Hello", blocks)
        val result2 = OcrResult.Success("Hello", blocks)
        val result3 = OcrResult.Success("World", blocks)
        
        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }
}
