package com.example.weekfind

import com.example.weekfind.calculator.WeekFindCalculator
import com.example.weekfind.calendar.DateNavigator
import com.example.weekfind.calendar.MonthCalendarGenerator
import com.example.weekfind.calendar.WeekGenerator
import com.example.weekfind.data.InMemoryPreferencesStorage
import com.example.weekfind.formatter.DateFormatter
import com.example.weekfind.history.HistoryManager
import com.example.weekfind.model.AppThemeMode
import com.example.weekfind.model.DateFormatOption
import com.example.weekfind.model.WeekNumberingMode
import com.example.weekfind.repository.WeekFindRepository
import com.example.weekfind.ui.WeekFindViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.temporal.WeekFields
import java.util.Locale

class WeekFindUnitTest {

    private val calculator = WeekFindCalculator()
    private val weekGenerator = WeekGenerator()
    private val monthCalendarGenerator = MonthCalendarGenerator()
    private val fixedClock = Clock.fixed(
        Instant.parse("2026-10-24T09:30:00Z"),
        ZoneOffset.UTC
    )
    private val dateNavigator = DateNavigator(clock = fixedClock)
    private val dateFormatter = DateFormatter()

    @Test
    fun criticalTest_basic_24October2026IsSaturday() {
        val date = LocalDate.of(2026, 10, 24)
        val result = calculator.calculate(date, Locale.UK, WeekNumberingMode.ISO)

        assertEquals(DayOfWeek.SATURDAY, result.weekday)
        assertEquals(297, result.dayOfYear)
        assertEquals(296, result.daysElapsed)
        // 365 days in 2026 - 297 days elapsed = 68 days remaining
        assertEquals(68, result.daysRemaining)
        assertEquals(365, result.yearLength)
        assertFalse(result.isLeapYear)
        assertEquals(43, result.isoWeek)
        assertEquals(2026, result.isoWeekYear)
    }

    @Test
    fun criticalTest_allWeekdaysAreAccurate() {
        val knownDates = mapOf(
            LocalDate.of(2026, 10, 19) to DayOfWeek.MONDAY,
            LocalDate.of(2026, 10, 20) to DayOfWeek.TUESDAY,
            LocalDate.of(2026, 10, 21) to DayOfWeek.WEDNESDAY,
            LocalDate.of(2026, 10, 22) to DayOfWeek.THURSDAY,
            LocalDate.of(2026, 10, 23) to DayOfWeek.FRIDAY,
            LocalDate.of(2026, 10, 24) to DayOfWeek.SATURDAY,
            LocalDate.of(2026, 10, 25) to DayOfWeek.SUNDAY
        )

        for ((date, expectedDayOfWeek) in knownDates) {
            val result = calculator.calculate(date)
            assertEquals("Failed for $date", expectedDayOfWeek, result.weekday)
        }
    }

    @Test
    fun criticalTest_leapYear_29February2024() {
        val date = LocalDate.of(2024, 2, 29)
        val result = calculator.calculate(date)

        assertEquals(DayOfWeek.THURSDAY, result.weekday)
        assertEquals(60, result.dayOfYear)
        assertEquals(366, result.yearLength)
        assertTrue(result.isLeapYear)
        assertEquals(59, result.daysElapsed)
        assertEquals(306, result.daysRemaining)
    }

    @Test
    fun criticalTest_nonLeapYear_28February2023() {
        val date = LocalDate.of(2023, 2, 28)
        val result = calculator.calculate(date)

        assertEquals(DayOfWeek.TUESDAY, result.weekday)
        assertEquals(59, result.dayOfYear)
        assertEquals(365, result.yearLength)
        assertFalse(result.isLeapYear)
        assertEquals(58, result.daysElapsed)
        assertEquals(306, result.daysRemaining)
    }

    @Test
    fun criticalTest_newYear_31Dec2026And1Jan2027() {
        val dec31 = LocalDate.of(2026, 12, 31)
        val jan1 = LocalDate.of(2027, 1, 1)

        val dec31Result = calculator.calculate(dec31, Locale.UK, WeekNumberingMode.LOCALE)
        assertEquals(DayOfWeek.THURSDAY, dec31Result.weekday)
        assertEquals(53, dec31Result.isoWeek)
        assertEquals(2026, dec31Result.isoWeekYear)

        val jan1Result = calculator.calculate(jan1, Locale.UK, WeekNumberingMode.LOCALE)
        assertEquals(DayOfWeek.FRIDAY, jan1Result.weekday)
        // Jan 1, 2027 is a Friday, so under ISO-8601 it belongs to ISO week 53 of 2026!
        assertEquals(53, jan1Result.isoWeek)
        assertEquals(2026, jan1Result.isoWeekYear)
    }

