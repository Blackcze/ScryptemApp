package com.example.scryptem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scryptem.ui.screen.CoinDetailScreen
import com.example.scryptem.ui.screen.CoinListScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "coin_list"
    ) {
        composable("coin_list") {
            CoinListScreen(navController)
        }
        composable("coin_detail/{coinId}") {
            CoinDetailScreen()
        }
    }
}
