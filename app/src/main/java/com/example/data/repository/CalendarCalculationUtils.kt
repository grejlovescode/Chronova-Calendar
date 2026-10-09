package com.example.data.repository

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.floor
import kotlin.math.sin

data class MoonPhaseInfo(
    val phaseName: String,
    val illuminationPercent: Int,
    val emoji: String,
    val description: String
)

object CalendarCalculationUtils {

    /**
     * Calculate approximate Moon Phase for any Gregorian date.
     * Uses synodic lunar month of 29.53058867 days anchored to known New Moon (Jan 6, 2000).
     */
    fun calculateMoonPhase(date: LocalDate): MoonPhaseInfo {
        val baseDate = LocalDate.of(2000, 1, 6)
        val days = ChronoUnit.DAYS.between(baseDate, date).toDouble()
        val synodicMonth = 29.53058867
        val phase = ((days % synodicMonth) + synodicMonth) % synodicMonth
        val illumination = ((1.0 - kotlin.math.cos(phase * 2 * Math.PI / synodicMonth)) / 2.0 * 100).toInt()

        return when {
            phase < 1.84 -> MoonPhaseInfo("New Moon", illumination, "🌑", "Conjunction with the Sun, invisible from Earth")
            phase < 5.53 -> MoonPhaseInfo("Waxing Crescent", illumination, "🌒", "First crescent visible in western twilight sky")
            phase < 9.22 -> MoonPhaseInfo("First Quarter", illumination, "🌓", "Right half illuminated, waxing toward full")
            phase < 12.91 -> MoonPhaseInfo("Waxing Gibbous", illumination, "🌔", "More than half illuminated, approaching full moon")
            phase < 16.61 -> MoonPhaseInfo("Full Moon", illumination, "🌕", "Fully illuminated opposite the Sun")
            phase < 20.30 -> MoonPhaseInfo("Waning Gibbous", illumination, "🌖", "Beginning to decrease, visible past midnight")
            phase < 23.99 -> MoonPhaseInfo("Last Quarter", illumination, "🌗", "Left half illuminated in early dawn sky")
            phase < 27.68 -> MoonPhaseInfo("Waning Crescent", illumination, "🌘", "Thin sliver in pre-dawn twilight")
            else -> MoonPhaseInfo("New Moon", illumination, "🌑", "Conjunction with the Sun, invisible from Earth")
        }
    }

    /**
     * Approximate Islamic Hijri date for given Gregorian date (Kuwaiti / Umm al-Qura algorithm).
     * Returns string like "18 Rabi' al-Awwal 1448 AH"
     */
    fun getApproximateHijriDate(date: LocalDate): String {
        val y = date.year
        val m = date.monthValue
        val d = date.dayOfMonth

        val julianDay = toJulianDay(y, m, d)
        // Epoch of Islamic Calendar: July 16, 622 CE (Julian day 1948439.5)
        val islamicEpoch = 1948439.5
        val daysSinceEpoch = julianDay - islamicEpoch

        val hijriYear = floor((30 * daysSinceEpoch + 10646) / 10631).toInt()
        val yearStartJulian = floor((hijriYear - 1) * 354.367 + islamicEpoch)
        val daysInYear = (julianDay - yearStartJulian).toInt()

        val hijriMonthNames = listOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )

        val monthIndex = ((daysInYear / 29.5).toInt()).coerceIn(0, 11)
        val dayInMonth = ((daysInYear % 29.5).toInt() + 1).coerceIn(1, 30)

        return "$dayInMonth ${hijriMonthNames[monthIndex]} $hijriYear AH"
    }

    /**
     * Approximate Hebrew date (simplified algorithmic representation).
     */
    fun getApproximateHebrewDate(date: LocalDate): String {
        val hebrewYear = date.year + 3760
        val monthNames = listOf(
            "Tishrei", "Cheshvan", "Kislev", "Tevet", "Shevat", "Adar",
            "Nisan", "Iyar", "Sivan", "Tammuz", "Av", "Elul"
        )
        // Offset mapping based on Gregorian month (approximate civil Hebrew correspondence)
        val idx = (date.monthValue + 3) % 12
        return "${date.dayOfMonth} ${monthNames[idx]} $hebrewYear AM"
    }

    /**
     * Buddhist Era date (Gregorian year + 543).
     */
    fun getBuddhistEraDate(date: LocalDate): String {
        val beYear = date.year + 543
        return "${date.dayOfMonth} ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} $beYear BE"
    }

    /**
     * Solar Hijri / Persian calendar date (Farvardin, Ordibehesht, etc.).
     */
    fun getSolarHijriDate(date: LocalDate): String {
        val shYear = if (date.monthValue < 3 || (date.monthValue == 3 && date.dayOfMonth < 21)) date.year - 622 else date.year - 621
        val months = listOf(
            "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
            "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
        )
        val idx = when {
            date.monthValue in 4..12 -> date.monthValue - 4
            else -> date.monthValue + 8
        }.coerceIn(0, 11)
        return "${date.dayOfMonth} ${months[idx]} $shYear SH"
    }

    private fun toJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /**
     * Formats days until a target date: "Today", "Tomorrow", "In 14 days", or "X days ago"
     */
    fun formatCountdown(targetYear: Int, targetMonth: Int, targetDay: Int, fromDate: LocalDate = LocalDate.now()): String {
        val target = LocalDate.of(targetYear, targetMonth, targetDay)
        val diff = ChronoUnit.DAYS.between(fromDate, target)
        return when {
            diff == 0L -> "Today"
            diff == 1L -> "Tomorrow"
            diff in 2..30 -> "In $diff days"
            diff in 31..365 -> "In ${diff / 30} months"
            diff == -1L -> "Yesterday"
            diff < -1L -> "${-diff} days ago"
            else -> "In ${diff / 365} years"
        }
    }
}
