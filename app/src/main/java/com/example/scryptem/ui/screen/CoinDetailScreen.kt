package com.example.scryptem.ui.screen

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.scryptem.presentation.coin_detail.CoinDetailViewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun CoinDetailScreen(viewModel: CoinDetailViewModel = hiltViewModel()) {
    val coinDetail by viewModel.coinDetail.collectAsState()
    val ohlcData by viewModel.ohlcData.collectAsState()
    val addressInput by viewModel.addressInput.collectAsState()
    val addressInfo by viewModel.addressInfo.collectAsState()
    val ownedAmount by viewModel.ownedAmount.collectAsState()
    var selectedDays by remember { mutableStateOf(30) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        color = MaterialTheme.colorScheme.background
    ) {
        coinDetail?.let { coin ->
            Column(modifier = Modifier.fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)) {

                //Info o kryptoměně
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = coin.name, style = MaterialTheme.typography.titleLarge)
                        Text(text = "Symbol: ${coin.symbol.uppercase()}")
                        Text(text = "Cena: $${coin.market_data.currentPrice["usd"]}")
                        Text(text = "Změna za 24h: ${coin.market_data.priceChange24h} %")
                    }
                    AsyncImage(
                        model = coin.image.large,
                        contentDescription = coin.name,
                        modifier = Modifier
                            .size(100.dp)
                    )
                }



                Spacer(modifier = Modifier.height(16.dp))

                //Přepínač mezi 7/30 dní
                TimeframeSelector(selectedDays) {
                    selectedDays = it
                    viewModel.loadOhlcData(it)
                }

                Spacer(modifier = Modifier.height(12.dp))

                //Graf
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
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                setDrawGridLines(false)
                                textColor = android.graphics.Color.parseColor("#FF6800")
                            }
                            axisLeft.apply {
                                setDrawGridLines(false)
                                textColor = android.graphics.Color.parseColor("#FF6800")
                            }
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

                //Zadání množství
                Text("Vlastněné množství", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = ownedAmount,
                    onValueChange = { viewModel.onAmountChanged(it) },
                    label = { Text("Množství (${coin.symbol.uppercase()})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )

                val currentPrice = coin.market_data.currentPrice["usd"]
                val calculated = try {
                    BigDecimal(ownedAmount.replace(",", "."))
                        .multiply(BigDecimal(currentPrice ?: 0.0))
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString()
                } catch (e: Exception) {
                    "-"
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Aktuální hodnota: $calculated USD")

                Spacer(modifier = Modifier.height(24.dp))

                //Vyhledávání zůstatku
                val supportedNetworks = listOf("bitcoin")
                if (coin.id in supportedNetworks) {
                    Text("Zůstatek peněženky", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { viewModel.onAddressEntered(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Adresa") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.loadAddressInfo(
                                    address = addressInput,
                                    network = coin.id,
                                    coinPriceUsd = coin.market_data.currentPrice["usd"]
                                )
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.loadAddressInfo(
                                address = addressInput,
                                network = coin.id,
                                coinPriceUsd = coin.market_data.currentPrice["usd"]
                            )
                        }
                    ) {
                        Text("Zkontrolovat zůstatek")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    addressInfo?.let { info ->
                        val formatter = NumberFormat.getNumberInstance()
                        Text("Zůstatek: ${formatter.format(info.balance)} satoshi")
                        Text("Zůstatek v USD: ${info.usdValue ?: "-"}")
                        Text("Přijato celkem: ${formatter.format(info.received)} satoshi")
                        Text("Odesláno celkem: ${formatter.format(info.spent)} satoshi")
                        Text("Doporučený poplatek: ${info.feeSuggestion ?: "-"}")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Popis kryptoměny
                Text(
                    text = HtmlCompat.fromHtml(
                        coin.description.en,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } ?: run {
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
