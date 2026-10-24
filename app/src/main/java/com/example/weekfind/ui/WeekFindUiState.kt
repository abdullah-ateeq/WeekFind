package com.example.weekfind.ui

import com.example.weekfind.calendar.MonthCalendarGrid
import com.example.weekfind.domain.WeekFindResult
import com.example.weekfind.model.WeekFindHistory
import com.example.weekfind.model.WeekFindSettings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class WeekFindUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekday: DayOfWeek = LocalDate.now().dayOfWeek,
    val weekOfYear: Int = 0,
    val isoWeek: Int = 0,
    val isoWeekYear: Int = 0,
    val dayOfYear: Int = 0,
    val daysElapsed: Int = 0,
    val daysRemaining: Int = 0,
    val yearLength: Int = 365,
    val isLeapYear: Boolean = false,
    val history: List<WeekFindHistory> = emptyList(),
    val errorMessage: String? = null,
    val weekStart: DayOfWeek = DayOfWeek.MONDAY,
    val yearProgress: Float = 0f,
    val currentWeek: List<LocalDate> = emptyList(),
    val displayedYearMonth: YearMonth = YearMonth.from(selectedDate),
    val monthCalendar: MonthCalendarGrid = MonthCalendarGrid.empty(),
    val isMonthCalendarExpanded: Boolean = false,
    val settings: WeekFindSettings = WeekFindSettings(),
    val statusMessage: String? = null
) {
    fun toResult(): WeekFindResult = WeekFindResult(
        date = selectedDate,
        weekday = weekday,
        weekOfYear = weekOfYear,
        isoWeek = isoWeek,
        isoWeekYear = isoWeekYear,
        dayOfYear = dayOfYear,
        daysElapsed = daysElapsed,
        daysRemaining = daysRemaining,
        yearLength = yearLength,
        isLeapYear = isLeapYear,
        weekStart = weekStart,
        yearProgress = yearProgress
    )
}
