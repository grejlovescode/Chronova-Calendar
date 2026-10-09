package com.example.ui.screens.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UniversalEvent
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CountdownPill
import com.example.ui.components.EventCard
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicViolet
import com.example.ui.theme.CosmicVioletLight
import com.example.ui.theme.MidnightCardBorder

@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel,
    onEventClick: (UniversalEvent) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("discovery_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "WORLD DISCOVERY",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 2.sp,
                    color = CosmicVioletLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Global Tapestry",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Uncover extraordinary ceremonies, ancient alignments, and cultural wisdom from around the globe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Global Search Bar
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search by name, tradition, symbol, food...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("discovery_search_input")
            )
        }

        // If user is searching, show search results
        if (uiState.searchQuery.isNotBlank()) {
            item {
                Text(
                    text = "Search Results (${uiState.searchResults.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (uiState.searchResults.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No events matched \"${uiState.searchQuery}\". Try searching for 'moon', 'spring', 'fasting', or 'festival'.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(uiState.searchResults) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }
            }
        } else {
            // Tab Selector
            item {
                ScrollableTabRow(
                    selectedTabIndex = uiState.currentTab.ordinal,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = CosmicViolet,
                    divider = {}
                ) {
                    DiscoveryTab.values().forEach { tab ->
                        val isSelected = uiState.currentTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.setTab(tab) },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CosmicVioletLight else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (uiState.currentTab) {
                DiscoveryTab.TODAY_WORLD -> {
                    item {
                        Text(
                            text = "Events Happening Today Worldwide (${uiState.todayEvents.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (uiState.todayEvents.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No major global public holidays today. Explore the Random Tradition Generator or Major Festivals tab!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(20.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(uiState.todayEvents) { event ->
                            EventCard(event = event, onClick = { onEventClick(event) })
                        }
                    }
                }

                DiscoveryTab.RANDOM_TRADITION -> {
                    uiState.randomTradition?.let { tradition ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, CosmicViolet)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CategoryBadge(category = tradition.category)
                                        Button(
                                            onClick = { viewModel.rollRandomTradition() },
                                            colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Discover Another")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = tradition.title,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (!tradition.originalOrNativeTitle.isNullOrBlank()) {
                                        Text(
                                            text = tradition.originalOrNativeTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = CosmicVioletLight
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = tradition.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (!tradition.traditionsAndCustoms.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Text(
                                                    text = "🏮 Ceremonial Customs & Rituals",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CelestialGold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = tradition.traditionsAndCustoms,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    if (!tradition.greetingPhrase.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Celebratory Greeting: \"${tradition.greetingPhrase}\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CelestialGold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { onEventClick(tradition) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("View Full Event Details & History", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }

                DiscoveryTab.ASTRONOMICAL -> {
                    items(uiState.astronomicalEvents) { event ->
                        EventCard(event = event, onClick = { onEventClick(event) })
                    }
                }

                DiscoveryTab.FESTIVALS -> {
                    items(uiState.majorFestivals) { event ->
                        EventCard(event = event, onClick = { onEventClick(event) })
                    }
                }

                DiscoveryTab.HISTORICAL -> {
                    items(uiState.historicalMilestones) { event ->
                        EventCard(event = event, onClick = { onEventClick(event) })
                    }
                }
            }
        }
    }
}
