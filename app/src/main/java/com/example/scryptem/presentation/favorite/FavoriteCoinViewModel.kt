package com.example.scryptem.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.local.entity.FavoriteCoinEntity
import com.example.scryptem.data.repository.CoinRepository
import com.example.scryptem.data.repository.FavoriteCoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteCoinViewModel @Inject constructor(
    private val repository: FavoriteCoinRepository,
    private val coinRepository: CoinRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteCoinEntity>> =
        repository.getAllFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _prices = mutableMapOf<String, MutableStateFlow<Double?>>()

    fun isFavorite(coinId: String): Flow<Boolean> {
        return repository.isFavorite(coinId)
    }

    fun toggleFavorite(coin: FavoriteCoinEntity) {
        viewModelScope.launch {
            repository.isFavorite(coin.id).first().let { isFav ->
                if (isFav) {
                    repository.removeFromFavorites(coin)
                } else {
                    repository.addToFavorites(coin)
                }
            }
        }
    }

    fun getCoinPrice(coinId: String, currency: String): StateFlow<Double?> {
        val key = "$coinId-$currency".lowercase()
        return _prices.getOrPut(key) {
            MutableStateFlow<Double?>(null).also { flow ->
                viewModelScope.launch {
                    try {
                        val detail = coinRepository.getCoinDetail(coinId)
                        val price = detail.market_data.currentPrice[currency.lowercase()]
                        flow.value = price
                    } catch (e: Exception) {
                        flow.value = null
                    }
                }
            }
        }
    }
}
