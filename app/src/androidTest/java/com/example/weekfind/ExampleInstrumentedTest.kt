package com.example.weekfind

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.weekfind.calendar.DateNavigator
import com.example.weekfind.navigation.WeekFindApp
import com.example.weekfind.navigation.WeekFindScreen
import com.example.weekfind.repository.WeekFindRepository
import com.example.weekfind.ui.WeekFindViewModel
import com.example.weekfind.ui.theme.WeekFindTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun useAppContext_andVerifyOfflineNoInternetOrLocationPermissions() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.weekfind", appContext.packageName)

        val internetPermission = appContext.checkSelfPermission(Manifest.permission.INTERNET)
        assertNotEquals(PackageManager.PERMISSION_GRANTED, internetPermission)

        val fineLocation = appContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        assertNotEquals(PackageManager.PERMISSION_GRANTED, fineLocation)

        val coarseLocation = appContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        assertNotEquals(PackageManager.PERMISSION_GRANTED, coarseLocation)
    }

    @Test
    fun homeScreen_displaysSelectedDateAndNavigatesDays() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("test_weekfind_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val fixedClock = Clock.fixed(
            Instant.parse("2026-10-24T09:30:00Z"),
            ZoneOffset.UTC
        )
        val repository = WeekFindRepository(sharedPreferences = prefs, clock = fixedClock)
        val viewModel = WeekFindViewModel(
            repository = repository,
            dateNavigator = DateNavigator(clock = fixedClock)
        )
        viewModel.selectDate(LocalDate.of(2026, 10, 24))

        composeTestRule.setContent {
            WeekFindTheme {
                WeekFindApp(
                    viewModel = viewModel,
                    startDestination = WeekFindScreen.HOME.route
                )
            }
        }

        composeTestRule.onNodeWithText("WeekFind").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Selected date, 24 October 2026.").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Saturday.").assertIsDisplayed()

        // Navigate to next day (25 October 2026 -> Sunday)
        composeTestRule.onNodeWithContentDescription("Next day.").performClick()
        composeTestRule.onNodeWithContentDescription("Selected date, 25 October 2026.").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Sunday.").assertIsDisplayed()
    }
}
