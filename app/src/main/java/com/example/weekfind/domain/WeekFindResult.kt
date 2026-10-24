package com.example.weekfind.domain

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Domain calculation result for a given [LocalDate].
 */
data class WeekFindResult(
    val date: LocalDate,
    val weekday: DayOfWeek,
    val weekOfYear: Int,
    val isoWeek: Int,
    val isoWeekYear: Int,
    val dayOfYear: Int,
    val daysElapsed: Int,
    val daysRemaining: Int,
    val yearLength: Int,
    val isLeapYear: Boolean,
    val weekStart: DayOfWeek = DayOfWeek.MONDAY,
    val yearProgress: Float = if (yearLength > 0) dayOfYear.toFloat() / yearLength.toFloat() else 0f
)
