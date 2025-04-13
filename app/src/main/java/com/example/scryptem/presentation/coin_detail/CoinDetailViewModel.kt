package com.example.scryptem.presentation.coin_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.remote.dto.CoinDetail
import com.example.scryptem.data.remote.dto.OhlcEntry
import com.example.scryptem.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val repository: CoinRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = savedStateHandle["coinId"] ?: ""

    private val _coinDetail = MutableStateFlow<CoinDetail?>(null)
    val coinDetail: StateFlow<CoinDetail?> = _coinDetail

    private val _ohlcData = MutableStateFlow<List<OhlcEntry>>(emptyList())
    val ohlcData: StateFlow<List<OhlcEntry>> = _ohlcData

    init {
        viewModelScope.launch {
            _coinDetail.value = repository.getCoinDetail(coinId)
            loadOhlcData(7)
        }
    }

    // 🆕 Funkce pro načtení OHLC dat
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
}
