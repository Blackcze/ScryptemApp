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
import kotlinx.coroutines.Job
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import android.util.Log

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

    private var loadJob: Job? = null

    init {
        Log.d("CoinDetailVM", "Init ViewModel for coinId = $coinId")

        if (coinId.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    _coinDetail.value = repository.getCoinDetail(coinId)
                } catch (e: Exception) {
                    Log.e("CoinDetailVM", "Failed to load coin detail: ${e.message}")
                }
            }

            //Načítej pouze pokud ještě nejsou data (ochrana proti opakování)
            if (_ohlcData.value.isEmpty()) {
                loadOhlcData(7)
            }
        } else {
            Log.e("CoinDetailVM", "coinId is EMPTY or NULL")
        }
    }

    private var lastLoadedCoinId: String? = null
    private var lastLoadedDays: Int? = null
    private var lastLoadTimeMillis: Long = 0

    fun loadOhlcData(days: Int) {
        val now = System.currentTimeMillis()

        // Pokud je požadavek identický a byl proveden nedávno, přeskočíme
        if (
            lastLoadedCoinId == coinId &&
            lastLoadedDays == days &&
            now - lastLoadTimeMillis < 5000 // 5 sekund cooldown
        ) {
            Log.d("CoinDetailVM", "Skipping duplicate OHLC load for $coinId ($days dní)")
            return
        }

        lastLoadedCoinId = coinId
        lastLoadedDays = days
        lastLoadTimeMillis = now

        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            try {
                val rawData = repository.getOhlcData(coinId, days)

                val mapped = rawData
                    .filter { it.size >= 5 }
                    .map {
                        OhlcEntry(
                            timestamp = it[0].toLong(),
                            open = it[1].toFloat(),
                            high = it[2].toFloat(),
                            low = it[3].toFloat(),
                            close = it[4].toFloat()
                        )
                    }

                _ohlcData.value = mapped
                Log.d("CoinDetailVM", "Loaded ${mapped.size} OHLC records for $coinId")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CoinDetailVM", "Error loading OHLC: ${e.message}")
                _ohlcData.value = emptyList()
            }
        }
    }

}
