package com.example.weekfind.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

data class MonthCalendarCell(
    val date: LocalDate?,
    val isCurrentMonth: Boolean
)

data class MonthCalendarGrid(
    val yearMonth: YearMonth,
    val firstDayOfWeek: DayOfWeek,
    val weekdayHeaders: List<DayOfWeek>,
    val weeks: List<List<MonthCalendarCell>>,
    val daysInMonth: List<LocalDate>
) {
    companion object {
        fun empty(
            yearMonth: YearMonth = YearMonth.now(),
            firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
        ): MonthCalendarGrid {
            val headers = (0L..6L).map { firstDayOfWeek.plus(it) }
            return MonthCalendarGrid(
                yearMonth = yearMonth,
                firstDayOfWeek = firstDayOfWeek,
                weekdayHeaders = headers,
                weeks = emptyList(),
                daysInMonth = emptyList()
            )
        }
    }
}

class MonthCalendarGenerator {

    /**
     * Generates the visible calendar grid for [yearMonth], respecting [firstDayOfWeek].
     * Never manually assumes Monday or Sunday.
     */
    @JvmOverloads
    fun generate(
        yearMonth: YearMonth,
        firstDayOfWeek: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    ): MonthCalendarGrid {
        val weekdayHeaders = (0L..6L).map { firstDayOfWeek.plus(it) }
        val firstOfMonth = yearMonth.atDay(1)
        val totalDays = yearMonth.lengthOfMonth()
        val daysInMonth = (1..totalDays).map { day -> yearMonth.atDay(day) }

        val leadingEmptyDays = (firstOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
        val totalCellsBeforeTrailing = leadingEmptyDays + totalDays
        val trailingEmptyDays = (7 - (totalCellsBeforeTrailing % 7)) % 7
        val totalCells = totalCellsBeforeTrailing + trailingEmptyDays

        val cells = ArrayList<MonthCalendarCell>(totalCells)
        repeat(leadingEmptyDays) {
            cells.add(MonthCalendarCell(date = null, isCurrentMonth = false))
        }
        for (date in daysInMonth) {
            cells.add(MonthCalendarCell(date = date, isCurrentMonth = true))
        }
        repeat(trailingEmptyDays) {
            cells.add(MonthCalendarCell(date = null, isCurrentMonth = false))
        }

        val weeks = cells.chunked(7)
        return MonthCalendarGrid(
            yearMonth = yearMonth,
            firstDayOfWeek = firstDayOfWeek,
            weekdayHeaders = weekdayHeaders,
            weeks = weeks,
            daysInMonth = daysInMonth
        )
    }
}
