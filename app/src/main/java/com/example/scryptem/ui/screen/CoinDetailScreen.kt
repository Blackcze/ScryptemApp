package com.example.scryptem.ui.screen

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.text.HtmlCompat
import com.example.scryptem.presentation.coin_detail.CoinDetailViewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage

@Composable
fun CoinDetailScreen(viewModel: CoinDetailViewModel = hiltViewModel()) {
    val coinDetail by viewModel.coinDetail.collectAsState()
    val ohlcData by viewModel.ohlcData.collectAsState()
    var selectedDays by remember { mutableStateOf(30) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        coinDetail?.let { coin ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 32.dp)
            ) {
                // Info o kryptoměně
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

                Spacer(modifier = Modifier.height(16.dp))

                // Přepínač mezi 7 a 30 dny
                TimeframeSelector(selectedDays) {
                    selectedDays = it
                    viewModel.loadOhlcData(it)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Candlestick graf bezpečně
                if (ohlcData.isNotEmpty()) {
                    AndroidView(factory = { context ->
                        CandleStickChart(context).apply {
                            clear()
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                500
                            )

                            val entries = ohlcData.mapIndexed { index, entry ->
                                CandleEntry(
                                    index.toFloat(),
                                    entry.high,
                                    entry.low,
                                    entry.open,
                                    entry.close
                                )
                            }

                            val dataSet = CandleDataSet(entries, "Vývoj ceny").apply {
                                shadowColorSameAsCandle = true
                                decreasingColor = android.graphics.Color.RED
                                increasingColor = android.graphics.Color.GREEN
                                neutralColor = android.graphics.Color.GRAY
                                decreasingPaintStyle = android.graphics.Paint.Style.FILL
                                increasingPaintStyle = android.graphics.Paint.Style.FILL
                                setDrawValues(false)
                            }

                            data = CandleData(dataSet)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            axisRight.isEnabled = false
                            axisLeft.setDrawGridLines(false)
                            xAxis.setDrawGridLines(false)
                            legend.isEnabled = false
                            description.isEnabled = false
                            invalidate()
                        }
                    })
                } else {
                    // Graf ještě není načtený
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = HtmlCompat.fromHtml(
                        coin.description.en,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } ?: run {
            // Loader při načítání detailu
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
@Composable
fun TimeframeSelector(selectedDays: Int, onSelectionChange: (Int) -> Unit) {
    val options = listOf(7, 30)
    TabRow(selectedTabIndex = options.indexOf(selectedDays)) {
        options.forEach { days ->
            Tab(
                selected = selectedDays == days,
                onClick = { onSelectionChange(days) },
                text = { Text("$days dní") }
            )
        }
    }
}
