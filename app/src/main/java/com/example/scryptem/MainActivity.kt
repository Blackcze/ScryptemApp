package com.example.scryptem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.scryptem.navigation.AppNavGraph
import com.example.scryptem.presentation.settings.SettingsViewModel
import com.example.scryptem.ui.theme.ScryptemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            ScryptemTheme(settingsViewModel) {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}

