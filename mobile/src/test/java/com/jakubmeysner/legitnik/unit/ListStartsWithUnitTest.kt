package com.jakubmeysner.legitnik.unit

import com.jakubmeysner.legitnik.util.startsWith
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ListStartsWithUnitTest {
    @Test
    fun `startsWith should return true when this starts with other`() {
        assertTrue(listOf(5, 6, 7, 8, 9).startsWith(listOf(5, 6, 7)))
    }

    @Test
    fun `startsWith should return true when this and other are equal`() {
        assertTrue(listOf(2, 3, 4).startsWith(listOf(2, 3, 4)))
    }

    @Test
    fun `startsWith should return true when other is empty`() {
        assertTrue(listOf(1, 2, 3).startsWith(emptyList()))
    }

    @Test
    fun `startsWith should return true when this and other are empty`() {
        assertTrue(emptyList<Int>().startsWith(emptyList()))
    }

    @Test
    fun `startsWith should return false when this does not start with other`() {
        assertFalse(listOf(6, 7, 8, 9).startsWith(listOf(2, 3, 4)))
    }

    @Test
    fun `startsWith should return false when this is empty and other is not`() {
        assertFalse(listOf<Int>().startsWith(listOf(6, 7, 8)))
    }
}
