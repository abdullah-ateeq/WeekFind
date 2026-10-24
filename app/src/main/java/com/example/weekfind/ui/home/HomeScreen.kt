package com.example.weekfind.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weekfind.calendar.MonthCalendarGrid
import com.example.weekfind.formatter.DateFormatter
import com.example.weekfind.model.DateFormatOption
import com.example.weekfind.ui.WeekFindUiState
import com.example.weekfind.util.HapticHelper
import com.example.weekfind.util.ShareAndClipboardHelper
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: WeekFindUiState,
    onSelectDate: (LocalDate) -> Unit,
    onSelectYesterday: () -> Unit,
    onSelectToday: () -> Unit,
    onSelectTomorrow: () -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleMonthCalendar: () -> Unit,
    onEnterDateIso: (String) -> Boolean,
    onClearError: () -> Unit,
    onClearStatusMessage: () -> Unit,
    onSaveDate: () -> Unit,
    getCopyWeekdayText: (Locale) -> String,
    getCopyResultText: (Locale) -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val locale = Locale.getDefault()
    val dateFormatter = remember { DateFormatter() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val hapticsEnabled = uiState.settings.hapticFeedback
    val animationsEnabled = uiState.settings.animations

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showManualDateDialog by rememberSaveable { mutableStateOf(false) }
    var manualDateText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.statusMessage) {
        val message = uiState.statusMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            onClearStatusMessage()
        }
    }

    val formattedDate = dateFormatter.formatDate(
        date = uiState.selectedDate,
        formatOption = uiState.settings.dateFormat,
        locale = locale
    )
    val defaultFormattedDate = dateFormatter.formatDate(
        date = uiState.selectedDate,
        formatOption = DateFormatOption.DAY_MONTH_YEAR,
        locale = locale
    )
    val fullWeekday = dateFormatter.formatWeekdayFull(uiState.weekday, locale)
    val uppercaseWeekday = dateFormatter.formatWeekdayUppercase(uiState.weekday, locale)
    val shortWeekday = dateFormatter.formatWeekdayAbbreviation(uiState.weekday, locale)
    val fullMonth = dateFormatter.formatMonthFull(uiState.selectedDate, locale)
    val weekStartName = dateFormatter.formatWeekdayFull(uiState.weekStart, locale)
    val ordinalWeek = dateFormatter.formatOrdinal(uiState.weekOfYear)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Identity Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "WeekFind",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Find the weekday for any date.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Large Date Selector Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Selected date, $defaultFormattedDate."
                    }
                    .clickable {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        showDatePicker = true
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT DATE",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = fullWeekday,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                HapticHelper.performLightTick(view, hapticsEnabled)
                                showDatePicker = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Change Date")
                        }

                        OutlinedButton(
                            onClick = {
                                HapticHelper.performLightTick(view, hapticsEnabled)
                                manualDateText = uiState.selectedDate.toString()
                                onClearError()
                                showManualDateDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Enter Date")
                        }
                    }
                }
            }

            // Quick Actions: YESTERDAY / TODAY / TOMORROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onSelectYesterday()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "YESTERDAY",
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }

                FilledTonalButton(
                    onClick = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onSelectToday()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "TODAY",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onSelectTomorrow()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "TOMORROW",
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }
            }

            // Stepper Controls: PREVIOUS / NEXT DAY & PREVIOUS / NEXT WEEK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onPreviousDay()
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Previous day."
                    }
                ) {
                    Text(
                        text = "‹ PREVIOUS DAY",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            onPreviousWeek()
                        }
                    ) {
                        Text(
                            text = "‹ WEEK",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            onNextWeek()
                        }
                    ) {
                        Text(
                            text = "WEEK ›",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(
                    onClick = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onNextDay()
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Next day."
                    }
                ) {
                    Text(
                        text = "NEXT DAY ›",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Main Result & Premium Date Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "$fullWeekday."
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AnimatedContent(
                    targetState = uiState.selectedDate,
                    transitionSpec = {
                        if (animationsEnabled) {
                            fadeIn(animationSpec = tween(220)) togetherWith
                                fadeOut(animationSpec = tween(180))
                        } else {
                            EnterTransition.None togetherWith ExitTransition.None
                        }
                    },
                    label = "weekday_result_transition"
                ) { targetDate ->
                    val targetWeekdayFull = dateFormatter.formatWeekdayFull(targetDate.dayOfWeek, locale)
                    val targetWeekdayUpper = dateFormatter.formatWeekdayUppercase(targetDate.dayOfWeek, locale)
                    val targetWeekdayShort = dateFormatter.formatWeekdayAbbreviation(targetDate.dayOfWeek, locale)
                    val targetMonth = dateFormatter.formatMonthFull(targetDate, locale)
                    val targetFormattedDate = dateFormatter.formatDate(
                        date = targetDate,
                        formatOption = uiState.settings.dateFormat,
                        locale = locale
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "WEEKDAY",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = targetWeekdayShort,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Text(
                            text = targetWeekdayUpper,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = targetWeekdayFull,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${targetDate.dayOfMonth} $targetMonth",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = targetDate.year.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )

                        if (uiState.settings.dateFormat != DateFormatOption.DAY_MONTH_YEAR) {
                            Text(
                                text = targetFormattedDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                        )

                        if (uiState.settings.showWeekNumber) {
                            Text(
                                text = "This is the $ordinalWeek week of the year.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        val daysRemainingLabel = if (uiState.daysRemaining == 1) {
                            "1 day remaining"
                        } else {
                            "${uiState.daysRemaining} days remaining"
                        }
                        Text(
                            text = "Day ${uiState.dayOfYear} of ${targetDate.year} • $daysRemainingLabel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Day / Month / Year Three-Column Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoStatTile(
                    label = "DAY",
                    value = uiState.selectedDate.dayOfMonth.toString(),
                    modifier = Modifier.weight(1f)
                )
                InfoStatTile(
                    label = "MONTH",
                    value = fullMonth,
                    modifier = Modifier.weight(1.4f)
                )
                InfoStatTile(
                    label = "YEAR",
                    value = uiState.selectedDate.year.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Compact Date Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$fullWeekday, $formattedDate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Day ${uiState.dayOfYear} of ${uiState.yearLength}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Week ${uiState.weekOfYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (uiState.isLeapYear) "Leap year" else "Non-leap year",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // THIS WEEK View
            if (uiState.settings.showWeekView && uiState.currentWeek.isNotEmpty()) {
                WeekViewSection(
                    weekDates = uiState.currentWeek,
                    selectedDate = uiState.selectedDate,
                    dateFormatter = dateFormatter,
                    locale = locale,
                    onSelectDate = { clickedDate ->
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onSelectDate(clickedDate)
                    },
                    onPreviousWeek = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onPreviousWeek()
                    },
                    onNextWeek = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onNextWeek()
                    }
                )
            }

            // DATE DETAILS Section
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "DATE DETAILS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    DetailRow(label = "Weekday", value = fullWeekday)
                    DetailRow(label = "Day", value = uiState.selectedDate.dayOfMonth.toString())
                    DetailRow(label = "Month", value = fullMonth)
                    DetailRow(label = "Year", value = uiState.selectedDate.year.toString())

                    if (uiState.settings.showDayOfYear) {
                        DetailRow(label = "Day of year", value = uiState.dayOfYear.toString())
                    }
                    if (uiState.settings.showWeekNumber) {
                        DetailRow(label = "Week of year", value = uiState.weekOfYear.toString())
                    }
                    if (uiState.settings.showIsoWeek) {
                        DetailRow(label = "ISO week", value = uiState.isoWeek.toString())
                        if (uiState.isoWeekYear != uiState.selectedDate.year) {
                            DetailRow(
                                label = "ISO week-year",
                                value = uiState.isoWeekYear.toString()
                            )
                        }
                    }
                    DetailRow(label = "Week start", value = weekStartName)
                    if (uiState.settings.showLeapYear) {
                        DetailRow(
                            label = "Leap year",
                            value = if (uiState.isLeapYear) "Yes" else "No"
                        )
                    }
                    DetailRow(label = "Days elapsed", value = uiState.daysElapsed.toString())
                    DetailRow(label = "Days remaining", value = uiState.daysRemaining.toString())
                }
            }

            // YEAR PROGRESS Section
            if (uiState.settings.showYearProgress) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.selectedDate.year.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${(uiState.yearProgress * 100f).toInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { uiState.yearProgress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )

                        Text(
                            text = "${uiState.dayOfYear} / ${uiState.yearLength} days",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expandable MONTH CALENDAR Section
            if (uiState.settings.showMonthCalendar) {
                MonthCalendarSection(
                    monthCalendar = uiState.monthCalendar,
                    selectedDate = uiState.selectedDate,
                    expanded = uiState.isMonthCalendarExpanded,
                    animationsEnabled = animationsEnabled,
                    dateFormatter = dateFormatter,
                    locale = locale,
                    onToggleExpanded = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onToggleMonthCalendar()
                    },
                    onPreviousMonth = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onPreviousMonth()
                    },
                    onNextMonth = {
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onNextMonth()
                    },
                    onSelectDate = { date ->
                        HapticHelper.performLightTick(view, hapticsEnabled)
                        onSelectDate(date)
                    }
                )
            }

            // Action Buttons: COPY WEEKDAY, COPY RESULT, SAVE, SHARE
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            val text = getCopyWeekdayText(locale)
                            ShareAndClipboardHelper.copyToClipboard(context, "Weekday", text)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Copied weekday: $text")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "COPY WEEKDAY")
                    }

                    OutlinedButton(
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            val text = getCopyResultText(locale)
                            ShareAndClipboardHelper.copyToClipboard(context, "WeekFind Result", text)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Full result copied to clipboard")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "COPY RESULT")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            onSaveDate()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "SAVE")
                    }

                    FilledTonalButton(
                        onClick = {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            val text = getCopyResultText(locale)
                            ShareAndClipboardHelper.shareResult(context, text)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "SHARE")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Material 3 DatePickerDialog
    if (showDatePicker) {
        val initialMillis = remember(uiState.selectedDate) {
            uiState.selectedDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val pickedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            onSelectDate(pickedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Optional Manual ISO Date Entry Dialog ("Enter Date")
    if (showManualDateDialog) {
        AlertDialog(
            onDismissRequest = {
                showManualDateDialog = false
                onClearError()
            },
            title = { Text("Enter Date") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter a calendar date in YYYY-MM-DD format (for example, 2026-10-24).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = manualDateText,
                        onValueChange = {
                            manualDateText = it
                            if (uiState.errorMessage != null) {
                                onClearError()
                            }
                        },
                        label = { Text("Date (YYYY-MM-DD)") },
                        placeholder = { Text("2026-10-24") },
                        singleLine = true,
                        isError = uiState.errorMessage != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val success = onEnterDateIso(manualDateText)
                        if (success) {
                            HapticHelper.performLightTick(view, hapticsEnabled)
                            showManualDateDialog = false
                        }
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showManualDateDialog = false
                        onClearError()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InfoStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun WeekViewSection(
    weekDates: List<LocalDate>,
    selectedDate: LocalDate,
    dateFormatter: DateFormatter,
    locale: Locale,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "THIS WEEK",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                weekDates.forEach { date ->
                    val isSelected = date == selectedDate
                    val dayAbbrev = dateFormatter.formatWeekdayAbbreviation(date.dayOfWeek, locale)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                }
                            )
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onSelectDate(date) }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = dayAbbrev,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (isSelected) "[${date.dayOfMonth}]" else date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onPreviousWeek) {
                    Text(
                        text = "‹ Previous Week",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                TextButton(onClick = onNextWeek) {
                    Text(
                        text = "Next Week ›",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarSection(
    monthCalendar: MonthCalendarGrid,
    selectedDate: LocalDate,
    expanded: Boolean,
    animationsEnabled: Boolean,
    dateFormatter: DateFormatter,
    locale: Locale,
    onToggleExpanded: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() }
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "MONTH CALENDAR",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dateFormatter.formatYearMonth(monthCalendar.yearMonth, locale),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse month calendar" else "Expand month calendar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = if (animationsEnabled) {
                    expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250))
                } else {
                    EnterTransition.None
                },
                exit = if (animationsEnabled) {
                    shrinkVertically(animationSpec = tween(220)) + fadeOut(animationSpec = tween(200))
                } else {
                    ExitTransition.None
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Month Navigation Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPreviousMonth,
                            modifier = Modifier.semantics {
                                contentDescription = "Previous month."
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null
                            )
                        }

                        Text(
                            text = dateFormatter.formatYearMonth(monthCalendar.yearMonth, locale),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = onNextMonth,
                            modifier = Modifier.semantics {
                                contentDescription = "Next month."
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }

                    // Weekday Headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        monthCalendar.weekdayHeaders.forEach { dayOfWeek ->
                            Text(
                                text = dateFormatter.formatWeekdayShortTitle(dayOfWeek, locale),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Month Grid with restrained horizontal transition
                    AnimatedContent(
                        targetState = monthCalendar,
                        transitionSpec = {
                            if (!animationsEnabled) {
                                EnterTransition.None togetherWith ExitTransition.None
                            } else if (targetState.yearMonth > initialState.yearMonth) {
                                (slideInHorizontally(tween(240)) { it / 4 } + fadeIn(tween(240))) togetherWith
                                    (slideOutHorizontally(tween(200)) { -it / 4 } + fadeOut(tween(200)))
                            } else if (targetState.yearMonth < initialState.yearMonth) {
                                (slideInHorizontally(tween(240)) { -it / 4 } + fadeIn(tween(240))) togetherWith
                                    (slideOutHorizontally(tween(200)) { it / 4 } + fadeOut(tween(200)))
                            } else {
                                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                            }
                        },
                        label = "month_calendar_transition"
                    ) { grid ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            grid.weeks.forEach { week ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    week.forEach { cell ->
                                        val cellDate = cell.date
                                        if (cellDate == null) {
                                            Spacer(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                            )
                                        } else {
                                            val isSelected = cellDate == selectedDate
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            MaterialTheme.colorScheme.surface
                                                        }
                                                    )
                                                    .then(
                                                        if (isSelected) {
                                                            Modifier.border(
                                                                width = 1.5.dp,
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                                shape = CircleShape
                                                            )
                                                        } else {
                                                            Modifier
                                                        }
                                                    )
                                                    .clickable { onSelectDate(cellDate) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = cellDate.dayOfMonth.toString(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
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