    @Test
    fun criticalTest_isoWeekBoundary_whereIsoWeekYearDiffersFromCalendarYear() {
        // 29 December 2025 (Monday) is in ISO Week 1 of 2026
        val dec29_2025 = LocalDate.of(2025, 12, 29)
        val resultDec29 = calculator.calculate(dec29_2025)
        assertEquals(DayOfWeek.MONDAY, resultDec29.weekday)
        assertEquals(1, resultDec29.isoWeek)
        assertEquals(2026, resultDec29.isoWeekYear)
        assertEquals(
            dec29_2025.get(WeekFields.ISO.weekOfWeekBasedYear()),
            resultDec29.isoWeek
        )
        assertEquals(
            dec29_2025.get(WeekFields.ISO.weekBasedYear()),
            resultDec29.isoWeekYear
        )

        // 31 December 2029 (Monday) is in ISO Week 1 of 2030
        val dec31_2029 = LocalDate.of(2029, 12, 31)
        val resultDec31 = calculator.calculate(dec31_2029)
        assertEquals(1, resultDec31.isoWeek)
        assertEquals(2030, resultDec31.isoWeekYear)
    }

    @Test
    fun criticalTest_dayOfYear_jan1AndDec31() {
        assertEquals(1, calculator.calculate(LocalDate.of(2026, 1, 1)).dayOfYear)
        assertEquals(365, calculator.calculate(LocalDate.of(2026, 12, 31)).dayOfYear)
        assertEquals(366, calculator.calculate(LocalDate.of(2024, 12, 31)).dayOfYear)
    }

    @Test
    fun criticalTest_daysRemaining() {
        // Normal year (365 days)
        assertEquals(364, calculator.calculate(LocalDate.of(2026, 1, 1)).daysRemaining)
        assertEquals(1, calculator.calculate(LocalDate.of(2026, 12, 30)).daysRemaining)
        assertEquals(0, calculator.calculate(LocalDate.of(2026, 12, 31)).daysRemaining)

        // Leap year (366 days)
        assertEquals(365, calculator.calculate(LocalDate.of(2024, 1, 1)).daysRemaining)
        assertEquals(1, calculator.calculate(LocalDate.of(2024, 12, 30)).daysRemaining)
        assertEquals(0, calculator.calculate(LocalDate.of(2024, 12, 31)).daysRemaining)
    }

    @Test
    fun criticalTest_daysElapsed() {
        assertEquals(0, calculator.calculate(LocalDate.of(2026, 1, 1)).daysElapsed)
        assertEquals(1, calculator.calculate(LocalDate.of(2026, 1, 2)).daysElapsed)
        assertEquals(364, calculator.calculate(LocalDate.of(2026, 12, 31)).daysElapsed)
        assertEquals(365, calculator.calculate(LocalDate.of(2024, 12, 31)).daysElapsed)
    }

    @Test
    fun criticalTest_yearProgress() {
        val jan1 = calculator.calculate(LocalDate.of(2026, 1, 1))
        assertEquals(1f / 365f, jan1.yearProgress, 0.0001f)

        val dec31Normal = calculator.calculate(LocalDate.of(2026, 12, 31))
        assertEquals(1.0f, dec31Normal.yearProgress, 0.0001f)

        val dec31Leap = calculator.calculate(LocalDate.of(2024, 12, 31))
        assertEquals(1.0f, dec31Leap.yearProgress, 0.0001f)
    }

    @Test
    fun criticalTest_monthCalendar_februaryLeapAndNonLeap() {
        val feb2024 = monthCalendarGenerator.generate(YearMonth.of(2024, 2), DayOfWeek.MONDAY)
        assertEquals(29, feb2024.daysInMonth.size)
        assertEquals(LocalDate.of(2024, 2, 29), feb2024.daysInMonth.last())

        val feb2023 = monthCalendarGenerator.generate(YearMonth.of(2023, 2), DayOfWeek.MONDAY)
        assertEquals(28, feb2023.daysInMonth.size)
        assertEquals(LocalDate.of(2023, 2, 28), feb2023.daysInMonth.last())

        val sundayGrid = monthCalendarGenerator.generate(YearMonth.of(2026, 10), DayOfWeek.SUNDAY)
        assertEquals(DayOfWeek.SUNDAY, sundayGrid.weekdayHeaders.first())
        assertEquals(DayOfWeek.SATURDAY, sundayGrid.weekdayHeaders.last())
    }

