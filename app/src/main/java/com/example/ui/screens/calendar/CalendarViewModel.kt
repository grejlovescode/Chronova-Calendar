package com.example.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EventCategory
import com.example.data.model.UniversalEvent
import com.example.data.repository.EventRepository
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
import java.time.YearMonth

enum class CalendarViewMode(val title: String) {
    MONTH("Month"),
    WEEK("Week"),
    DAY("Day Agenda"),
    YEAR("Year"),
    HEATMAP("Density")
}

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val viewMode: CalendarViewMode = CalendarViewMode.MONTH,
    val selectedCategoryFilter: EventCategory? = null,
    val eventsInCurrentMonth: List<UniversalEvent> = emptyList(),
    val eventsOnSelectedDate: List<UniversalEvent> = emptyList(),
    val favoriteEventIds: Set<String> = emptySet(),
    val userPreferences: UserPreferences = UserPreferences()
)

private data class CalendarInternalFilter(
    val month: YearMonth,
    val selectedDate: LocalDate,
    val viewMode: CalendarViewMode,
    val categoryFilter: EventCategory?
)

class CalendarViewModel(
    private val repository: EventRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _viewMode = MutableStateFlow(CalendarViewMode.MONTH)
    private val _selectedCategoryFilter = MutableStateFlow<EventCategory?>(null)

    private val _calendarFilterFlow = combine(
        _currentMonth,
        _selectedDate,
        _viewMode,
        _selectedCategoryFilter
    ) { month, selDate, mode, catFilter ->
        CalendarInternalFilter(month, selDate, mode, catFilter)
    }

    val uiState: StateFlow<CalendarUiState> = combine(
        _calendarFilterFlow,
        preferencesRepository.userPreferencesFlow,
        repository.favoritesFlow
    ) { filter, prefs, favs ->
        val monthEvents = repository.getEventsForMonth(
            year = filter.month.year,
            month = filter.month.monthValue,
            countryCode = if (prefs.primaryCountryCode == "WORLD") null else prefs.primaryCountryCode,
            enabledCategories = filter.categoryFilter?.let { setOf(it.name) } ?: prefs.enabledCategories,
            enabledTraditions = prefs.enabledTraditions
        )

        val dateEvents = repository.getEventsForDate(
            year = filter.selectedDate.year,
            month = filter.selectedDate.monthValue,
            day = filter.selectedDate.dayOfMonth,
            countryCode = if (prefs.primaryCountryCode == "WORLD") null else prefs.primaryCountryCode,
            enabledCategories = filter.categoryFilter?.let { setOf(it.name) } ?: prefs.enabledCategories,
            enabledTraditions = prefs.enabledTraditions
        )

        val favIds = favs.map { it.eventId }.toSet()

        CalendarUiState(
            currentMonth = filter.month,
            selectedDate = filter.selectedDate,
            viewMode = filter.viewMode,
            selectedCategoryFilter = filter.categoryFilter,
            eventsInCurrentMonth = monthEvents,
            eventsOnSelectedDate = dateEvents,
            favoriteEventIds = favIds,
            userPreferences = prefs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        if (date.year != _currentMonth.value.year || date.monthValue != _currentMonth.value.monthValue) {
            _currentMonth.value = YearMonth.of(date.year, date.monthValue)
        }
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    fun setYearMonth(ym: YearMonth) {
        _currentMonth.value = ym
    }

    fun jumpToToday() {
        val today = LocalDate.now()
        _selectedDate.value = today
        _currentMonth.value = YearMonth.of(today.year, today.monthValue)
    }

    fun setViewMode(mode: CalendarViewMode) {
        _viewMode.value = mode
    }

    fun setCategoryFilter(category: EventCategory?) {
        _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == category) null else category
    }

    fun toggleFavorite(eventId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(eventId)
        }
    }
}
