package com.example.weekfind.calendar

import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth

class DateNavigator(
    private val clock: Clock = Clock.systemDefaultZone()
) {

    fun previousDay(date: LocalDate): LocalDate = date.minusDays(1)

    fun nextDay(date: LocalDate): LocalDate = date.plusDays(1)

    fun previousWeek(date: LocalDate): LocalDate = date.minusDays(7)

    fun nextWeek(date: LocalDate): LocalDate = date.plusDays(7)

    /**
     * Moves to the previous month, clamping the day of month to the last valid day
     * of the target month when the current day does not exist in that month
     * (e.g., 31 March -> 28/29 February).
     */
    fun previousMonth(date: LocalDate): LocalDate = date.minusMonths(1)

    /**
     * Moves to the next month, clamping the day of month to the last valid day
     * of the target month when the current day does not exist in that month
     * (e.g., 31 January 2026 -> 28 February 2026; 31 January 2024 -> 29 February 2024).
     */
    fun nextMonth(date: LocalDate): LocalDate = date.plusMonths(1)

    /**
     * Moves to [targetYearMonth], preserving the day of month or clamping to the last valid day.
     */
    fun moveToYearMonth(date: LocalDate, targetYearMonth: YearMonth): LocalDate {
        val clampedDay = minOf(date.dayOfMonth, targetYearMonth.lengthOfMonth())
        return targetYearMonth.atDay(clampedDay)
    }

    fun today(): LocalDate = LocalDate.now(clock)

    fun yesterday(): LocalDate = LocalDate.now(clock).minusDays(1)

    fun tomorrow(): LocalDate = LocalDate.now(clock).plusDays(1)
}
