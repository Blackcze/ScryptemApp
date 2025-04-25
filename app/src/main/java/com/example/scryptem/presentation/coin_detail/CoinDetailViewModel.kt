package com.example.scryptem.presentation.coin_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.example.scryptem.data.local.AddressPreferences
import com.example.scryptem.data.local.AmountPreferences
import com.example.scryptem.data.local.SettingsPreferences
import com.example.scryptem.data.remote.dto.CoinDetail
import com.example.scryptem.data.remote.dto.OhlcEntry
import com.example.scryptem.data.repository.CoinRepository
import com.example.scryptem.data.repository.MempoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val mempoolRepository: MempoolRepository,
    private val addressPreferences: AddressPreferences,
    private val amountPreferences: AmountPreferences,
    private val settingsPreferences: SettingsPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = savedStateHandle["coinId"] ?: ""

    private val _coinDetail = MutableStateFlow<CoinDetail?>(null)
    val coinDetail: StateFlow<CoinDetail?> = _coinDetail

    private val _ohlcData = MutableStateFlow<List<OhlcEntry>>(emptyList())
    val ohlcData: StateFlow<List<OhlcEntry>> = _ohlcData

    private val _addressInput = MutableStateFlow("")
    val addressInput: StateFlow<String> = _addressInput

    private val _ownedAmount = MutableStateFlow("")
    val ownedAmount: StateFlow<String> = _ownedAmount

    private val _addressInfo = MutableStateFlow<AddressBalanceInfo?>(null)
    val addressInfo: StateFlow<AddressBalanceInfo?> = _addressInfo

    private val _selectedCurrency = MutableStateFlow("usd")
    val selectedCurrency: StateFlow<String> = _selectedCurrency

    private val lastOhlcLoad = mutableMapOf<Int, Long>()

    init {
        viewModelScope.launch {
            settingsPreferences.currency.collectLatest { currency ->
                _selectedCurrency.value = currency.lowercase()

                try {
                    _coinDetail.value = repository.getCoinDetail(coinId)
                    loadOhlcData(30, currency)
                } catch (e: Exception) {
                    Log.e("CoinDetailVM", "Nepodařilo se načíst detail coinu", e)
                }
            }
        }

        viewModelScope.launch {
            addressPreferences.address.collectLatest { savedAddress ->
                _addressInput.value = savedAddress ?: ""
            }
        }

        viewModelScope.launch {
            amountPreferences.getAmountFlow(coinId).collectLatest {
                _ownedAmount.value = it ?: ""
            }
        }
    }

    fun loadOhlcData(days: Int, currency: String) {
        val now = System.currentTimeMillis()
        val lastLoad = lastOhlcLoad[days] ?: 0
        if (now - lastLoad < 5000) return
        lastOhlcLoad[days] = now

        viewModelScope.launch {
            try {
                val rawData = repository.getOhlcData(coinId, days, currency.lowercase())
                _ohlcData.value = rawData.map {
                    OhlcEntry(
                        timestamp = it[0].toLong(),
                        open = it[1].toFloat(),
                        high = it[2].toFloat(),
                        low = it[3].toFloat(),
                        close = it[4].toFloat()
                    )
                }
            } catch (e: Exception) {
                Log.e("CoinDetailVM", "Chyba při načítání OHLC", e)
            }
        }
    }

    fun loadAddressInfo(address: String, network: String, coinDetail: CoinDetail) {
        val coinPrice = coinDetail.market_data.currentPrice[_selectedCurrency.value]

        viewModelScope.launch {
            try {
                val info = mempoolRepository.getAddressInfo(address)
                val fees = mempoolRepository.getFees()

                val balance = info.chain_stats.funded_txo_sum - info.chain_stats.spent_txo_sum
                val fiatValue = coinPrice?.let {
                    BigDecimal(balance)
                        .divide(BigDecimal(1e8), 8, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(it))
                        .setScale(2, RoundingMode.HALF_UP)
                }

                _addressInfo.value = AddressBalanceInfo(
                    balance = balance,
                    received = info.chain_stats.funded_txo_sum,
                    spent = info.chain_stats.spent_txo_sum,
                    usdValue = fiatValue,
                    feeSuggestion = "${fees.hourFee} sat/vB"
                )
            } catch (e: Exception) {
                Log.e("AddressCheck", "Chyba při načítání adresy", e)
                _addressInfo.value = null
            }
        }
    }

    fun onAddressEntered(address: String) {
        _addressInput.value = address
        viewModelScope.launch {
            addressPreferences.saveAddress(address)
        }
    }

    fun onAmountChanged(newAmount: String) {
        _ownedAmount.value = newAmount
        viewModelScope.launch {
            amountPreferences.saveAmount(coinId, newAmount)
        }
    }
}
