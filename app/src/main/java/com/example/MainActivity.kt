package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.database.ChronovaDatabase
import com.example.data.model.UniversalEvent
import com.example.data.repository.EventRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.navigation.Screen
import com.example.ui.notifications.ReminderNotificationHelper
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.calendar.CalendarViewModel
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dashboard.DashboardViewModel
import com.example.ui.screens.discovery.DiscoveryScreen
import com.example.ui.screens.discovery.DiscoveryViewModel
import com.example.ui.screens.eventdetail.EventDetailScreen
import com.example.ui.screens.explorer.CountryExplorerScreen
import com.example.ui.screens.explorer.CountryExplorerViewModel
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.ChronovaTheme
import com.example.ui.theme.CosmicViolet
import com.example.ui.theme.CosmicVioletLight
import kotlinx.coroutines.launch

sealed class AppDestination {
    data class Main(val screen: Screen) : AppDestination()
    data class EventDetail(val event: UniversalEvent) : AppDestination()
    object Settings : AppDestination()
    object Onboarding : AppDestination()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channels for reminders
        ReminderNotificationHelper.createNotificationChannels(this)

        val database = ChronovaDatabase.getDatabase(this)
        val preferencesRepository = UserPreferencesRepository(this)
        val eventRepository = EventRepository(database, preferencesRepository)

        val dashboardViewModel = DashboardViewModel(eventRepository, preferencesRepository)
        val calendarViewModel = CalendarViewModel(eventRepository, preferencesRepository)
        val explorerViewModel = CountryExplorerViewModel(eventRepository, preferencesRepository)
        val discoveryViewModel = DiscoveryViewModel(eventRepository)

        setContent {
            val userPreferences by preferencesRepository.userPreferencesFlow.collectAsState(
                initial = com.example.data.repository.UserPreferences()
            )
            val scope = rememberCoroutineScope()

            var currentDestination by remember {
                mutableStateOf<AppDestination>(
                    if (!userPreferences.isOnboarded) AppDestination.Onboarding
                    else AppDestination.Main(Screen.Dashboard)
                )
            }

            var previousScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

            ChronovaTheme(darkTheme = userPreferences.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val dest = currentDestination) {
                        is AppDestination.Onboarding -> {
                            OnboardingScreen(
                                countries = eventRepository.getAllCountries(),
                                onComplete = { country, traditions, secondaryCal ->
                                    scope.launch {
                                        preferencesRepository.setPrimaryCountry(country)
                                        preferencesRepository.setSecondaryCalendar(secondaryCal)
                                        preferencesRepository.setOnboarded(true)
                                        currentDestination = AppDestination.Main(Screen.Dashboard)
                                    }
                                }
                            )
                        }

                        is AppDestination.Settings -> {
                            BackHandler {
                                currentDestination = AppDestination.Main(previousScreen)
                            }
                            SettingsScreen(
                                userPreferences = userPreferences,
                                preferencesRepository = preferencesRepository,
                                onBack = { currentDestination = AppDestination.Main(previousScreen) }
                            )
                        }

                        is AppDestination.EventDetail -> {
                            val isFavorite by eventRepository.isFavorite(dest.event.id).collectAsState(initial = false)
                            BackHandler {
                                currentDestination = AppDestination.Main(previousScreen)
                            }
                            EventDetailScreen(
                                event = dest.event,
                                isFavorite = isFavorite,
                                onToggleFavorite = {
                                    scope.launch { eventRepository.toggleFavorite(dest.event.id) }
                                },
                                onBack = { currentDestination = AppDestination.Main(previousScreen) }
                            )
                        }

                        is AppDestination.Main -> {
                            val currentScreen = dest.screen
                            Scaffold(
                                bottomBar = {
                                    NavigationBar(
                                        modifier = Modifier.navigationBarsPadding(),
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        tonalElevation = androidx.compose.ui.unit.Dp(8f)
                                    ) {
                                        Screen.bottomNavItems.forEach { screen ->
                                            val isSelected = currentScreen == screen
                                            NavigationBarItem(
                                                selected = isSelected,
                                                onClick = {
                                                    previousScreen = screen
                                                    currentDestination = AppDestination.Main(screen)
                                                },
                                                icon = {
                                                    Icon(
                                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                        contentDescription = screen.title
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = screen.title,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = Color.White,
                                                    indicatorColor = CosmicViolet,
                                                    selectedTextColor = CosmicVioletLight,
                                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                modifier = Modifier.testTag("nav_${screen.route}")
                                            )
                                        }
                                    }
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    AnimatedContent(
                                        targetState = currentScreen,
                                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                                        label = "screen_transition"
                                    ) { targetScreen ->
                                        when (targetScreen) {
                                            Screen.Dashboard -> {
                                                DashboardScreen(
                                                    viewModel = dashboardViewModel,
                                                    allCountries = eventRepository.getAllCountries(),
                                                    onEventClick = { event ->
                                                        previousScreen = Screen.Dashboard
                                                        currentDestination = AppDestination.EventDetail(event)
                                                    },
                                                    onNavigateToCalendar = {
                                                        previousScreen = Screen.Calendar
                                                        currentDestination = AppDestination.Main(Screen.Calendar)
                                                    },
                                                    onNavigateToExplorer = {
                                                        previousScreen = Screen.Explorer
                                                        currentDestination = AppDestination.Main(Screen.Explorer)
                                                    },
                                                    onOpenSettings = {
                                                        previousScreen = Screen.Dashboard
                                                        currentDestination = AppDestination.Settings
                                                    },
                                                    onOpenSearch = {
                                                        previousScreen = Screen.Discovery
                                                        currentDestination = AppDestination.Main(Screen.Discovery)
                                                    }
                                                )
                                            }

                                            Screen.Calendar -> {
                                                CalendarScreen(
                                                    viewModel = calendarViewModel,
                                                    onEventClick = { event ->
                                                        previousScreen = Screen.Calendar
                                                        currentDestination = AppDestination.EventDetail(event)
                                                    }
                                                )
                                            }

                                            Screen.Explorer -> {
                                                CountryExplorerScreen(
                                                    viewModel = explorerViewModel,
                                                    onEventClick = { event ->
                                                        previousScreen = Screen.Explorer
                                                        currentDestination = AppDestination.EventDetail(event)
                                                    }
                                                )
                                            }

                                            Screen.Discovery -> {
                                                DiscoveryScreen(
                                                    viewModel = discoveryViewModel,
                                                    onEventClick = { event ->
                                                        previousScreen = Screen.Discovery
                                                        currentDestination = AppDestination.EventDetail(event)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
