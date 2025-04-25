package com.example.scryptem.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.remote.Coin
import com.example.scryptem.data.repository.CoinRepository
import com.example.scryptem.data.local.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val settingsPrefs: SettingsPreferences
) : ViewModel() {

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    val coins: StateFlow<List<Coin>> = _coins

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            settingsPrefs.currency.collectLatest { selectedCurrency ->
                loadCoins(selectedCurrency)
            }
        }
    }

    private fun loadCoins(currency: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getCoins(vsCurrency = currency.lowercase())
                _coins.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
