package com.example.weekfind.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weekfind.model.AppThemeMode
import com.example.weekfind.model.DateFormatOption
import com.example.weekfind.model.WeekFindSettings
import com.example.weekfind.model.WeekNumberingMode
import com.example.weekfind.util.HapticHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: WeekFindSettings,
    onUpdateThemeMode: (AppThemeMode) -> Unit,
    onUpdateDynamicColor: (Boolean) -> Unit,
    onUpdateWeekNumberingMode: (WeekNumberingMode) -> Unit,
    onUpdateShowWeekNumber: (Boolean) -> Unit,
    onUpdateShowIsoWeek: (Boolean) -> Unit,
    onUpdateShowDayOfYear: (Boolean) -> Unit,
    onUpdateShowYearProgress: (Boolean) -> Unit,
    onUpdateShowLeapYear: (Boolean) -> Unit,
    onUpdateDateFormat: (DateFormatOption) -> Unit,
    onUpdateShowMonthCalendar: (Boolean) -> Unit,
    onUpdateShowWeekView: (Boolean) -> Unit,
    onUpdateHapticFeedback: (Boolean) -> Unit,
    onUpdateAnimations: (Boolean) -> Unit,
    onUpdateConfirmDelete: (Boolean) -> Unit,
    onUpdateRestoreLastDate: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onResetSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val hapticsEnabled = settings.hapticFeedback

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Customize appearance, calendar rules, display, and behavior.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // APPEARANCE
        SettingsSectionCard(title = "APPEARANCE") {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = settings.themeMode == mode,
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            onUpdateThemeMode(mode)
                        },
                        label = { Text(mode.displayName) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            SettingsToggleRow(
                title = "Dynamic Color",
                subtitle = "Use Material You colors on supported devices",
                checked = settings.dynamicColor,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateDynamicColor(it)
                }
            )
        }

        // CALENDAR SETTINGS
        SettingsSectionCard(title = "CALENDAR SETTINGS") {
            Text(
                text = "Week Numbering",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WeekNumberingMode.entries.forEach { mode ->
                    FilterChip(
                        selected = settings.weekNumberingMode == mode,
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            onUpdateWeekNumberingMode(mode)
                        },
                        label = { Text(mode.displayName) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            SettingsToggleRow(
                title = "Show Week Number",
                subtitle = "Display the week-of-year number",
                checked = settings.showWeekNumber,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateShowWeekNumber(it)
                }
            )

            SettingsToggleRow(
                title = "Show ISO Week",
                subtitle = "Display ISO 8601 week and week-year",
                checked = settings.showIsoWeek,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateShowIsoWeek(it)
                }
            )

            SettingsToggleRow(
                title = "Show Day of Year",
                subtitle = "Display numerical day position (1–365/366)",
                checked = settings.showDayOfYear,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateShowDayOfYear(it)
                }
            )

            SettingsToggleRow(
                title = "Show Year Progress",
                subtitle = "Display visual progress bar for the selected year",
                checked = settings.showYearProgress,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateShowYearProgress(it)
                }
            )

            SettingsToggleRow(
                title = "Show Leap Year",
                subtitle = "Indicate whether the selected year is a leap year",
                checked = settings.showLeapYear,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateShowLeapYear(it)
                }
            )
        }

        // DISPLAY SETTINGS
        SettingsSectionCard(title = "DISPLAY SETTINGS") {
            Text(
                text = "Date Format",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DateFormatOption.entries.forEach { option ->
                    FilterChip(
                        selected = settings.dateFormat == option,
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            onUpdateDateFormat(option)
                        },
                        label = { Text(option.example) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            SettingsToggleRow(
                title = "Show Month Calendar",
                subtitle = "Show expandable month calendar on Home",
                checked = settings.showMonthCalendar,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateShowMonthCalendar(it)
                }
            )

            SettingsToggleRow(
                title = "Show Week View",
                subtitle = "Show 7-day week strip on Home",
                checked = settings.showWeekView,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateShowWeekView(it)
                }
            )
        }

        // BEHAVIOR SETTINGS
        SettingsSectionCard(title = "BEHAVIOR SETTINGS") {
            SettingsToggleRow(
                title = "Haptic Feedback",
                subtitle = "Light tactile feedback on date navigation and actions",
                checked = settings.hapticFeedback,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, it)
                    onUpdateHapticFeedback(it)
                }
            )

            SettingsToggleRow(
                title = "Animations",
                subtitle = "Smooth transitions when changing dates and months",
                checked = settings.animations,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateAnimations(it)
                }
            )

            SettingsToggleRow(
                title = "Confirm Delete",
                subtitle = "Ask for confirmation before deleting saved dates",
                checked = settings.confirmDelete,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateConfirmDelete(it)
                }
            )

            SettingsToggleRow(
                title = "Restore Last Date",
                subtitle = "Remember the last selected date across app restarts",
                checked = settings.restoreLastDate,
                onCheckedChange = {
                    HapticHelper.performLightTick(view, hapticsEnabled)
                    onUpdateRestoreLastDate(it)
                }
            )
        }

        // DATA & RESET ACTIONS
        SettingsSectionCard(title = "DATA & DEFAULTS") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        showResetSettingsDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset Settings")
                }

                OutlinedButton(
                    onClick = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        showClearHistoryDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear History")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear all saved dates?") },
            text = {
                Text("This will permanently remove all saved dates from History.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text(
                        text = "Clear",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings") },
            text = {
                Text("Restore all settings to their default values? Your saved history and selected date will be preserved.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
