package com.example.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.FavoriteEntity
import com.example.data.database.PersonalEventEntity
import com.example.data.model.CountryInfo
import com.example.data.model.UniversalEvent
import com.example.data.repository.CalendarCalculationUtils
import com.example.data.repository.EventRepository
import com.example.data.repository.MoonPhaseInfo
import com.example.data.repository.UserPreferences
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentDate: LocalDate = LocalDate.now(),
    val primaryCountry: CountryInfo? = null,
    val isWorldMode: Boolean = false,
    val moonPhase: MoonPhaseInfo = CalendarCalculationUtils.calculateMoonPhase(LocalDate.now()),
    val hijriDate: String = CalendarCalculationUtils.getApproximateHijriDate(LocalDate.now()),
    val hebrewDate: String = CalendarCalculationUtils.getApproximateHebrewDate(LocalDate.now()),
    val solarHijriDate: String = CalendarCalculationUtils.getSolarHijriDate(LocalDate.now()),
    val buddhistDate: String = CalendarCalculationUtils.getBuddhistEraDate(LocalDate.now()),
    val eventsForSelectedDate: List<UniversalEvent> = emptyList(),
    val upcomingCountdowns: List<UniversalEvent> = emptyList(),
    val worldEventsToday: List<UniversalEvent> = emptyList(),
    val spotlightTradition: UniversalEvent? = null,
    val personalEventsToday: List<PersonalEventEntity> = emptyList(),
    val favoriteEventIds: Set<String> = emptySet(),
    val userPreferences: UserPreferences = UserPreferences()
)

class DashboardViewModel(
    private val repository: EventRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _spotlightEvent = MutableStateFlow<UniversalEvent?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        _selectedDate,
        preferencesRepository.userPreferencesFlow,
        repository.favoritesFlow,
        _spotlightEvent
    ) { selDate, prefs, favs, spotlight ->
        val now = LocalDate.now()
        val isWorld = prefs.primaryCountryCode == "WORLD"
        val country = repository.getCountryByCode(prefs.primaryCountryCode)

        val eventsForDate = repository.getEventsForDate(
            year = selDate.year,
            month = selDate.monthValue,
            day = selDate.dayOfMonth,
            countryCode = if (isWorld) null else prefs.primaryCountryCode,
            enabledCategories = prefs.enabledCategories,
            enabledTraditions = prefs.enabledTraditions
        )

        val upcoming = repository.getUpcomingEvents(
            fromDate = now,
            count = 5,
            countryCode = if (isWorld) null else prefs.primaryCountryCode
        )

        val worldToday = repository.getTodayEventsWorldwide(selDate)

        val favIds = favs.map { it.eventId }.toSet()

        DashboardUiState(
            selectedDate = selDate,
            currentDate = now,
            primaryCountry = country,
            isWorldMode = isWorld,
            moonPhase = CalendarCalculationUtils.calculateMoonPhase(selDate),
            hijriDate = CalendarCalculationUtils.getApproximateHijriDate(selDate),
            hebrewDate = CalendarCalculationUtils.getApproximateHebrewDate(selDate),
            solarHijriDate = CalendarCalculationUtils.getSolarHijriDate(selDate),
            buddhistDate = CalendarCalculationUtils.getBuddhistEraDate(selDate),
            eventsForSelectedDate = eventsForDate,
            upcomingCountdowns = upcoming,
            worldEventsToday = worldToday,
            spotlightTradition = spotlight ?: repository.getRandomTradition(),
            favoriteEventIds = favIds,
            userPreferences = prefs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
        refreshSpotlight()
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun resetToToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun setPrimaryCountry(countryCode: String) {
        viewModelScope.launch {
            preferencesRepository.setPrimaryCountry(countryCode)
        }
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(eventId)
        }
    }

    fun refreshSpotlight() {
        _spotlightEvent.value = repository.getRandomTradition()
    }

    fun savePersonalEvent(event: PersonalEventEntity) {
        viewModelScope.launch {
            repository.createPersonalEvent(event)
        }
    }
}
