package com.example.scryptem.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.scryptem.data.remote.Coin
import com.example.scryptem.presentation.favorite.FavoriteCoinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import com.example.scryptem.data.local.entity.FavoriteCoinEntity
import kotlinx.coroutines.flow.Flow



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListScreen(navController: NavController, viewModel: CoinViewModel = hiltViewModel(), favoriteViewModel: FavoriteCoinViewModel = hiltViewModel()) {
    val coins by viewModel.coins.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scryptem - Kryptoměny") },
                actions = {
                    IconButton(onClick = { navController.navigate("favorite_coins") }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Oblíbené"
                        )
                    }
                }
            )

        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(coins) { coin ->
                        CoinItem(
                            coin = coin,
                            isFavoriteFlow = favoriteViewModel.isFavorite(coin.id),
                            onToggleFavorite = {
                                favoriteViewModel.toggleFavorite(
                                    FavoriteCoinEntity(
                                        id = coin.id,
                                        name = coin.name,
                                        symbol = coin.symbol,
                                        image = coin.image,
                                        currentPrice = coin.current_price
                                    )
                                )
                            },
                            onClick = {
                                navController.navigate("coin_detail/${coin.id}")
                            }
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun CoinItem(
    coin: Coin,
    isFavoriteFlow: Flow<Boolean>,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val isFavorite by isFavoriteFlow.collectAsState(initial = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            AsyncImage(
                model = coin.image,
                contentDescription = "${coin.name} icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = coin.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Cena: $${coin.current_price}", style = MaterialTheme.typography.bodySmall)
            }
        }

        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Odebrat z oblíbených" else "Přidat do oblíbených"
            )
        }
    }
}



