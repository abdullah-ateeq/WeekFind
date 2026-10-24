package com.example.weekfind.model

enum class AppThemeMode(val storageKey: String, val displayName: String) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    companion object {
        fun fromKey(key: String?): AppThemeMode =
            entries.firstOrNull { it.storageKey == key } ?: SYSTEM
    }
}

enum class WeekNumberingMode(val storageKey: String, val displayName: String) {
    LOCALE("locale", "Locale"),
    ISO("iso", "ISO");

    companion object {
        fun fromKey(key: String?): WeekNumberingMode =
            entries.firstOrNull { it.storageKey == key } ?: LOCALE
    }
}

enum class DateFormatOption(
    val storageKey: String,
    val pattern: String,
    val example: String
) {
    DAY_MONTH_YEAR("d_mmmm_yyyy", "d MMMM yyyy", "24 October 2026"),
    SHORT_MONTH_DAY_YEAR("mmm_d_yyyy", "MMM d, yyyy", "Oct 24, 2026"),
    SLASH_DMY("dd_mm_yyyy", "dd/MM/yyyy", "24/10/2026"),
    ISO_YMD("yyyy_mm_dd", "yyyy-MM-dd", "2026-10-24");

    companion object {
        fun fromKey(key: String?): DateFormatOption =
            entries.firstOrNull { it.storageKey == key } ?: DAY_MONTH_YEAR
    }
}

data class WeekFindSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val weekNumberingMode: WeekNumberingMode = WeekNumberingMode.LOCALE,
    val showWeekNumber: Boolean = true,
    val showIsoWeek: Boolean = true,
    val showDayOfYear: Boolean = true,
    val showYearProgress: Boolean = true,
    val showLeapYear: Boolean = true,
    val dateFormat: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
    val showMonthCalendar: Boolean = true,
    val showWeekView: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val confirmDelete: Boolean = true,
    val restoreLastDate: Boolean = true
)
