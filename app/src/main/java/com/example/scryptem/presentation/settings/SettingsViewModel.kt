package com.example.scryptem.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scryptem.data.local.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: SettingsPreferences
) : ViewModel() {

    val theme: StateFlow<String> = prefs.theme.stateIn(viewModelScope, SharingStarted.Lazily, "system")
    val currency: StateFlow<String> = prefs.currency.stateIn(viewModelScope, SharingStarted.Lazily, "USD")
    val defaultScreen: StateFlow<String> = prefs.defaultScreen.stateIn(viewModelScope, SharingStarted.Lazily, "List of all")



    fun setTheme(value: String) {
        viewModelScope.launch { prefs.setTheme(value) }
    }

    fun setCurrency(value: String) {
        viewModelScope.launch { prefs.setCurrency(value) }
    }

    fun setDefaultScreen(value: String) {
        viewModelScope.launch { prefs.setDefaultScreen(value) }
    }

}
