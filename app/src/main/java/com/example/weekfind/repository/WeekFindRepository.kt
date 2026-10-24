package com.example.weekfind.repository

import android.content.SharedPreferences
import com.example.weekfind.data.PreferencesStorage
import com.example.weekfind.data.SharedPreferencesStorage
import com.example.weekfind.history.HistoryManager
import com.example.weekfind.model.AppThemeMode
import com.example.weekfind.model.DateFormatOption
import com.example.weekfind.model.WeekFindHistory
import com.example.weekfind.model.WeekFindSettings
import com.example.weekfind.model.WeekNumberingMode
import java.time.Clock
import java.time.LocalDate

class WeekFindRepository(
    private val storage: PreferencesStorage,
    private val historyManager: HistoryManager = HistoryManager(),
    private val clock: Clock = Clock.systemDefaultZone()
) {
    constructor(
        sharedPreferences: SharedPreferences,
        historyManager: HistoryManager = HistoryManager(),
        clock: Clock = Clock.systemDefaultZone()
    ) : this(SharedPreferencesStorage(sharedPreferences), historyManager, clock)

    companion object {
        const val PREFS_NAME = "weekfind_preferences"

        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_DYNAMIC_COLOR = "dynamic_color"
        const val KEY_WEEK_NUMBERING_MODE = "week_numbering_mode"
        const val KEY_SHOW_WEEK_NUMBER = "show_week_number"
        const val KEY_SHOW_ISO_WEEK = "show_iso_week"
        const val KEY_SHOW_DAY_OF_YEAR = "show_day_of_year"
        const val KEY_SHOW_YEAR_PROGRESS = "show_year_progress"
        const val KEY_SHOW_LEAP_YEAR = "show_leap_year"
        const val KEY_DATE_FORMAT = "date_format"
        const val KEY_SHOW_MONTH_CALENDAR = "show_month_calendar"
        const val KEY_SHOW_WEEK_VIEW = "show_week_view"
        const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        const val KEY_ANIMATIONS = "animations"
        const val KEY_CONFIRM_DELETE = "confirm_delete"
        const val KEY_RESTORE_LAST_DATE = "restore_last_date"
        const val KEY_LAST_SELECTED_DATE = "last_selected_date"
        const val KEY_HISTORY = "saved_history"
    }

    fun loadSettings(): WeekFindSettings {
        val defaults = WeekFindSettings()
        return WeekFindSettings(
            themeMode = AppThemeMode.fromKey(
                storage.getString(KEY_THEME_MODE, defaults.themeMode.storageKey)
            ),
            dynamicColor = storage.getBoolean(KEY_DYNAMIC_COLOR, defaults.dynamicColor),
            weekNumberingMode = WeekNumberingMode.fromKey(
                storage.getString(KEY_WEEK_NUMBERING_MODE, defaults.weekNumberingMode.storageKey)
            ),
            showWeekNumber = storage.getBoolean(KEY_SHOW_WEEK_NUMBER, defaults.showWeekNumber),
            showIsoWeek = storage.getBoolean(KEY_SHOW_ISO_WEEK, defaults.showIsoWeek),
            showDayOfYear = storage.getBoolean(KEY_SHOW_DAY_OF_YEAR, defaults.showDayOfYear),
            showYearProgress = storage.getBoolean(KEY_SHOW_YEAR_PROGRESS, defaults.showYearProgress),
            showLeapYear = storage.getBoolean(KEY_SHOW_LEAP_YEAR, defaults.showLeapYear),
            dateFormat = DateFormatOption.fromKey(
                storage.getString(KEY_DATE_FORMAT, defaults.dateFormat.storageKey)
            ),
            showMonthCalendar = storage.getBoolean(KEY_SHOW_MONTH_CALENDAR, defaults.showMonthCalendar),
            showWeekView = storage.getBoolean(KEY_SHOW_WEEK_VIEW, defaults.showWeekView),
            hapticFeedback = storage.getBoolean(KEY_HAPTIC_FEEDBACK, defaults.hapticFeedback),
            animations = storage.getBoolean(KEY_ANIMATIONS, defaults.animations),
            confirmDelete = storage.getBoolean(KEY_CONFIRM_DELETE, defaults.confirmDelete),
            restoreLastDate = storage.getBoolean(KEY_RESTORE_LAST_DATE, defaults.restoreLastDate)
        )
    }

    fun saveSettings(settings: WeekFindSettings) {
        storage.putString(KEY_THEME_MODE, settings.themeMode.storageKey)
        storage.putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
        storage.putString(KEY_WEEK_NUMBERING_MODE, settings.weekNumberingMode.storageKey)
        storage.putBoolean(KEY_SHOW_WEEK_NUMBER, settings.showWeekNumber)
        storage.putBoolean(KEY_SHOW_ISO_WEEK, settings.showIsoWeek)
        storage.putBoolean(KEY_SHOW_DAY_OF_YEAR, settings.showDayOfYear)
        storage.putBoolean(KEY_SHOW_YEAR_PROGRESS, settings.showYearProgress)
        storage.putBoolean(KEY_SHOW_LEAP_YEAR, settings.showLeapYear)
        storage.putString(KEY_DATE_FORMAT, settings.dateFormat.storageKey)
        storage.putBoolean(KEY_SHOW_MONTH_CALENDAR, settings.showMonthCalendar)
        storage.putBoolean(KEY_SHOW_WEEK_VIEW, settings.showWeekView)
        storage.putBoolean(KEY_HAPTIC_FEEDBACK, settings.hapticFeedback)
        storage.putBoolean(KEY_ANIMATIONS, settings.animations)
        storage.putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
        storage.putBoolean(KEY_RESTORE_LAST_DATE, settings.restoreLastDate)
    }

    /**
     * Resets all settings to defaults without deleting history or the saved last selected date.
     */
    fun resetSettings(): WeekFindSettings {
        val defaults = WeekFindSettings()
        saveSettings(defaults)
        return defaults
    }

    fun saveLastSelectedDate(date: LocalDate) {
        storage.putString(KEY_LAST_SELECTED_DATE, date.toString())
    }

    fun loadLastSelectedDate(): LocalDate? {
        val raw = storage.getString(KEY_LAST_SELECTED_DATE, null) ?: return null
        return runCatching { LocalDate.parse(raw) }.getOrNull()
    }

    /**
     * Resolves the initial date on startup:
     * - If Restore Last Date is On and a saved date exists, restores it.
     * - Otherwise starts with LocalDate.now(clock).
     */
    fun resolveInitialDate(): LocalDate {
        val settings = loadSettings()
        if (settings.restoreLastDate) {
            val saved = loadLastSelectedDate()
            if (saved != null) return saved
        }
        return LocalDate.now(clock)
    }

    fun loadHistory(): List<WeekFindHistory> {
        val raw = storage.getString(KEY_HISTORY, null)
        return historyManager.deserialize(raw)
    }

    fun saveHistoryEntry(
        date: LocalDate,
        savedAt: Long = clock.millis()
    ): List<WeekFindHistory> {
        val current = loadHistory()
        val updated = historyManager.addEntry(currentHistory = current, date = date, savedAt = savedAt)
        storage.putString(KEY_HISTORY, historyManager.serialize(updated))
        return updated
    }

    fun deleteHistoryEntry(id: String): List<WeekFindHistory> {
        val current = loadHistory()
        val updated = historyManager.removeEntry(current, id)
        storage.putString(KEY_HISTORY, historyManager.serialize(updated))
        return updated
    }

    fun clearHistory(): List<WeekFindHistory> {
        storage.remove(KEY_HISTORY)
        return emptyList()
    }

    fun restoreHistoryDate(entry: WeekFindHistory): LocalDate {
        return historyManager.restoreDate(entry)
    }
}
