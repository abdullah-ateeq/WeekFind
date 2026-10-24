package com.example.weekfind.formatter

import com.example.weekfind.domain.WeekFindResult
import com.example.weekfind.model.DateFormatOption
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Locale

class DateFormatter {

    companion object {
        const val INVALID_DATE_ERROR = "Invalid date.\nPlease enter a valid calendar date."
    }

    @JvmOverloads
    fun formatDate(
        date: LocalDate,
        formatOption: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
        locale: Locale = Locale.getDefault()
    ): String {
        val formatter = DateTimeFormatter.ofPattern(formatOption.pattern, locale)
        return date.format(formatter)
    }

    @JvmOverloads
    fun formatWeekdayFull(
        dayOfWeek: DayOfWeek,
        locale: Locale = Locale.getDefault()
    ): String {
        return dayOfWeek.getDisplayName(TextStyle.FULL, locale)
    }

    @JvmOverloads
    fun formatWeekdayUppercase(
        dayOfWeek: DayOfWeek,
        locale: Locale = Locale.getDefault()
    ): String {
        return formatWeekdayFull(dayOfWeek, locale).uppercase(locale)
    }

    @JvmOverloads
    fun formatWeekdayAbbreviation(
        dayOfWeek: DayOfWeek,
        locale: Locale = Locale.getDefault()
    ): String {
        val raw = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            .trimEnd('.')
        return raw.uppercase(locale)
    }

    @JvmOverloads
    fun formatWeekdayShortTitle(
        dayOfWeek: DayOfWeek,
        locale: Locale = Locale.getDefault()
    ): String {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, locale).trimEnd('.')
    }

    @JvmOverloads
    fun formatMonthFull(
        date: LocalDate,
        locale: Locale = Locale.getDefault()
    ): String {
        return date.month.getDisplayName(TextStyle.FULL, locale)
    }

    @JvmOverloads
    fun formatYearMonth(
        yearMonth: YearMonth,
        locale: Locale = Locale.getDefault()
    ): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        return yearMonth.format(formatter)
    }

    fun formatOrdinal(number: Int): String {
        val mod100 = number % 100
        val suffix = if (mod100 in 11..13) {
            "th"
        } else {
            when (number % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
        return "$number$suffix"
    }

    @JvmOverloads
    fun formatCopyWeekday(
        result: WeekFindResult,
        locale: Locale = Locale.getDefault()
    ): String {
        return formatWeekdayFull(result.weekday, locale)
    }

    @JvmOverloads
    fun formatCopyResult(
        result: WeekFindResult,
        formatOption: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
        locale: Locale = Locale.getDefault()
    ): String {
        val formattedDate = formatDate(result.date, formatOption, locale)
        val weekdayName = formatWeekdayFull(result.weekday, locale)
        val leapYearLabel = if (result.isLeapYear) "Yes" else "No"

        return buildString {
            appendLine("WeekFind")
            appendLine()
            appendLine("Date: $formattedDate")
            appendLine("Weekday: $weekdayName")
            appendLine()
            appendLine("Day of year: ${result.dayOfYear}")
            appendLine("Week of year: ${result.weekOfYear}")
            appendLine("ISO week: ${result.isoWeek}")
            if (result.isoWeekYear != result.date.year) {
                appendLine("ISO week-year: ${result.isoWeekYear}")
            }
            appendLine()
            appendLine("Days elapsed: ${result.daysElapsed}")
            appendLine("Days remaining: ${result.daysRemaining}")
            appendLine()
            append("Leap year: $leapYearLabel")
        }
    }

    @JvmOverloads
    fun formatSavedTimestamp(
        savedAtMillis: Long,
        today: LocalDate = LocalDate.now(),
        zoneId: ZoneId = ZoneId.systemDefault(),
        locale: Locale = Locale.getDefault()
    ): String {
        val zonedDateTime = Instant.ofEpochMilli(savedAtMillis).atZone(zoneId)
        val savedDate = zonedDateTime.toLocalDate()
        val timeString = zonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm a", locale))

        return when (savedDate) {
            today -> "Saved today • $timeString"
            today.minusDays(1) -> "Saved yesterday • $timeString"
            else -> {
                val datePart = savedDate.format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))
                "Saved $datePart • $timeString"
            }
        }
    }

    /**
     * Parses an ISO-8601 date string (e.g., "2026-10-24") using [LocalDate.parse].
     * Rejects invalid dates such as "2026-02-30" with a friendly error message.
     */
    fun parseIsoDate(input: String): Result<LocalDate> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException(INVALID_DATE_ERROR))
        }
        return try {
            val parsed = LocalDate.parse(trimmed)
            Result.success(parsed)
        } catch (e: DateTimeParseException) {
            Result.failure(IllegalArgumentException(INVALID_DATE_ERROR, e))
        }
    }
}