    @Test
    fun criticalTest_weekGeneration_monthAndYearAndLeapBoundaries() {
        // 1 November 2026 is a Sunday. With Monday start, week spans Oct 26 .. Nov 1
        val nov1 = LocalDate.of(2026, 11, 1)
        val weekMondayStart = weekGenerator.generateWeek(nov1, DayOfWeek.MONDAY)
        assertEquals(7, weekMondayStart.size)
        assertEquals(LocalDate.of(2026, 10, 26), weekMondayStart.first())
        assertEquals(LocalDate.of(2026, 11, 1), weekMondayStart.last())

        // With Sunday start, week spans Nov 1 .. Nov 7
        val weekSundayStart = weekGenerator.generateWeek(nov1, DayOfWeek.SUNDAY)
        assertEquals(7, weekSundayStart.size)
        assertEquals(LocalDate.of(2026, 11, 1), weekSundayStart.first())
        assertEquals(LocalDate.of(2026, 11, 7), weekSundayStart.last())

        // Year boundary: 31 Dec 2026 (Thursday) with Monday start spans Dec 28, 2026 .. Jan 3, 2027
        val dec31 = LocalDate.of(2026, 12, 31)
        val newYearWeek = weekGenerator.generateWeek(dec31, DayOfWeek.MONDAY)
        assertEquals(LocalDate.of(2026, 12, 28), newYearWeek.first())
        assertEquals(LocalDate.of(2027, 1, 3), newYearWeek.last())

        // Leap boundary: 1 March 2024 (Friday) with Monday start includes 29 Feb 2024
        val mar1Leap = LocalDate.of(2024, 3, 1)
        val leapWeek = weekGenerator.generateWeek(mar1Leap, DayOfWeek.MONDAY)
        assertTrue(leapWeek.contains(LocalDate.of(2024, 2, 29)))
    }

    @Test
    fun criticalTest_monthNavigation_clampingEdgeCases() {
        // 31 January 2026 -> 28 February 2026
        assertEquals(
            LocalDate.of(2026, 2, 28),
            dateNavigator.nextMonth(LocalDate.of(2026, 1, 31))
        )

        // 31 January 2024 -> 29 February 2024 (leap year)
        assertEquals(
            LocalDate.of(2024, 2, 29),
            dateNavigator.nextMonth(LocalDate.of(2024, 1, 31))
        )

        // 30 March -> 30 April
        assertEquals(
            LocalDate.of(2026, 4, 30),
            dateNavigator.nextMonth(LocalDate.of(2026, 3, 30))
        )

        // 31 March -> 30 April
        assertEquals(
            LocalDate.of(2026, 4, 30),
            dateNavigator.nextMonth(LocalDate.of(2026, 3, 31))
        )

        // 31 May -> 30 June
        assertEquals(
            LocalDate.of(2026, 6, 30),
            dateNavigator.nextMonth(LocalDate.of(2026, 5, 31))
        )

        // 31 August -> 30 September
        assertEquals(
            LocalDate.of(2026, 9, 30),
            dateNavigator.nextMonth(LocalDate.of(2026, 8, 31))
        )

        // 31 October -> 30 November
        assertEquals(
            LocalDate.of(2026, 11, 30),
            dateNavigator.nextMonth(LocalDate.of(2026, 10, 31))
        )
    }

    @Test
    fun criticalTest_persistence_restoresSelectedDateAcrossRestart() {
        val storage = InMemoryPreferencesStorage()
        val repo1 = WeekFindRepository(storage = storage, clock = fixedClock)
        val viewModel1 = WeekFindViewModel(repository = repo1, dateNavigator = dateNavigator)

        val targetDate = LocalDate.of(2026, 10, 24)
        viewModel1.selectDate(targetDate)
        assertEquals(targetDate, viewModel1.uiState.value.selectedDate)

        // Simulate app restart with the same underlying storage
        val repo2 = WeekFindRepository(storage = storage, clock = fixedClock)
        val viewModel2 = WeekFindViewModel(repository = repo2, dateNavigator = dateNavigator)
        assertEquals(targetDate, viewModel2.uiState.value.selectedDate)
        assertEquals(DayOfWeek.SATURDAY, viewModel2.uiState.value.weekday)
    }

    @Test
    fun criticalTest_history_limit10AndOldestRemovedOn11th() {
        val historyManager = HistoryManager()
        var list = emptyList<com.example.weekfind.model.WeekFindHistory>()

        for (day in 1..10) {
            list = historyManager.addEntry(
                currentHistory = list,
                date = LocalDate.of(2026, 10, day),
                savedAt = day * 1000L,
                id = "id_$day"
            )
        }
        assertEquals(10, list.size)
        assertEquals("2026-10-10", list.first().date)
        assertEquals("2026-10-01", list.last().date)

        // Save 11th date -> oldest ("2026-10-01") must be removed
        list = historyManager.addEntry(
            currentHistory = list,
            date = LocalDate.of(2026, 10, 11),
            savedAt = 11_000L,
            id = "id_11"
        )
        assertEquals(10, list.size)
        assertEquals("2026-10-11", list.first().date)
        assertEquals("2026-10-02", list.last().date)
        assertFalse(list.any { it.date == "2026-10-01" })
    }

