package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountryInfo
import com.example.data.model.TraditionType
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicViolet
import com.example.ui.theme.CosmicVioletLight
import com.example.ui.theme.MidnightCardBorder

@Composable
fun OnboardingScreen(
    countries: List<CountryInfo>,
    onComplete: (selectedCountry: String, selectedTraditions: Set<String>, secondaryCalendar: String) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedCountryCode by remember { mutableStateOf("US") }
    var selectedTraditions by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedSecondaryCalendar by remember { mutableStateOf("HIJRI") }

    val traditionsList = listOf(
        TraditionType.ISLAM to "☪️ Islam",
        TraditionType.CHRISTIANITY_WESTERN to "✝️ Christianity (Western)",
        TraditionType.CHRISTIANITY_ORTHODOX to "☦️ Christianity (Eastern / Orthodox)",
        TraditionType.HINDUISM to "🕉️ Hinduism",
        TraditionType.BUDDHISM to "☸️ Buddhism",
        TraditionType.JUDAISM to "✡️ Judaism",
        TraditionType.SIKHISM to "☬ Sikhism",
        TraditionType.SHINTO_TAOIST to "⛩️ Shinto & East Asian Traditions",
        TraditionType.ZOROASTRIAN to "🔥 Persian & Central Asian (Nowruz)",
        TraditionType.INDIGENOUS to "🌿 Indigenous & Traditional Celebrations"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("onboarding_screen"),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                // Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..3).forEach { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    if (step >= index) CosmicViolet else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (step) {
                    1 -> {
                        // Step 1: Country & Region
                        Text(
                            text = "Step 1 of 3",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 2.sp,
                            color = CosmicVioletLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Select Your Country",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Chronova will prioritize your nation's official statutory public holidays while keeping the entire world explorable.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.height(380.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(countries) { country ->
                                val isSelected = selectedCountryCode == country.code
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) CosmicViolet.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.2.dp, CosmicViolet) else androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedCountryCode = country.code }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = country.flagEmoji, fontSize = 22.sp)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = country.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "${country.nativeName} • ${country.region}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = CelestialGold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Step 2: Traditions of Interest
                        Text(
                            text = "Step 2 of 3",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 2.sp,
                            color = CosmicVioletLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Spiritual & Cultural Heritage",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Choose traditions you wish to follow. Or choose 'Show All Religions' for universal global coverage.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Universal toggle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedTraditions.isEmpty()) CosmicViolet.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTraditions.isEmpty()) CosmicViolet else MidnightCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTraditions = emptySet() }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🌐", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Show All World Religions (Recommended)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (selectedTraditions.isEmpty()) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = CelestialGold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(traditionsList) { (tradition, label) ->
                                val isSelected = selectedTraditions.contains(tradition.name)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) CosmicViolet.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CosmicViolet) else null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedTraditions = if (isSelected) selectedTraditions - tradition.name else selectedTraditions + tradition.name
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = CosmicVioletLight)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Calendar System
                        Text(
                            text = "Step 3 of 3",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 2.sp,
                            color = CosmicVioletLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Auxiliary Calendar System",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Choose which traditional calendar to display on daily calendar cells alongside Gregorian dates.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val calendars = listOf(
                            "HIJRI" to "☪️ Islamic Hijri Calendar (AH)",
                            "HEBREW" to "✡️ Jewish Hebrew Calendar (AM)",
                            "BUDDHIST" to "☸️ Buddhist Era Calendar (BE)",
                            "SOLAR_HIJRI" to "☀️ Solar Hijri Persian Calendar (SH)"
                        )

                        calendars.forEach { (key, label) ->
                            val isSelected = selectedSecondaryCalendar == key
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) CosmicViolet.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CosmicViolet) else androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { selectedSecondaryCalendar = key }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = CelestialGold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                }

                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            onComplete(selectedCountryCode, selectedTraditions, selectedSecondaryCalendar)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("onboarding_next_button")
                ) {
                    Text(if (step == 3) "Enter Chronova" else "Continue")
                }
            }
        }
    }
}
