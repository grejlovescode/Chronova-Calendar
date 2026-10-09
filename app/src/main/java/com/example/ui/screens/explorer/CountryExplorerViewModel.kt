package com.example.ui.screens.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CountryInfo
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

data class ExplorerUiState(
    val allCountries: List<CountryInfo> = emptyList(),
    val searchQuery: String = "",
    val selectedCountry: CountryInfo? = null,
    val eventsForSelectedCountry: List<UniversalEvent> = emptyList(),
    val followedCountryCodes: Set<String> = emptySet(),
    val primaryCountryCode: String = "US"
)

class CountryExplorerViewModel(
    private val repository: EventRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCountryCode = MutableStateFlow<String?>("JP")

    val uiState: StateFlow<ExplorerUiState> = combine(
        _searchQuery,
        _selectedCountryCode,
        preferencesRepository.userPreferencesFlow
    ) { query, selectedCode, prefs ->
        val countries = repository.getAllCountries()
        val filtered = if (query.isBlank()) countries
        else countries.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.nativeName.contains(query, ignoreCase = true) ||
                it.region.contains(query, ignoreCase = true)
        }

        val targetCountry = countries.find { it.code == (selectedCode ?: prefs.primaryCountryCode) } ?: countries.first()

        // Get events for target country across all 12 months
        val countryEvents = (1..12).flatMap { month ->
            repository.getEventsForMonth(
                year = 2026,
                month = month,
                countryCode = targetCountry.code
            )
        }.distinctBy { it.id }

        ExplorerUiState(
            allCountries = filtered,
            searchQuery = query,
            selectedCountry = targetCountry,
            eventsForSelectedCountry = countryEvents,
            followedCountryCodes = prefs.followedCountries,
            primaryCountryCode = prefs.primaryCountryCode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExplorerUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectCountry(countryCode: String) {
        _selectedCountryCode.value = countryCode
    }

    fun toggleFollowCountry(countryCode: String) {
        viewModelScope.launch {
            preferencesRepository.toggleFollowCountry(countryCode)
        }
    }

    fun setAsPrimaryCountry(countryCode: String) {
        viewModelScope.launch {
            preferencesRepository.setPrimaryCountry(countryCode)
        }
    }
}
