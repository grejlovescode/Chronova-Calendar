package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DateCertainty
import com.example.data.model.EventCategory
import com.example.data.model.UniversalEvent
import com.example.data.repository.CalendarCalculationUtils
import com.example.data.repository.MoonPhaseInfo
import com.example.ui.theme.CategoryAstronomical
import com.example.ui.theme.CategoryCultural
import com.example.ui.theme.CategoryHistorical
import com.example.ui.theme.CategoryPersonal
import com.example.ui.theme.CategoryPublicHoliday
import com.example.ui.theme.CategoryReligiousChristian
import com.example.ui.theme.CategoryReligiousHindu
import com.example.ui.theme.CategoryReligiousIslam
import com.example.ui.theme.CategoryReligiousJewish
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicViolet
import com.example.ui.theme.CosmicVioletLight
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import java.time.LocalDate

fun getCategoryColor(category: EventCategory): Color {
    return when (category) {
        EventCategory.PUBLIC_HOLIDAY -> CategoryPublicHoliday
        EventCategory.RELIGIOUS -> CategoryReligiousIslam
        EventCategory.CULTURAL_FESTIVAL -> CategoryCultural
        EventCategory.HISTORICAL -> CategoryHistorical
        EventCategory.ASTRONOMICAL -> CategoryAstronomical
        EventCategory.INTERNATIONAL_OBSERVANCE -> CosmicViolet
        EventCategory.PERSONAL -> CategoryPersonal
    }
}

@Composable
fun CategoryBadge(
    category: EventCategory,
    modifier: Modifier = Modifier
) {
    val color = getCategoryColor(category)
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = category.displayName,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CertaintyBadge(certainty: DateCertainty) {
    if (certainty == DateCertainty.CONFIRMED_FIXED) return

    val color = when (certainty) {
        DateCertainty.ESTIMATED_MOON_SIGHTING -> CelestialGold
        DateCertainty.REGIONAL_VARIATION -> CosmicVioletLight
        DateCertainty.CONFIRMED_CALCULATED -> CategoryAstronomical
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = certainty.badgeLabel,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun CountdownPill(
    eventYear: Int,
    eventMonth: Int,
    eventDay: Int,
    modifier: Modifier = Modifier
) {
    val countdown = CalendarCalculationUtils.formatCountdown(eventYear, eventMonth, eventDay)
    val isImminent = countdown == "Today" || countdown == "Tomorrow"

    Surface(
        modifier = modifier,
        color = if (isImminent) CelestialGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = if (isImminent) androidx.compose.foundation.BorderStroke(1.dp, CelestialGold) else null
    ) {
        Text(
            text = countdown,
            color = if (isImminent) CelestialGold else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MoonPhaseBadge(
    moonPhase: MoonPhaseInfo,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = moonPhase.emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = moonPhase.phaseName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${moonPhase.illuminationPercent}% Illuminated",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: UniversalEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    val accentColor = getCategoryColor(event.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadge(category = event.category)
                    CertaintyBadge(certainty = event.certainty)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CountdownPill(
                        eventYear = event.year,
                        eventMonth = event.month,
                        eventDay = event.day
                    )
                    if (onToggleFavorite != null) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) CelestialGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!event.originalOrNativeTitle.isNullOrBlank()) {
                Text(
                    text = event.originalOrNativeTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CosmicVioletLight,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!event.secondaryCalendarInfo.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = CosmicViolet.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🗓️ ${event.secondaryCalendarInfo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmicVioletLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (!event.greetingPhrase.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“${event.greetingPhrase}”",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = CelestialGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ShareableEventDialog(
    event: UniversalEvent,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shareText = buildString {
        append("✨ ${event.title}")
        if (!event.originalOrNativeTitle.isNullOrBlank()) {
            append(" (${event.originalOrNativeTitle})")
        }
        append("\n📅 Date: ${event.month}/${event.day}/${event.year}")
        if (!event.secondaryCalendarInfo.isNullOrBlank()) {
            append(" [${event.secondaryCalendarInfo}]")
        }
        append("\n🏷️ ${event.category.displayName} | ${event.tradition.displayName}")
        append("\n\n${event.description}")
        if (!event.greetingPhrase.isNullOrBlank()) {
            append("\n\n💬 Greeting: \"${event.greetingPhrase}\"")
        }
        if (!event.traditionsAndCustoms.isNullOrBlank()) {
            append("\n\n🏮 Traditions: ${event.traditionsAndCustoms}")
        }
        append("\n\n— Shared from Chronova Universal Calendar")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shareable Event Card",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Preview
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicViolet.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CategoryBadge(category = event.category)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!event.originalOrNativeTitle.isNullOrBlank()) {
                            Text(
                                text = event.originalOrNativeTitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = CosmicVioletLight
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!event.greetingPhrase.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Greeting: ${event.greetingPhrase}",
                                style = MaterialTheme.typography.labelMedium,
                                color = CelestialGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Event via"))
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                }
            }
        }
    }
}