    @Test
    fun criticalTest_history_preventConsecutiveDuplicates() {
        val historyManager = HistoryManager()
        val date = LocalDate.of(2026, 10, 24)

        var list = historyManager.addEntry(emptyList(), date, savedAt = 1000L, id = "1")
        list = historyManager.addEntry(list, date, savedAt = 2000L, id = "2")

        assertEquals(1, list.size)
        assertEquals("2026-10-24", list.first().date)
        assertEquals(2000L, list.first().savedAt)
    }

    @Test
    fun criticalTest_historyRestore_recalculatesThursdayFor29Feb2024() {
        val storage = InMemoryPreferencesStorage()
        val repo = WeekFindRepository(storage = storage, clock = fixedClock)
        val viewModel = WeekFindViewModel(repository = repo, dateNavigator = dateNavigator)

        val leapDate = LocalDate.of(2024, 2, 29)
        viewModel.selectDate(leapDate)
        viewModel.saveCurrentDate(savedAtMillis = 1000L)

        // Move to another date
        viewModel.selectDate(LocalDate.of(2026, 10, 24))
        assertEquals(DayOfWeek.SATURDAY, viewModel.uiState.value.weekday)

        // Restore saved entry from History
        val savedEntry = viewModel.uiState.value.history.first()
        assertEquals("2024-02-29", savedEntry.date)
        viewModel.restoreHistoryEntry(savedEntry)

        assertEquals(leapDate, viewModel.uiState.value.selectedDate)
        assertEquals(DayOfWeek.THURSDAY, viewModel.uiState.value.weekday)
        assertEquals(60, viewModel.uiState.value.dayOfYear)
        assertEquals(366, viewModel.uiState.value.yearLength)
        assertTrue(viewModel.uiState.value.isLeapYear)
    }

    @Test
    fun criticalTest_invalidManualDate_producesFriendlyErrorWithoutCrashing() {
        val storage = InMemoryPreferencesStorage()
        val repo = WeekFindRepository(storage = storage, clock = fixedClock)
        val viewModel = WeekFindViewModel(repository = repo, dateNavigator = dateNavigator)

        val success = viewModel.enterDateIso("2026-02-30")
        assertFalse(success)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(
            "Invalid date.\nPlease enter a valid calendar date.",
            viewModel.uiState.value.errorMessage
        )

        val validSuccess = viewModel.enterDateIso("2026-10-24")
        assertTrue(validSuccess)
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(LocalDate.of(2026, 10, 24), viewModel.uiState.value.selectedDate)
    }

    @Test
    fun criticalTest_localeFormatting_englishAndFrenchAndFormats() {
        val date = LocalDate.of(2026, 10, 24)
        assertEquals("Saturday", dateFormatter.formatWeekdayFull(date.dayOfWeek, Locale.ENGLISH))
        assertEquals("samedi", dateFormatter.formatWeekdayFull(date.dayOfWeek, Locale.FRENCH).lowercase(Locale.FRENCH))

        assertEquals(
            "24 October 2026",
            dateFormatter.formatDate(date, DateFormatOption.DAY_MONTH_YEAR, Locale.ENGLISH)
        )
        assertEquals(
            "Oct 24, 2026",
            dateFormatter.formatDate(date, DateFormatOption.SHORT_MONTH_DAY_YEAR, Locale.US)
        )
        assertEquals(
            "24/10/2026",
            dateFormatter.formatDate(date, DateFormatOption.SLASH_DMY, Locale.UK)
        )
        assertEquals(
            "2026-10-24",
            dateFormatter.formatDate(date, DateFormatOption.ISO_YMD, Locale.ENGLISH)
        )
    }

    @Test
    fun criticalTest_resetSettings_preservesHistoryAndSavedDate() {
        val storage = InMemoryPreferencesStorage()
        val repo = WeekFindRepository(storage = storage, clock = fixedClock)
        val viewModel = WeekFindViewModel(repository = repo, dateNavigator = dateNavigator)

        val customDate = LocalDate.of(2024, 2, 29)
        viewModel.selectDate(customDate)
        viewModel.saveCurrentDate(12345L)
        viewModel.updateThemeMode(AppThemeMode.DARK)
        viewModel.updateDateFormat(DateFormatOption.ISO_YMD)
        viewModel.updateShowWeekView(false)

        viewModel.resetSettings()

        val state = viewModel.uiState.value
        assertEquals(AppThemeMode.SYSTEM, state.settings.themeMode)
        assertEquals(DateFormatOption.DAY_MONTH_YEAR, state.settings.dateFormat)
        assertTrue(state.settings.showWeekView)
        assertEquals(customDate, state.selectedDate)
        assertEquals(1, state.history.size)
        assertEquals("2024-02-29", state.history.first().date)
    }
}
