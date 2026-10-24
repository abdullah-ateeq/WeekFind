package com.example.weekfind.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekfind.calculator.WeekFindCalculator
import com.example.weekfind.calendar.DateNavigator
import com.example.weekfind.calendar.MonthCalendarGenerator
import com.example.weekfind.calendar.WeekGenerator
import com.example.weekfind.formatter.DateFormatter
import com.example.weekfind.model.AppThemeMode
import com.example.weekfind.model.DateFormatOption
import com.example.weekfind.model.WeekFindHistory
import com.example.weekfind.model.WeekFindSettings
import com.example.weekfind.model.WeekNumberingMode
import com.example.weekfind.repository.WeekFindRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class WeekFindViewModel(
    private val repository: WeekFindRepository,
    private val calculator: WeekFindCalculator = WeekFindCalculator(),
    private val weekGenerator: WeekGenerator = WeekGenerator(),
    private val monthCalendarGenerator: MonthCalendarGenerator = MonthCalendarGenerator(),
    private val dateNavigator: DateNavigator = DateNavigator(),
    private val dateFormatter: DateFormatter = DateFormatter(),
    private val localeProvider: () -> Locale = { Locale.getDefault() }
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<WeekFindUiState> = _uiState.asStateFlow()

    private fun createInitialState(): WeekFindUiState {
        val settings = repository.loadSettings()
        val initialDate = repository.resolveInitialDate()
        repository.saveLastSelectedDate(initialDate)
        val history = repository.loadHistory()
        return buildStateForDate(
            date = initialDate,
            settings = settings,
            history = history,
            isMonthCalendarExpanded = false,
            errorMessage = null,
            statusMessage = null
        )
    }

    private fun buildStateForDate(
        date: LocalDate,
        settings: WeekFindSettings,
        history: List<WeekFindHistory>,
        isMonthCalendarExpanded: Boolean,
        errorMessage: String?,
        statusMessage: String?
    ): WeekFindUiState {
        val locale = localeProvider()
        val result = calculator.calculate(
            date = date,
            locale = locale,
            weekNumberingMode = settings.weekNumberingMode
        )
        val weekDates = weekGenerator.generateWeek(
            selectedDate = date,
            firstDayOfWeek = result.weekStart
        )
        val yearMonth = YearMonth.from(date)
        val monthGrid = monthCalendarGenerator.generate(
            yearMonth = yearMonth,
            firstDayOfWeek = result.weekStart
        )

        return WeekFindUiState(
            selectedDate = result.date,
            weekday = result.weekday,
            weekOfYear = result.weekOfYear,
            isoWeek = result.isoWeek,
            isoWeekYear = result.isoWeekYear,
            dayOfYear = result.dayOfYear,
            daysElapsed = result.daysElapsed,
            daysRemaining = result.daysRemaining,
            yearLength = result.yearLength,
            isLeapYear = result.isLeapYear,
            history = history,
            errorMessage = errorMessage,
            weekStart = result.weekStart,
            yearProgress = result.yearProgress,
            currentWeek = weekDates,
            displayedYearMonth = yearMonth,
            monthCalendar = monthGrid,
            isMonthCalendarExpanded = isMonthCalendarExpanded,
            settings = settings,
            statusMessage = statusMessage
        )
    }

    fun selectDate(date: LocalDate) {
        repository.saveLastSelectedDate(date)
        _uiState.update { current ->
            buildStateForDate(
                date = date,
                settings = current.settings,
                history = current.history,
                isMonthCalendarExpanded = current.isMonthCalendarExpanded,
                errorMessage = null,
                statusMessage = null
            )
        }
    }

    fun selectToday() {
        selectDate(dateNavigator.today())
    }

    fun selectYesterday() {
        selectDate(dateNavigator.yesterday())
    }

    fun selectTomorrow() {
        selectDate(dateNavigator.tomorrow())
    }

    fun previousDay() {
        selectDate(dateNavigator.previousDay(_uiState.value.selectedDate))
    }

    fun nextDay() {
        selectDate(dateNavigator.nextDay(_uiState.value.selectedDate))
    }

    fun previousWeek() {
        selectDate(dateNavigator.previousWeek(_uiState.value.selectedDate))
    }

    fun nextWeek() {
        selectDate(dateNavigator.nextWeek(_uiState.value.selectedDate))
    }

    fun previousMonth() {
        selectDate(dateNavigator.previousMonth(_uiState.value.selectedDate))
    }

    fun nextMonth() {
        selectDate(dateNavigator.nextMonth(_uiState.value.selectedDate))
    }

    fun moveToYearMonth(targetYearMonth: YearMonth) {
        selectDate(dateNavigator.moveToYearMonth(_uiState.value.selectedDate, targetYearMonth))
    }

    fun toggleMonthCalendarExpanded() {
        _uiState.update { it.copy(isMonthCalendarExpanded = !it.isMonthCalendarExpanded) }
    }

    fun setMonthCalendarExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isMonthCalendarExpanded = expanded) }
    }

    /**
     * Parses and selects a manual ISO date string (e.g., "2026-10-24").
     * Sets a friendly error message if the date is invalid (e.g., "2026-02-30").
     */
    fun enterDateIso(input: String): Boolean {
        val parseResult = dateFormatter.parseIsoDate(input)
        return parseResult.fold(
            onSuccess = { parsedDate ->
                selectDate(parsedDate)
                true
            },
            onFailure = { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: DateFormatter.INVALID_DATE_ERROR)
                }
                false
            }
        )
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun saveCurrentDate(savedAtMillis: Long = System.currentTimeMillis()) {
        val current = _uiState.value
        val updatedHistory = repository.saveHistoryEntry(
            date = current.selectedDate,
            savedAt = savedAtMillis
        )
        _uiState.update {
            it.copy(
                history = updatedHistory,
                statusMessage = "Date saved to History"
            )
        }
    }

    fun restoreHistoryEntry(entry: WeekFindHistory) {
        val restoredDate = repository.restoreHistoryDate(entry)
        selectDate(restoredDate)
    }

    fun deleteHistoryEntry(id: String) {
        val updatedHistory = repository.deleteHistoryEntry(id)
        _uiState.update { it.copy(history = updatedHistory) }
    }

    fun clearHistory() {
        val updatedHistory = repository.clearHistory()
        _uiState.update {
            it.copy(
                history = updatedHistory,
                statusMessage = "History cleared"
            )
        }
    }

    private fun applySettings(transform: (WeekFindSettings) -> WeekFindSettings) {
        val current = _uiState.value
        val newSettings = transform(current.settings)
        repository.saveSettings(newSettings)
        _uiState.update {
            buildStateForDate(
                date = it.selectedDate,
                settings = newSettings,
                history = it.history,
                isMonthCalendarExpanded = it.isMonthCalendarExpanded,
                errorMessage = it.errorMessage,
                statusMessage = null
            )
        }
    }

    fun updateThemeMode(mode: AppThemeMode) = applySettings { it.copy(themeMode = mode) }

    fun updateDynamicColor(enabled: Boolean) = applySettings { it.copy(dynamicColor = enabled) }

    fun updateWeekNumberingMode(mode: WeekNumberingMode) =
        applySettings { it.copy(weekNumberingMode = mode) }

    fun updateShowWeekNumber(enabled: Boolean) = applySettings { it.copy(showWeekNumber = enabled) }

    fun updateShowIsoWeek(enabled: Boolean) = applySettings { it.copy(showIsoWeek = enabled) }

    fun updateShowDayOfYear(enabled: Boolean) = applySettings { it.copy(showDayOfYear = enabled) }

    fun updateShowYearProgress(enabled: Boolean) =
        applySettings { it.copy(showYearProgress = enabled) }

    fun updateShowLeapYear(enabled: Boolean) = applySettings { it.copy(showLeapYear = enabled) }

    fun updateDateFormat(format: DateFormatOption) =
        applySettings { it.copy(dateFormat = format) }

    fun updateShowMonthCalendar(enabled: Boolean) =
        applySettings { it.copy(showMonthCalendar = enabled) }

    fun updateShowWeekView(enabled: Boolean) = applySettings { it.copy(showWeekView = enabled) }

    fun updateHapticFeedback(enabled: Boolean) =
        applySettings { it.copy(hapticFeedback = enabled) }

    fun updateAnimations(enabled: Boolean) = applySettings { it.copy(animations = enabled) }

    fun updateConfirmDelete(enabled: Boolean) =
        applySettings { it.copy(confirmDelete = enabled) }

    fun updateRestoreLastDate(enabled: Boolean) =
        applySettings { it.copy(restoreLastDate = enabled) }

    /**
     * Resets all settings to their defaults without deleting history or the saved date.
     */
    fun resetSettings() {
        val defaults = repository.resetSettings()
        _uiState.update { current ->
            buildStateForDate(
                date = current.selectedDate,
                settings = defaults,
                history = current.history,
                isMonthCalendarExpanded = current.isMonthCalendarExpanded,
                errorMessage = null,
                statusMessage = "Settings reset to defaults"
            )
        }
    }

    fun getCopyWeekdayText(locale: Locale = localeProvider()): String {
        return dateFormatter.formatCopyWeekday(_uiState.value.toResult(), locale)
    }

    fun getCopyResultText(locale: Locale = localeProvider()): String {
        return dateFormatter.formatCopyResult(
            result = _uiState.value.toResult(),
            formatOption = _uiState.value.settings.dateFormat,
            locale = locale
        )
    }

    class Factory(
        private val context: Context,
        private val clock: Clock = Clock.systemDefaultZone()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val prefs = context.applicationContext.getSharedPreferences(
                WeekFindRepository.PREFS_NAME,
                Context.MODE_PRIVATE
            )
            val repository = WeekFindRepository(
                sharedPreferences = prefs,
                clock = clock
            )
            return WeekFindViewModel(
                repository = repository,
                dateNavigator = DateNavigator(clock = clock)
            ) as T
        }
    }
}
