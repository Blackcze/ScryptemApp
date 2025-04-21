package com.example.scryptem.presentation.coin_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.remote.dto.CoinDetail
import com.example.scryptem.data.remote.dto.OhlcEntry
import com.example.scryptem.data.repository.BlockchairRepository
import com.example.scryptem.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val blockchairRepository: BlockchairRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = savedStateHandle["coinId"] ?: ""

    private val _coinDetail = MutableStateFlow<CoinDetail?>(null)
    val coinDetail: StateFlow<CoinDetail?> = _coinDetail

    private val _ohlcData = MutableStateFlow<List<OhlcEntry>>(emptyList())
    val ohlcData: StateFlow<List<OhlcEntry>> = _ohlcData

    val addressInput = MutableStateFlow("")
    private val _addressInfo = MutableStateFlow<AddressBalanceInfo?>(null)
    val addressInfo: StateFlow<AddressBalanceInfo?> = _addressInfo

    init {
        viewModelScope.launch {
            _coinDetail.value = repository.getCoinDetail(coinId)
        }
        loadOhlcData(30)
    }

    fun loadOhlcData(days: Int) {
        viewModelScope.launch {
            val rawData = repository.getOhlcData(coinId, days)
            _ohlcData.value = rawData.map {
                OhlcEntry(
                    timestamp = it[0].toLong(),
                    open = it[1].toFloat(),
                    high = it[2].toFloat(),
                    low = it[3].toFloat(),
                    close = it[4].toFloat()
                )
            }
        }
    }

    fun loadAddressInfo(address: String, network: String, coinPriceUsd: Double?) {
       /* viewModelScope.launch {
            _addressInfo.value = AddressBalanceInfo(
                balance = 500000000L,
                received = 1000000000L,
                spent = 500000000L,
                usdValue = BigDecimal("30214.55"),
                feeSuggestion = "12 sat/B"
            )
        }*/


        viewModelScope.launch {
            try {
                val safeAddress = address.trim()
                Log.d("AddressCheck", "Kontrola: $safeAddress na síti $network")

                val addressData = blockchairRepository.getAddressInfo(network, safeAddress)
                Log.d("AddressCheck", "Klíče v datech: ${addressData.data.keys}")

                val stats = blockchairRepository.getNetworkStats(network)

                val info = addressData.data[safeAddress]?.address

                if (info == null) {
                    Log.e("AddressCheck", " Adresa nebyla nalezena")
                    _addressInfo.value = null
                    return@launch
                }

                val feeSuggestion = stats.data.suggested_transaction_fee_per_byte_sat?.let {
                    "$it sat/B"
                } ?: stats.data.gas_price_wei?.let {
                    it.toLongOrNull()?.div(1_000_000_000)?.toString()?.plus(" Gwei")
                }

                val usd = coinPriceUsd?.let {
                    BigDecimal(info.balance)
                        .divide(BigDecimal(1e8), 8, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(it))
                        .setScale(2, RoundingMode.HALF_UP)
                }

                _addressInfo.value = AddressBalanceInfo(
                    balance = info.balance,
                    received = info.received,
                    spent = info.spent,
                    usdValue = usd,
                    feeSuggestion = feeSuggestion
                )

            } catch (e: Exception) {
                Log.e("AddressCheck", "Chyba při načítání adresy", e)

                if (e is retrofit2.HttpException) {
                    Log.e("AddressCheck", " HTTP ${e.code()} – ${e.message()}")
                }

                _addressInfo.value = null
            }


        }
    }
}
