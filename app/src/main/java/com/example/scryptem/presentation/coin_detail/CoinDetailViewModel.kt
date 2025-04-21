package com.example.scryptem.presentation.coin_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.local.AddressPreferences
import com.example.scryptem.data.local.AmountPreferences
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
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val mempoolRepository: MempoolRepository,
    private val addressPreferences: AddressPreferences,
    private val amountPreferences: AmountPreferences,
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

    init {
        viewModelScope.launch {
            _coinDetail.value = repository.getCoinDetail(coinId)
        }

        viewModelScope.launch {
            addressPreferences.address.collectLatest { savedAddress ->
                println("Načtená adresa z DataStore: $savedAddress")
                _addressInput.value = savedAddress ?: ""
            }
        }

        viewModelScope.launch {
            amountPreferences.getAmountFlow(coinId).collectLatest {
                println("Načtené množství z DataStore: $it")
                _ownedAmount.value = it ?: ""
            }
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
        Log.d("AddressCheck", "Kontrola: $address na síti $network")

        viewModelScope.launch {
            try {
                val info = mempoolRepository.getAddressInfo(address)
                val fees = mempoolRepository.getFees()

                val balance = info.chain_stats.funded_txo_sum - info.chain_stats.spent_txo_sum
                val usd = coinPriceUsd?.let {
                    BigDecimal(balance)
                        .divide(BigDecimal(1e8), 8, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(it))
                        .setScale(2, RoundingMode.HALF_UP)
                }

                _addressInfo.value = AddressBalanceInfo(
                    balance = balance,
                    received = info.chain_stats.funded_txo_sum,
                    spent = info.chain_stats.spent_txo_sum,
                    usdValue = usd,
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
        println("Ukládám adresu: $address")
        viewModelScope.launch {
            addressPreferences.saveAddress(address)
        }
    }

    fun onAmountChanged(newAmount: String) {
        _ownedAmount.value = newAmount
        println("Ukládám množství: $newAmount pro coinId=$coinId")
        viewModelScope.launch {
            amountPreferences.saveAmount(coinId, newAmount)
        }
    }
}
