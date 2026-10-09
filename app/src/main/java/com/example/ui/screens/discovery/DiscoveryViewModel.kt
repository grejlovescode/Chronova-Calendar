package com.example.ui.screens.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EventCategory
import com.example.data.model.UniversalEvent
import com.example.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

enum class DiscoveryTab(val title: String) {
    TODAY_WORLD("Today in the World"),
    RANDOM_TRADITION("Tradition Generator"),
    ASTRONOMICAL("Celestial & Solar"),
    FESTIVALS("Major Festivals"),
    HISTORICAL("Historical Anniversaries")
}

data class DiscoveryUiState(
    val currentTab: DiscoveryTab = DiscoveryTab.TODAY_WORLD,
    val todayEvents: List<UniversalEvent> = emptyList(),
    val randomTradition: UniversalEvent? = null,
    val astronomicalEvents: List<UniversalEvent> = emptyList(),
    val majorFestivals: List<UniversalEvent> = emptyList(),
    val historicalMilestones: List<UniversalEvent> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<UniversalEvent> = emptyList(),
    val favoriteEventIds: Set<String> = emptySet()
)

class DiscoveryViewModel(
    private val repository: EventRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(DiscoveryTab.TODAY_WORLD)
    private val _randomTradition = MutableStateFlow<UniversalEvent?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<DiscoveryUiState> = combine(
        _currentTab,
        _randomTradition,
        _searchQuery,
        repository.favoritesFlow
    ) { tab, random, query, favs ->
        val now = LocalDate.now()
        val today = repository.getTodayEventsWorldwide(now)
        val astronomical = (1..12).flatMap { m ->
            repository.getEventsForMonth(2026, m, enabledCategories = setOf(EventCategory.ASTRONOMICAL.name))
        }.distinctBy { it.id }

        val festivals = (1..12).flatMap { m ->
            repository.getEventsForMonth(2026, m, enabledCategories = setOf(EventCategory.CULTURAL_FESTIVAL.name, EventCategory.RELIGIOUS.name))
        }.distinctBy { it.id }

        val historical = (1..12).flatMap { m ->
            repository.getEventsForMonth(2026, m, enabledCategories = setOf(EventCategory.HISTORICAL.name))
        }.distinctBy { it.id }

        val searchRes = if (query.isNotBlank()) repository.searchEvents(query) else emptyList()
        val favIds = favs.map { it.eventId }.toSet()

        DiscoveryUiState(
            currentTab = tab,
            todayEvents = today,
            randomTradition = random ?: repository.getRandomTradition(),
            astronomicalEvents = astronomical,
            majorFestivals = festivals,
            historicalMilestones = historical,
            searchQuery = query,
            searchResults = searchRes,
            favoriteEventIds = favIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiscoveryUiState()
    )

    init {
        rollRandomTradition()
    }

    fun setTab(tab: DiscoveryTab) {
        _currentTab.value = tab
    }

    fun onSearchQueryChanged(q: String) {
        _searchQuery.value = q
    }

    fun rollRandomTradition() {
        _randomTradition.value = repository.getRandomTradition()
    }
}
