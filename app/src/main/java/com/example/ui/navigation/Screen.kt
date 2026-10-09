package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Public
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "Today", Icons.Filled.Home, Icons.Outlined.Home)
    data object Calendar : Screen("calendar", "Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    data object Explorer : Screen("explorer", "Countries", Icons.Filled.Public, Icons.Outlined.Public)
    data object Discovery : Screen("discovery", "Discovery", Icons.Filled.Explore, Icons.Outlined.Explore)

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Dashboard, Calendar, Explorer, Discovery)
    }
}
