package com.example.scryptem.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.scryptem.presentation.coin_detail.CoinDetailViewModel
import androidx.core.text.HtmlCompat

@Composable
fun CoinDetailScreen(viewModel: CoinDetailViewModel = hiltViewModel()) {
    val coinDetail by viewModel.coinDetail.collectAsState()

    coinDetail?.let { coin ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 32.dp)
        ) {
            AsyncImage(
                model = coin.image.large,
                contentDescription = coin.name,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = coin.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Symbol: ${coin.symbol.uppercase()}")
            Text(text = "Cena: $${coin.market_data.currentPrice["usd"]}")
            Text(text = "Změna za 24h: ${coin.market_data.priceChange24h} %")
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = HtmlCompat.fromHtml(coin.description.en, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
