package com.example.scryptem.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.scryptem.data.local.entity.FavoriteCoinEntity
import com.example.scryptem.presentation.favorite.FavoriteCoinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCoinsScreen(
    navController: NavController,
    viewModel: FavoriteCoinViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oblíbené kryptoměny") }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (favorites.isEmpty()) {
                Text(
                    text = "Zatím nemáš žádné oblíbené kryptoměny.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(favorites) { coin ->
                        FavoriteCoinItem(
                            coin = coin,
                            onClick = {
                                navController.navigate("coin_detail/${coin.id}")
                            },
                            onRemove = {
                                viewModel.toggleFavorite(coin)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteCoinItem(
    coin: FavoriteCoinEntity,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
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
                Text(text = "Symbol: ${coin.symbol.uppercase()}")
                Text(text = "Cena: $${coin.currentPrice}", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Odebrání ze seznamu
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Odebrat z oblíbených"
            )
        }
    }
}
