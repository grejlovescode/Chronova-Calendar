package com.example.data.repository

import com.example.data.database.ChronovaDatabase
import com.example.data.database.FavoriteEntity
import com.example.data.database.PersonalEventEntity
import com.example.data.model.CountryInfo
import com.example.data.model.EventCategory
import com.example.data.model.TraditionType
import com.example.data.model.UniversalEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class EventRepository(
    private val database: ChronovaDatabase,
    private val preferencesRepository: UserPreferencesRepository
) {
    private val events = GlobalEventsProvider.allEvents
    private val countries = GlobalEventDataset.supportedCountries

    fun getAllCountries(): List<CountryInfo> = countries

    fun getCountryByCode(code: String): CountryInfo? =
        countries.find { it.code.equals(code, ignoreCase = true) }

    fun getEventById(id: String): UniversalEvent? =
        events.find { it.id == id }

    fun getEventsForDate(
        year: Int,
        month: Int,
        day: Int,
        countryCode: String? = null,
        enabledCategories: Set<String>? = null,
        enabledTraditions: Set<String>? = null
    ): List<UniversalEvent> {
        return events.filter { event ->
            event.matchesDate(month, day) &&
                matchesCountryFilter(event, countryCode) &&
                matchesCategoryFilter(event, enabledCategories) &&
                matchesTraditionFilter(event, enabledTraditions)
        }
    }

    fun getEventsForMonth(
        year: Int,
        month: Int,
        countryCode: String? = null,
        enabledCategories: Set<String>? = null,
        enabledTraditions: Set<String>? = null
    ): List<UniversalEvent> {
        return events.filter { event ->
            (event.month == month || (event.endMonth != null && event.endMonth == month)) &&
                matchesCountryFilter(event, countryCode) &&
                matchesCategoryFilter(event, enabledCategories) &&
                matchesTraditionFilter(event, enabledTraditions)
        }.sortedBy { it.day }
    }

    fun searchEvents(query: String): List<UniversalEvent> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return events.filter { event ->
            event.title.lowercase().contains(q) ||
                (event.originalOrNativeTitle?.lowercase()?.contains(q) == true) ||
                event.description.lowercase().contains(q) ||
                event.category.displayName.lowercase().contains(q) ||
                event.tradition.displayName.lowercase().contains(q) ||
                event.tags.any { it.lowercase().contains(q) } ||
                event.countryCodes.any { code ->
                    getCountryByCode(code)?.name?.lowercase()?.contains(q) == true
                }
        }
    }

    fun getUpcomingEvents(fromDate: LocalDate, count: Int = 10, countryCode: String? = null): List<UniversalEvent> {
        val currentMonth = fromDate.monthValue
        val currentDay = fromDate.dayOfMonth

        val sorted = events.filter { event ->
            matchesCountryFilter(event, countryCode)
        }.sortedWith(Comparator { a, b ->
            val daysA = daysUntil(a.month, a.day, currentMonth, currentDay)
            val daysB = daysUntil(b.month, b.day, currentMonth, currentDay)
            daysA.compareTo(daysB)
        })

        return sorted.take(count)
    }

    fun getTodayEventsWorldwide(date: LocalDate): List<UniversalEvent> {
        return events.filter { it.matchesDate(date.monthValue, date.dayOfMonth) }
    }

    fun getRandomTradition(): UniversalEvent {
        val traditions = events.filter {
            !it.traditionsAndCustoms.isNullOrBlank() || !it.traditionalFoodOrSymbols.isNullOrBlank()
        }
        return traditions.randomOrNull() ?: events.first()
    }

    private fun daysUntil(targetMonth: Int, targetDay: Int, curMonth: Int, curDay: Int): Int {
        val curDayOfYear = curMonth * 31 + curDay
        val targetDayOfYear = targetMonth * 31 + targetDay
        return if (targetDayOfYear >= curDayOfYear) {
            targetDayOfYear - curDayOfYear
        } else {
            365 + (targetDayOfYear - curDayOfYear)
        }
    }

    private fun matchesCountryFilter(event: UniversalEvent, countryCode: String?): Boolean {
        if (countryCode == null || countryCode == "WORLD" || countryCode == "GLOBAL") return true
        return event.countryCodes.contains("GLOBAL") || event.countryCodes.contains(countryCode)
    }

    private fun matchesCategoryFilter(event: UniversalEvent, enabledCategories: Set<String>?): Boolean {
        if (enabledCategories == null || enabledCategories.isEmpty()) return true
        return enabledCategories.contains(event.category.name)
    }

    private fun matchesTraditionFilter(event: UniversalEvent, enabledTraditions: Set<String>?): Boolean {
        if (enabledTraditions == null || enabledTraditions.isEmpty()) return true
        if (event.tradition == TraditionType.NONE || event.tradition == TraditionType.SECULAR || event.tradition == TraditionType.MULTI_FAITH) return true
        return enabledTraditions.contains(event.tradition.name)
    }

    // Room Favorites
    val favoritesFlow: Flow<List<FavoriteEntity>> = database.favoriteDao().getAllFavorites()

    fun isFavorite(eventId: String): Flow<Boolean> = database.favoriteDao().isFavoriteFlow(eventId)

    suspend fun toggleFavorite(eventId: String) {
        if (database.favoriteDao().isFavorite(eventId)) {
            database.favoriteDao().removeFavorite(eventId)
        } else {
            database.favoriteDao().insertFavorite(FavoriteEntity(eventId = eventId))
        }
    }

    suspend fun updateFavoriteNotes(eventId: String, notes: String?) {
        database.favoriteDao().updateNotes(eventId, notes)
    }

    // Room Personal Events
    val personalEventsFlow: Flow<List<PersonalEventEntity>> = database.personalEventDao().getAllEvents()

    fun getPersonalEventsForDate(dateString: String): Flow<List<PersonalEventEntity>> =
        database.personalEventDao().getEventsForDate(dateString)

    suspend fun createPersonalEvent(event: PersonalEventEntity): Long =
        database.personalEventDao().insertEvent(event)

    suspend fun updatePersonalEvent(event: PersonalEventEntity) =
        database.personalEventDao().updateEvent(event)

    suspend fun deletePersonalEvent(event: PersonalEventEntity) =
        database.personalEventDao().deleteEvent(event)

    suspend fun togglePersonalEventCompleted(id: Long, isCompleted: Boolean) =
        database.personalEventDao().setCompleted(id, isCompleted)
}
