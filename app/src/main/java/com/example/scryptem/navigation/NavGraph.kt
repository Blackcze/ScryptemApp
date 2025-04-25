package com.example.scryptem.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scryptem.presentation.settings.SettingsViewModel
import com.example.scryptem.ui.screen.CoinDetailScreen
import com.example.scryptem.ui.screen.CoinListScreen
import com.example.scryptem.ui.screen.FavoriteCoinsScreen
import com.example.scryptem.ui.screen.SettingsScreen
import androidx.compose.runtime.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val defaultScreenState = settingsViewModel.defaultScreen.collectAsState()
    val startDestination = remember(defaultScreenState.value) {
        when (defaultScreenState.value) {
            "favorites" -> "favorite_coins"
            else -> "coin_list"
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("coin_list") {
            CoinListScreen(navController)
        }
        composable("coin_detail/{coinId}") {
            CoinDetailScreen()
        }
        composable("favorite_coins") {
            FavoriteCoinsScreen(navController)
        }
        composable("settings") {
            SettingsScreen()
        }

    }
}