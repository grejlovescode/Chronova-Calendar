package com.example.ui.screens.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EventCategory
import com.example.data.model.UniversalEvent
import com.example.data.repository.CalendarCalculationUtils
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EventCard
import com.example.ui.components.getCategoryColor
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicViolet
import com.example.ui.theme.CosmicVioletLight
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onEventClick: (UniversalEvent) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // === View Mode Selector (Month, Week, Day, Year, Heatmap) ===
        item {
            ViewModeSelector(
                currentMode = uiState.viewMode,
                onModeSelected = { viewModel.setViewMode(it) }
            )
        }

        // === Month Navigation Header ===
        item {
            MonthNavigationHeader(
                currentMonth = uiState.currentMonth,
                onPrevious = { viewModel.previousMonth() },
                onNext = { viewModel.nextMonth() },
                onJumpToday = { viewModel.jumpToToday() }
            )
        }

        // === Filter Chips Row ===
        item {
            CategoryFiltersRow(
                selectedFilter = uiState.selectedCategoryFilter,
                onSelectFilter = { viewModel.setCategoryFilter(it) }
            )
        }

        // === Calendar View Content (Animated) ===
        item {
            when (uiState.viewMode) {
                CalendarViewMode.MONTH -> {
                    MonthCalendarGrid(
                        currentMonth = uiState.currentMonth,
                        selectedDate = uiState.selectedDate,
                        eventsInMonth = uiState.eventsInCurrentMonth,
                        onDateSelected = { viewModel.onDateSelected(it) },
                        secondaryCalendarType = uiState.userPreferences.secondaryCalendar
                    )
                }
                CalendarViewMode.WEEK -> {
                    WeekAgendaView(
                        selectedDate = uiState.selectedDate,
                        onDateSelected = { viewModel.onDateSelected(it) },
                        eventsInMonth = uiState.eventsInCurrentMonth,
                        onEventClick = onEventClick
                    )
                }
                CalendarViewMode.DAY -> {
                    DayAgendaView(
                        selectedDate = uiState.selectedDate,
                        eventsOnDate = uiState.eventsOnSelectedDate,
                        onEventClick = onEventClick
                    )
                }
                CalendarViewMode.YEAR -> {
                    YearOverviewGrid(
                        year = uiState.currentMonth.year,
                        onSelectMonth = { ym ->
                            viewModel.setYearMonth(ym)
                            viewModel.setViewMode(CalendarViewMode.MONTH)
                        }
                    )
                }
                CalendarViewMode.HEATMAP -> {
                    CalendarHeatmapView(
                        currentMonth = uiState.currentMonth,
                        events = uiState.eventsInCurrentMonth
                    )
                }
            }
        }

        // === Events on Selected Date (When in Month view) ===
        if (uiState.viewMode == CalendarViewMode.MONTH) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Events on ${uiState.selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${uiState.selectedDate.dayOfMonth}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${uiState.eventsOnSelectedDate.size} Observance(s) Recorded",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (uiState.eventsOnSelectedDate.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "No recorded public holidays or observances for this day.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(uiState.eventsOnSelectedDate) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event) },
                        isFavorite = uiState.favoriteEventIds.contains(event.id),
                        onToggleFavorite = { viewModel.toggleFavorite(event.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewModeSelector(
    currentMode: CalendarViewMode,
    onModeSelected: (CalendarViewMode) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = currentMode.ordinal,
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        contentColor = CosmicViolet,
        divider = {}
    ) {
        CalendarViewMode.values().forEach { mode ->
            val isSelected = currentMode == mode
            Tab(
                selected = isSelected,
                onClick = { onModeSelected(mode) },
                text = {
                    Text(
                        text = mode.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CosmicVioletLight else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

@Composable
private fun MonthNavigationHeader(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onJumpToday: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onJumpToday,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "Today",
                    tint = CosmicVioletLight,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onPrevious,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onNext,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryFiltersRow(
    selectedFilter: EventCategory?,
    onSelectFilter: (EventCategory?) -> Unit
) {
    val filters = listOf(
        null to "All Observances",
        EventCategory.PUBLIC_HOLIDAY to "Public Holidays",
        EventCategory.RELIGIOUS to "Religious",
        EventCategory.CULTURAL_FESTIVAL to "Cultural",
        EventCategory.ASTRONOMICAL to "Astronomical",
        EventCategory.HISTORICAL to "Historical"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        items(filters) { (category, label) ->
            val isSelected = selectedFilter == category
            FilterChip(
                selected = isSelected,
                onClick = { onSelectFilter(category) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}

@Composable
private fun MonthCalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    eventsInMonth: List<UniversalEvent>,
    onDateSelected: (LocalDate) -> Unit,
    secondaryCalendarType: String
) {
    val daysOfWeek = remember {
        listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        )
    }

    val firstDayOfMonth = currentMonth.atDay(1)
    val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday
    val daysInMonth = currentMonth.lengthOfMonth()
    val totalCells = ((dayOfWeekOffset + daysInMonth + 6) / 7) * 7

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Days of week header
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { dayOfWeek ->
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CosmicVioletLight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar day cells
            Column {
                val weeksCount = totalCells / 7
                for (weekIndex in 0 until weeksCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (dayIndex in 0..6) {
                            val cellIndex = weekIndex * 7 + dayIndex
                            val dayNumber = cellIndex - dayOfWeekOffset + 1
                            if (dayNumber in 1..daysInMonth) {
                                val cellDate = currentMonth.atDay(dayNumber)
                                val isSelected = cellDate == selectedDate
                                val isToday = cellDate == LocalDate.now()
                                val eventsOnThisDay = eventsInMonth.filter { it.matchesDate(currentMonth.monthValue, dayNumber) }

                                MonthDayCell(
                                    date = cellDate,
                                    dayNumber = dayNumber,
                                    isSelected = isSelected,
                                    isToday = isToday,
                                    events = eventsOnThisDay,
                                    onClick = { onDateSelected(cellDate) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Box(modifier = Modifier.weight(1f).aspectRatio(0.85f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    date: LocalDate,
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    events: List<UniversalEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(0.82f)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) CosmicViolet
                else if (isToday) MaterialTheme.colorScheme.surfaceVariant
                else Color.Transparent
            )
            .then(
                if (isToday && !isSelected) Modifier.border(1.5.dp, CosmicVioletLight, RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$dayNumber",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )

            // Category indicator dots
            if (events.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    events.take(3).forEach { ev ->
                        val dotColor = getCategoryColor(ev.category)
                        Box(
                            modifier = Modifier
                                .size(4.5.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else dotColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekAgendaView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    eventsInMonth: List<UniversalEvent>,
    onEventClick: (UniversalEvent) -> Unit
) {
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() % 7)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        weekDays.forEach { date ->
            val events = eventsInMonth.filter { it.matchesDate(date.monthValue, date.dayOfMonth) }
            val isSelected = date == selectedDate
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CosmicViolet else MidnightCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDateSelected(date) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}, ${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CosmicVioletLight else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${events.size} event(s)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (events.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        events.forEach { ev ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onEventClick(ev) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(getCategoryColor(ev.category))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ev.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayAgendaView(
    selectedDate: LocalDate,
    eventsOnDate: List<UniversalEvent>,
    onEventClick: (UniversalEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Full Day Timeline • ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedDate.dayOfMonth}, ${selectedDate.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (eventsOnDate.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No events scheduled for this day.",
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            eventsOnDate.forEach { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun YearOverviewGrid(
    year: Int,
    onSelectMonth: (YearMonth) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "$year Year Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items((1..12).toList()) { monthNum ->
                val ym = YearMonth.of(year, monthNum)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectMonth(ym) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ym.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CosmicVioletLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${ym.lengthOfMonth()} days",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeatmapView(
    currentMonth: YearMonth,
    events: List<UniversalEvent>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Celebration Density Heatmap",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Festival frequency across the days of ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items((1..currentMonth.lengthOfMonth()).toList()) { day ->
                    val eventCount = events.count { it.matchesDate(currentMonth.monthValue, day) }
                    val intensityColor = when (eventCount) {
                        0 -> MaterialTheme.colorScheme.surfaceVariant
                        1 -> CosmicViolet.copy(alpha = 0.4f)
                        2 -> CosmicViolet.copy(alpha = 0.7f)
                        else -> CosmicViolet
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(intensityColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$day",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (eventCount > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
