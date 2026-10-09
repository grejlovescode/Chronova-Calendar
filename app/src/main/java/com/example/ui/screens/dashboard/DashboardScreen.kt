package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CountryInfo
import com.example.data.model.UniversalEvent
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CountdownPill
import com.example.ui.components.CountryPickerSheet
import com.example.ui.components.EventCard
import com.example.ui.components.MoonPhaseBadge
import com.example.ui.components.ShareableEventDialog
import com.example.ui.screens.personal.PersonalEventDialog
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicPurple
import com.example.ui.theme.CosmicViolet
import com.example.ui.theme.CosmicVioletLight
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.StarlightCyan
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    allCountries: List<CountryInfo>,
    onEventClick: (UniversalEvent) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToExplorer: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCountryPicker by remember { mutableStateOf(false) }
    var showPersonalEventDialog by remember { mutableStateOf(false) }
    var shareEventTarget by remember { mutableStateOf<UniversalEvent?>(null) }

    if (showCountryPicker) {
        CountryPickerSheet(
            countries = allCountries,
            selectedCountryCode = uiState.userPreferences.primaryCountryCode,
            onCountrySelected = { code ->
                viewModel.setPrimaryCountry(code)
            },
            onDismiss = { showCountryPicker = false }
        )
    }

    if (showPersonalEventDialog) {
        PersonalEventDialog(
            initialDate = uiState.selectedDate,
            onDismiss = { showPersonalEventDialog = false },
            onSave = { event ->
                viewModel.savePersonalEvent(event)
            }
        )
    }

    shareEventTarget?.let { event ->
        ShareableEventDialog(
            event = event,
            onDismiss = { shareEventTarget = null }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPersonalEventDialog = true },
                containerColor = CosmicViolet,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_event_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Personal Event")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("dashboard_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // === Top Brand & Quick Actions Header ===
            item {
                DashboardHeader(
                    uiState = uiState,
                    onOpenCountryPicker = { showCountryPicker = true },
                    onOpenSearch = onOpenSearch,
                    onOpenSettings = onOpenSettings
                )
            }

            // === Multi-Calendar & Moon Phase Ribbon ===
            item {
                MultiCalendarRibbon(
                    uiState = uiState,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // === Interactive Mini Day Navigator ===
            item {
                InteractiveWeekStrip(
                    selectedDate = uiState.selectedDate,
                    onDateSelected = { viewModel.onDateSelected(it) },
                    onJumpToday = { viewModel.resetToToday() },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            // === Events for Selected Date ===
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (uiState.selectedDate == uiState.currentDate) "Today's Observances"
                            else "Events on ${uiState.selectedDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${uiState.selectedDate.dayOfMonth}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (uiState.isWorldMode) "Showing global traditions & public holidays"
                            else "Prioritizing ${uiState.primaryCountry?.name ?: "Local"} & Global events",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = CosmicViolet.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable(onClick = onNavigateToCalendar)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Full Calendar",
                                style = MaterialTheme.typography.labelSmall,
                                color = CosmicVioletLight,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = CosmicVioletLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (uiState.eventsForSelectedDate.isEmpty()) {
                item {
                    EmptyDayNotice(
                        selectedDate = uiState.selectedDate,
                        onAddEvent = { showPersonalEventDialog = true },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(uiState.eventsForSelectedDate) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event) },
                        isFavorite = uiState.favoriteEventIds.contains(event.id),
                        onToggleFavorite = { viewModel.toggleFavorite(event.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // === Cultural Spotlight / Tradition of the Day ===
            uiState.spotlightTradition?.let { tradition ->
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SpotlightTraditionCard(
                        event = tradition,
                        onClick = { onEventClick(tradition) },
                        onRefresh = { viewModel.refreshSpotlight() },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // === Coming Up Next (Countdowns) ===
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Coming Up Next",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Countdowns",
                        style = MaterialTheme.typography.labelMedium,
                        color = CelestialGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            items(uiState.upcomingCountdowns) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                    isFavorite = uiState.favoriteEventIds.contains(event.id),
                    onToggleFavorite = { viewModel.toggleFavorite(event.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }

            // === This Day Around the World (Global Discovery) ===
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "This Day Around the World",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Discover simultaneous festivals across continents",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToExplorer) {
                        Icon(Icons.Default.Public, contentDescription = "Explore Nations", tint = CosmicVioletLight)
                    }
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.worldEventsToday) { event ->
                        WorldEventMiniCard(
                            event = event,
                            onClick = { onEventClick(event) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    uiState: DashboardUiState,
    onOpenCountryPicker: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CHRONOVA",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CosmicVioletLight
                )
                Text(
                    text = "The Universal Calendar",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("search_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("settings_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Country Switcher Badge
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenCountryPicker)
                .testTag("country_switcher_badge")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (uiState.isWorldMode) "🌐" else (uiState.primaryCountry?.flagEmoji ?: "🌍"),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (uiState.isWorldMode) "World Calendar View" else (uiState.primaryCountry?.name ?: "All Countries"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (uiState.isWorldMode) "Displaying observances from all regions"
                            else "${uiState.primaryCountry?.publicHolidayCount ?: 12} Public Holidays • Tap to switch country",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CosmicViolet.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "Change",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmicVioletLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MultiCalendarRibbon(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${uiState.selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}, ${uiState.selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${uiState.selectedDate.dayOfMonth}, ${uiState.selectedDate.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Civil Gregorian Epoch",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                MoonPhaseBadge(moonPhase = uiState.moonPhase)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-calendar chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "☪️ Hijri (AH)", style = MaterialTheme.typography.labelSmall, color = CosmicVioletLight)
                        Text(
                            text = uiState.hijriDate,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "✡️ Hebrew (AM)", style = MaterialTheme.typography.labelSmall, color = CelestialGold)
                        Text(
                            text = uiState.hebrewDate,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveWeekStrip(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onJumpToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    // 7 days around current selected date
    val days = remember(selectedDate) {
        (-3..3).map { offset -> selectedDate.plusDays(offset.toLong()) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedDate.year}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (selectedDate != today) {
                Surface(
                    color = CosmicViolet.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable(onClick = onJumpToday)
                ) {
                    Text(
                        text = "Jump to Today",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmicVioletLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(days) { date ->
                val isSelected = date == selectedDate
                val isToday = date == today

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) CosmicViolet
                    else if (isToday) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) null
                    else if (isToday) androidx.compose.foundation.BorderStroke(1.5.dp, CosmicVioletLight)
                    else androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                    modifier = Modifier
                        .width(48.dp)
                        .height(68.dp)
                        .clickable { onDateSelected(date) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${date.dayOfMonth}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyDayNotice(
    selectedDate: LocalDate,
    onAddEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "✨", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No Public Holidays on this day",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "A serene open day on the calendar. Add a personal celebration, anniversary, or reminder.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onAddEvent,
                colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Event on This Date")
            }
        }
    }
}

@Composable
private fun SpotlightTraditionCard(
    event: UniversalEvent,
    onClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicViolet.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CelestialGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cultural Heritage Spotlight",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                }

                IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Next Tradition",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!event.traditionsAndCustoms.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = event.traditionsAndCustoms,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }

            if (!event.traditionalFoodOrSymbols.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "🏮 Symbols & Customs: ${event.traditionalFoodOrSymbols}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmicVioletLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorldEventMiniCard(
    event: UniversalEvent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            CategoryBadge(category = event.category)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
