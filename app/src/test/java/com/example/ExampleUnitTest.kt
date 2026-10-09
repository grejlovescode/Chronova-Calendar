package com.example

import com.example.data.model.EventCategory
import com.example.data.repository.CalendarCalculationUtils
import com.example.data.repository.GlobalEventDataset
import com.example.data.repository.GlobalEventsProvider
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun testMoonPhaseCalculation() {
        val testDate = LocalDate.of(2026, 10, 8)
        val moonPhase = CalendarCalculationUtils.calculateMoonPhase(testDate)
        assertNotNull(moonPhase)
        assertTrue(moonPhase.phaseName.isNotBlank())
        assertTrue(moonPhase.illuminationPercent in 0..100)
        assertTrue(moonPhase.emoji.isNotBlank())
    }

    @Test
    fun testAuxiliaryCalendarDateCalculations() {
        val date = LocalDate.of(2026, 1, 1)
        val hijri = CalendarCalculationUtils.getApproximateHijriDate(date)
        val hebrew = CalendarCalculationUtils.getApproximateHebrewDate(date)
        val buddhist = CalendarCalculationUtils.getBuddhistEraDate(date)
        val solarHijri = CalendarCalculationUtils.getSolarHijriDate(date)

        assertTrue("Hijri date should contain AH", hijri.contains("AH"))
        assertTrue("Hebrew date should contain AM", hebrew.contains("AM"))
        assertTrue("Buddhist date should be 2569 BE", buddhist.contains("2569 BE"))
        assertTrue("Solar Hijri should contain SH", solarHijri.contains("SH"))
    }

    @Test
    fun testCountdownFormatting() {
        val today = LocalDate.of(2026, 10, 8)
        assertEquals("Today", CalendarCalculationUtils.formatCountdown(2026, 10, 8, today))
        assertEquals("Tomorrow", CalendarCalculationUtils.formatCountdown(2026, 10, 9, today))
        assertEquals("In 5 days", CalendarCalculationUtils.formatCountdown(2026, 10, 13, today))
        assertEquals("Yesterday", CalendarCalculationUtils.formatCountdown(2026, 10, 7, today))
    }

    @Test
    fun testGlobalEventsDataIntegrity() {
        val events = GlobalEventsProvider.allEvents
        assertTrue("Should have extensive events catalog", events.size >= 25)

        events.forEach { event ->
            assertTrue("Event ID must not be blank", event.id.isNotBlank())
            assertTrue("Event title must not be blank", event.title.isNotBlank())
            assertTrue("Month must be 1..12", event.month in 1..12)
            assertTrue("Day must be 1..31", event.day in 1..31)
            assertTrue("Must have country codes", event.countryCodes.isNotEmpty())
            assertTrue("Description must be detailed", event.description.length >= 20)
        }
    }

    @Test
    fun testMultiDayEventMatching() {
        val newYearJapan = GlobalEventsProvider.allEvents.find { it.id == "shogatsu_japan" }
        assertNotNull(newYearJapan)
        assertTrue(newYearJapan!!.matchesDate(1, 1))
        assertTrue(newYearJapan.matchesDate(1, 2))
        assertTrue(newYearJapan.matchesDate(1, 3))
        assertFalse(newYearJapan.matchesDate(1, 4))
    }

    @Test
    fun testSupportedCountriesCount() {
        val countries = GlobalEventDataset.supportedCountries
        assertTrue("Should support over 20 nations", countries.size >= 20)
        val japan = countries.find { it.code == "JP" }
        assertNotNull(japan)
        assertEquals("Japan", japan!!.name)
    }

    @Test
    fun testBottomNavScreensAreNotNull() {
        val items = com.example.ui.navigation.Screen.bottomNavItems
        assertEquals(4, items.size)
        items.forEach { screen ->
            assertNotNull("Screen must not be null", screen)
            assertTrue("Route must not be blank", screen.route.isNotBlank())
            assertTrue("Title must not be blank", screen.title.isNotBlank())
        }
    }
}
