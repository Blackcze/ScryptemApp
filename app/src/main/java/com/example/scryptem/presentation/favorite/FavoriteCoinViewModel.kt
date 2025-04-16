package com.example.scryptem.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.local.entity.FavoriteCoinEntity
import com.example.scryptem.data.repository.FavoriteCoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteCoinViewModel @Inject constructor(
    private val repository: FavoriteCoinRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteCoinEntity>> =
        repository.getAllFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
}
