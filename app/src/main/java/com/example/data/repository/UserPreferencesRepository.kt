package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chronova_preferences")

data class UserPreferences(
    val primaryCountryCode: String = "US",
    val primaryRegion: String = "",
    val followedCountries: Set<String> = setOf("US", "JP", "IN"),
    val enabledCategories: Set<String> = setOf(
        "PUBLIC_HOLIDAY", "RELIGIOUS", "CULTURAL_FESTIVAL",
        "HISTORICAL", "ASTRONOMICAL", "INTERNATIONAL_OBSERVANCE"
    ),
    val enabledTraditions: Set<String> = emptySet(), // empty means all traditions
    val secondaryCalendar: String = "HIJRI",
    val isDarkTheme: Boolean = true,
    val isOnboarded: Boolean = true,
    val startOfWeekSunday: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val PRIMARY_COUNTRY = stringPreferencesKey("primary_country")
        val PRIMARY_REGION = stringPreferencesKey("primary_region")
        val FOLLOWED_COUNTRIES = stringSetPreferencesKey("followed_countries")
        val ENABLED_CATEGORIES = stringSetPreferencesKey("enabled_categories")
        val ENABLED_TRADITIONS = stringSetPreferencesKey("enabled_traditions")
        val SECONDARY_CALENDAR = stringPreferencesKey("secondary_calendar")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val START_OF_WEEK_SUNDAY = booleanPreferencesKey("start_of_week_sunday")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            primaryCountryCode = preferences[PreferencesKeys.PRIMARY_COUNTRY] ?: "US",
            primaryRegion = preferences[PreferencesKeys.PRIMARY_REGION] ?: "",
            followedCountries = preferences[PreferencesKeys.FOLLOWED_COUNTRIES] ?: setOf("US", "JP", "IN"),
            enabledCategories = preferences[PreferencesKeys.ENABLED_CATEGORIES] ?: setOf(
                "PUBLIC_HOLIDAY", "RELIGIOUS", "CULTURAL_FESTIVAL",
                "HISTORICAL", "ASTRONOMICAL", "INTERNATIONAL_OBSERVANCE"
            ),
            enabledTraditions = preferences[PreferencesKeys.ENABLED_TRADITIONS] ?: emptySet(),
            secondaryCalendar = preferences[PreferencesKeys.SECONDARY_CALENDAR] ?: "HIJRI",
            isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: true,
            isOnboarded = preferences[PreferencesKeys.IS_ONBOARDED] ?: true,
            startOfWeekSunday = preferences[PreferencesKeys.START_OF_WEEK_SUNDAY] ?: true
        )
    }

    suspend fun setPrimaryCountry(countryCode: String, region: String = "") {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRIMARY_COUNTRY] = countryCode
            preferences[PreferencesKeys.PRIMARY_REGION] = region
            val currentFollowed = preferences[PreferencesKeys.FOLLOWED_COUNTRIES] ?: setOf("US", "JP", "IN")
            preferences[PreferencesKeys.FOLLOWED_COUNTRIES] = currentFollowed + countryCode
        }
    }

    suspend fun toggleFollowCountry(countryCode: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.FOLLOWED_COUNTRIES] ?: setOf("US", "JP", "IN")
            if (current.contains(countryCode)) {
                preferences[PreferencesKeys.FOLLOWED_COUNTRIES] = current - countryCode
            } else {
                preferences[PreferencesKeys.FOLLOWED_COUNTRIES] = current + countryCode
            }
        }
    }

    suspend fun toggleCategory(categoryName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.ENABLED_CATEGORIES] ?: setOf(
                "PUBLIC_HOLIDAY", "RELIGIOUS", "CULTURAL_FESTIVAL",
                "HISTORICAL", "ASTRONOMICAL", "INTERNATIONAL_OBSERVANCE"
            )
            if (current.contains(categoryName)) {
                if (current.size > 1) { // keep at least one
                    preferences[PreferencesKeys.ENABLED_CATEGORIES] = current - categoryName
                }
            } else {
                preferences[PreferencesKeys.ENABLED_CATEGORIES] = current + categoryName
            }
        }
    }

    suspend fun setSecondaryCalendar(calendarSystem: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SECONDARY_CALENDAR] = calendarSystem
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }

    suspend fun setStartOfWeekSunday(sundayFirst: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_OF_WEEK_SUNDAY] = sundayFirst
        }
    }

    suspend fun setOnboarded(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDED] = completed
        }
    }
}
