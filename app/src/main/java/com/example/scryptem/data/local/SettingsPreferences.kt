package com.example.scryptem.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferences(private val dataStore: DataStore<Preferences>) {

    private val themeKey = stringPreferencesKey("theme")
    private val currencyKey = stringPreferencesKey("currency")
    private val defaultScreenKey = stringPreferencesKey("default_screen")

    val theme: Flow<String> = dataStore.data.map { it[themeKey] ?: "system" }
    val currency: Flow<String> = dataStore.data.map { it[currencyKey] ?: "USD" }
    val defaultScreen: Flow<String> = dataStore.data.map { it[defaultScreenKey] ?: "List of all" }

    suspend fun setTheme(value: String) {
        dataStore.edit { it[themeKey] = value }
    }

    suspend fun setCurrency(value: String) {
        dataStore.edit { it[currencyKey] = value }
    }

    suspend fun setDefaultScreen(value: String) {
        dataStore.edit { it[defaultScreenKey] = value }
    }
}
