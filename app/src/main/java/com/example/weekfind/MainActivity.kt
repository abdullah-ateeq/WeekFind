package com.example.weekfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weekfind.navigation.WeekFindApp
import com.example.weekfind.ui.WeekFindViewModel
import com.example.weekfind.ui.theme.WeekFindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WeekFindViewModel = viewModel(
                factory = WeekFindViewModel.Factory(applicationContext)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            WeekFindTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                WeekFindApp(viewModel = viewModel)
            }
        }
    }
}
