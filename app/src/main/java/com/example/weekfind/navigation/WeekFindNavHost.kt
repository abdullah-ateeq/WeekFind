package com.example.weekfind.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weekfind.ui.SplashScreen
import com.example.weekfind.ui.WeekFindViewModel
import com.example.weekfind.ui.about.AboutScreen
import com.example.weekfind.ui.history.HistoryScreen
import com.example.weekfind.ui.home.HomeScreen
import com.example.weekfind.ui.settings.SettingsScreen
import com.example.weekfind.util.HapticHelper

@Composable
fun WeekFindApp(
    viewModel: WeekFindViewModel,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WeekFindScreen.SPLASH.route
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val view = LocalView.current
    val hapticsEnabled = uiState.settings.hapticFeedback

    val showBottomBar = currentRoute != null && currentRoute != WeekFindScreen.SPLASH.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    WeekFindScreen.bottomBarScreens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    HapticHelper.performLightTick(view, hapticsEnabled)
                                    navController.navigate(screen.route) {
                                        popUpTo(WeekFindScreen.HOME.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                val icon = screen.icon
                                if (icon != null) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(WeekFindScreen.SPLASH.route) {
                SplashScreen(
                    selectedDate = uiState.selectedDate,
                    animationsEnabled = uiState.settings.animations,
                    onSplashFinished = {
                        navController.navigate(WeekFindScreen.HOME.route) {
                            popUpTo(WeekFindScreen.SPLASH.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(WeekFindScreen.HOME.route) {
                HomeScreen(
                    uiState = uiState,
                    onSelectDate = viewModel::selectDate,
                    onSelectYesterday = viewModel::selectYesterday,
                    onSelectToday = viewModel::selectToday,
                    onSelectTomorrow = viewModel::selectTomorrow,
                    onPreviousDay = viewModel::previousDay,
                    onNextDay = viewModel::nextDay,
                    onPreviousWeek = viewModel::previousWeek,
                    onNextWeek = viewModel::nextWeek,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth,
                    onToggleMonthCalendar = viewModel::toggleMonthCalendarExpanded,
                    onEnterDateIso = viewModel::enterDateIso,
                    onClearError = viewModel::clearError,
                    onClearStatusMessage = viewModel::clearStatusMessage,
                    onSaveDate = { viewModel.saveCurrentDate() },
                    getCopyWeekdayText = { locale -> viewModel.getCopyWeekdayText(locale) },
                    getCopyResultText = { locale -> viewModel.getCopyResultText(locale) }
                )
            }

            composable(WeekFindScreen.HISTORY.route) {
                HistoryScreen(
                    history = uiState.history,
                    dateFormat = uiState.settings.dateFormat,
                    confirmDelete = uiState.settings.confirmDelete,
                    hapticsEnabled = uiState.settings.hapticFeedback,
                    onRestoreEntry = { entry ->
                        viewModel.restoreHistoryEntry(entry)
                        navController.navigate(WeekFindScreen.HOME.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onDeleteEntry = viewModel::deleteHistoryEntry,
                    onClearHistory = viewModel::clearHistory
                )
            }

            composable(WeekFindScreen.SETTINGS.route) {
                SettingsScreen(
                    settings = uiState.settings,
                    onUpdateThemeMode = viewModel::updateThemeMode,
                    onUpdateDynamicColor = viewModel::updateDynamicColor,
                    onUpdateWeekNumberingMode = viewModel::updateWeekNumberingMode,
                    onUpdateShowWeekNumber = viewModel::updateShowWeekNumber,
                    onUpdateShowIsoWeek = viewModel::updateShowIsoWeek,
                    onUpdateShowDayOfYear = viewModel::updateShowDayOfYear,
                    onUpdateShowYearProgress = viewModel::updateShowYearProgress,
                    onUpdateShowLeapYear = viewModel::updateShowLeapYear,
                    onUpdateDateFormat = viewModel::updateDateFormat,
                    onUpdateShowMonthCalendar = viewModel::updateShowMonthCalendar,
                    onUpdateShowWeekView = viewModel::updateShowWeekView,
                    onUpdateHapticFeedback = viewModel::updateHapticFeedback,
                    onUpdateAnimations = viewModel::updateAnimations,
                    onUpdateConfirmDelete = viewModel::updateConfirmDelete,
                    onUpdateRestoreLastDate = viewModel::updateRestoreLastDate,
                    onClearHistory = viewModel::clearHistory,
                    onResetSettings = viewModel::resetSettings
                )
            }

            composable(WeekFindScreen.ABOUT.route) {
                AboutScreen()
            }
        }
    }
}
