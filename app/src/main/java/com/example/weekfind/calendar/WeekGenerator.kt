package com.example.weekfind.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class WeekGenerator {

    /**
     * Generates the seven consecutive [LocalDate]s belonging to the week containing [selectedDate],
     * starting from [firstDayOfWeek].
     * Correctly handles month crossings, year crossings, and leap years.
     */
    @JvmOverloads
    fun generateWeek(
        selectedDate: LocalDate,
        firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    ): List<LocalDate> {
        val startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        return (0L..6L).map { offset -> startOfWeek.plusDays(offset) }
    }
}
