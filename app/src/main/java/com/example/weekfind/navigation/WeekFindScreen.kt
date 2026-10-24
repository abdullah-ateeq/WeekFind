package com.example.weekfind.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class WeekFindScreen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    SPLASH(route = "splash", title = "Splash", icon = null),
    HOME(route = "home", title = "Home", icon = Icons.Default.CalendarToday),
    HISTORY(route = "history", title = "History", icon = Icons.Default.History),
    SETTINGS(route = "settings", title = "Settings", icon = Icons.Default.Settings),
    ABOUT(route = "about", title = "About", icon = Icons.Default.Info);

    companion object {
        val bottomBarScreens = listOf(HOME, HISTORY, SETTINGS, ABOUT)
    }
}
