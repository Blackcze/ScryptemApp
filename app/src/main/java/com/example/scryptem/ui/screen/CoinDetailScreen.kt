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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.scryptem.presentation.coin_detail.CoinDetailViewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import androidx.compose.ui.viewinterop.AndroidView
import java.text.NumberFormat

@Composable
fun CoinDetailScreen(viewModel: CoinDetailViewModel = hiltViewModel()) {
    val coinDetail by viewModel.coinDetail.collectAsState()
    val ohlcData by viewModel.ohlcData.collectAsState()
    val address by viewModel.addressInput.collectAsState()
    val addressInfo by viewModel.addressInfo.collectAsState()
    val ownedAmount by viewModel.ownedAmount.collectAsState()

    var selectedDays by remember { mutableStateOf(30) }
    val focusManager = LocalFocusManager.current

    coinDetail?.let { coin ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            TimeframeSelector(selectedDays) {
                selectedDays = it
                viewModel.loadOhlcData(it)
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Moje množství", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = ownedAmount,
                onValueChange = {
                    viewModel.onAmountChanged(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Zadej množství (${coin.symbol.uppercase()})") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(8.dp))

            val usd = try {
                val amount = ownedAmount.replace(",", ".").toBigDecimal()
                val price = coin.market_data.currentPrice["usd"]?.toBigDecimal()
                price?.multiply(amount)?.setScale(2, java.math.RoundingMode.HALF_UP)
            } catch (e: Exception) {
                null
            }

            usd?.let {
                Text("Hodnota v USD: $it")
            }
            Spacer(modifier = Modifier.height(24.dp))

            val supported = listOf("bitcoin")
            if (coin.id in supported) {
                Text("Zůstatek peněženky", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = address,
                    onValueChange = {
                        viewModel.onAddressEntered(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Adresa") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.loadAddressInfo(
                            address = address,
                            network = coin.id,
                            coinPriceUsd = coin.market_data.currentPrice["usd"]
                        )
                        focusManager.clearFocus()
                    })
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.loadAddressInfo(
                            address = address,
                            network = coin.id,
                            coinPriceUsd = coin.market_data.currentPrice["usd"]
                        )
                    }
                ) {
                    Text("Zkontrolovat zůstatek")
                }

                Spacer(modifier = Modifier.height(16.dp))

                addressInfo?.let { info ->
                    val fmt = NumberFormat.getNumberInstance()

                    Text("Zůstatek: ${fmt.format(info.balance)} satoshi")
                    Text("Zůstatek v USD: ${info.usdValue ?: "-"}")
                    Text("Přijato celkem: ${fmt.format(info.received)} satoshi")
                    Text("Odesláno celkem: ${fmt.format(info.spent)} satoshi")
                    Text("Doporučený poplatek: ${info.feeSuggestion}")
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
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
