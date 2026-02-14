package com.example.fyp.core

import org.junit.Assert.assertEquals
import org.junit.Test

class PaginationTest {

    @Test
    fun `pageCount returns 1 for empty list`() {
        assertEquals(1, pageCount(0, 10))
    }

    @Test
    fun `pageCount returns 1 for items less than page size`() {
        assertEquals(1, pageCount(5, 10))
    }

    @Test
    fun `pageCount returns 1 for items equal to page size`() {
        assertEquals(1, pageCount(10, 10))
    }

    @Test
    fun `pageCount returns 2 for items just over page size`() {
        assertEquals(2, pageCount(11, 10))
    }

    @Test
    fun `pageCount calculates correctly for multiple pages`() {
        assertEquals(2, pageCount(20, 10))
        assertEquals(3, pageCount(25, 10))
        assertEquals(5, pageCount(50, 10))
    }

    @Test
    fun `pageCount handles different page sizes`() {
        assertEquals(4, pageCount(100, 25))
        assertEquals(5, pageCount(101, 25))
        assertEquals(10, pageCount(100, 10))
        assertEquals(20, pageCount(100, 5))
    }

    @Test
    fun `pageCount handles edge cases`() {
        assertEquals(1, pageCount(1, 10))
        assertEquals(1, pageCount(1, 1))
        assertEquals(100, pageCount(100, 1))
    }

    @Test
    fun `pageCount handles negative total as 1 page`() {
        assertEquals(1, pageCount(-1, 10))
        assertEquals(1, pageCount(-100, 10))
    }

    @Test
    fun `pageCount with large numbers`() {
        assertEquals(1000, pageCount(10000, 10))
        assertEquals(100, pageCount(10000, 100))
    }
}
