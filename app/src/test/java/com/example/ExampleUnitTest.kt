package com.example

import com.example.data.parser.QuickAddNlpParser
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testQuickAddPaymentParsing() {
        val parsed = QuickAddNlpParser.parse("Pay rent 15000 on November 5")
        assertEquals("PAYMENT", parsed.entityType)
        assertEquals(15000.0, parsed.amount ?: 0.0, 0.01)
        assertTrue(parsed.date.contains("-11-05"))
        assertEquals("Rent", parsed.category)
    }

    @Test
    fun testQuickAddMeetingParsing() {
        val parsed = QuickAddNlpParser.parse("Meeting with team at 10:00 am tomorrow")
        assertEquals("MEETING", parsed.entityType)
        assertEquals("10:00", parsed.time)
        assertEquals("Work", parsed.category)
    }

    @Test
    fun testQuickAddTripParsing() {
        val parsed = QuickAddNlpParser.parse("Trip to Goa on Dec 20")
        assertEquals("TRIP", parsed.entityType)
        assertTrue(parsed.date.contains("-12-20"))
        assertEquals("Travel", parsed.category)
    }
}
