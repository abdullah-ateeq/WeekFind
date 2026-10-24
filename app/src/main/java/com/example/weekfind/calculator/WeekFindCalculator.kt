package com.example.weekfind.calculator

import com.example.weekfind.domain.WeekFindResult
import com.example.weekfind.model.WeekNumberingMode
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

class WeekFindCalculator {

    @JvmOverloads
    fun calculate(
        date: LocalDate,
        locale: Locale = Locale.getDefault(),
        weekNumberingMode: WeekNumberingMode = WeekNumberingMode.LOCALE
    ): WeekFindResult {
        val weekday = date.dayOfWeek
        val configuredWeekFields = when (weekNumberingMode) {
            WeekNumberingMode.LOCALE -> WeekFields.of(locale)
            WeekNumberingMode.ISO -> WeekFields.ISO
        }

        val weekOfYear = date.get(configuredWeekFields.weekOfWeekBasedYear())
        val isoWeek = date.get(WeekFields.ISO.weekOfWeekBasedYear())
        val isoWeekYear = date.get(WeekFields.ISO.weekBasedYear())
        val dayOfYear = date.dayOfYear
        val yearLength = date.lengthOfYear()
        val daysElapsed = dayOfYear - 1
        val daysRemaining = yearLength - dayOfYear
        val isLeapYear = date.isLeapYear
        val weekStart = configuredWeekFields.firstDayOfWeek
        val yearProgress = dayOfYear.toFloat() / yearLength.toFloat()

        return WeekFindResult(
            date = date,
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
}
