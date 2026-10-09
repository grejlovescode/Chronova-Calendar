package com.example.data.model

enum class EventCategory(val displayName: String, val iconName: String) {
    PUBLIC_HOLIDAY("Public Holiday", "ic_flag"),
    RELIGIOUS("Religious & Spiritual", "ic_temple"),
    CULTURAL_FESTIVAL("Cultural Festival", "ic_festival"),
    HISTORICAL("Historical Milestone", "ic_history"),
    ASTRONOMICAL("Astronomical Event", "ic_stars"),
    INTERNATIONAL_OBSERVANCE("International Day", "ic_globe"),
    PERSONAL("Personal Event", "ic_person")
}

enum class TraditionType(val displayName: String) {
    ALL("All Traditions"),
    ISLAM("Islam"),
    CHRISTIANITY_WESTERN("Christianity (Western)"),
    CHRISTIANITY_ORTHODOX("Christianity (Eastern / Orthodox)"),
    HINDUISM("Hinduism"),
    BUDDHISM("Buddhism"),
    JUDAISM("Judaism"),
    SIKHISM("Sikhism"),
    SHINTO_TAOIST("Shinto & Taoism"),
    BAHAI("Baháʼí"),
    ZOROASTRIAN("Zoroastrian & Persian"),
    INDIGENOUS("Indigenous & Traditional"),
    SECULAR("Secular & National"),
    MULTI_FAITH("Interfaith & Global"),
    NONE("General")
}

enum class CalendarSystem(val displayName: String, val epochDescription: String) {
    GREGORIAN("Gregorian", "Standard civil solar calendar"),
    HIJRI("Islamic Hijri (AH)", "Pure lunar calendar based on moon phases"),
    HEBREW("Hebrew (AM)", "Lunisolar calendar marking Jewish cycles"),
    HINDU_LUNISOLAR("Hindu (Saka/Vikram)", "Lunisolar calendar based on tithis and nakshatras"),
    BUDDHIST("Buddhist Era (BE)", "Solar calendar epoch from Gautama Buddha's parinirvana"),
    SOLAR_HIJRI("Solar Hijri (SH)", "Astronomical solar calendar used in Iran and Afghanistan"),
    CHINESE_LUNISOLAR("Chinese Lunisolar", "Agricultural lunar calendar marking solar terms")
}

enum class DateCertainty(val displayName: String, val badgeLabel: String) {
    CONFIRMED_FIXED("Fixed Gregorian Date", "Confirmed"),
    CONFIRMED_CALCULATED("Calculated Solis/Equinox", "Astronomical"),
    ESTIMATED_MOON_SIGHTING("Subject to Lunar Moon Sighting", "Estimated (Moon Sighting)"),
    REGIONAL_VARIATION("Regional Observance Date", "Regional Variance")
}

data class UniversalEvent(
    val id: String,
    val title: String,
    val originalOrNativeTitle: String? = null,
    val description: String,
    val year: Int = 2026,
    val month: Int, // 1 to 12
    val day: Int,   // 1 to 31
    val endMonth: Int? = null,
    val endDay: Int? = null,
    val countryCodes: List<String>, // "GLOBAL" or ISO codes like "JP", "US", "IN", "PK", "SA"
    val primaryRegionOrState: String? = null,
    val category: EventCategory,
    val tradition: TraditionType = TraditionType.NONE,
    val isOfficialPublicHoliday: Boolean = false,
    val certainty: DateCertainty = DateCertainty.CONFIRMED_FIXED,
    val calculationExplanation: String? = null,
    val historicalBackground: String? = null,
    val traditionsAndCustoms: String? = null,
    val traditionalFoodOrSymbols: String? = null,
    val greetingPhrase: String? = null,
    val officialSourceAttribution: String = "Chronova Global Chronology & Verified Sources",
    val secondaryCalendarInfo: String? = null,
    val tags: List<String> = emptyList()
) {
    val isMultiDay: Boolean
        get() = endMonth != null && endDay != null && (endMonth != month || endDay != day)

    fun matchesDate(m: Int, d: Int): Boolean {
        if (month == m && day == d) return true
        if (isMultiDay && endMonth != null && endDay != null) {
            if (month == endMonth) {
                return m == month && d in day..endDay
            } else {
                // spans across month boundary
                if (m == month && d >= day) return true
                if (m == endMonth && d <= endDay) return true
            }
        }
        return false
    }
}

data class CountryInfo(
    val code: String,
    val name: String,
    val nativeName: String,
    val flagEmoji: String,
    val region: String,
    val primaryCalendar: CalendarSystem = CalendarSystem.GREGORIAN,
    val secondaryCalendars: List<CalendarSystem> = emptyList(),
    val publicHolidayCount: Int = 12,
    val officialSources: String = "Official Gazette & National Ministry of Culture",
    val description: String = ""
)
