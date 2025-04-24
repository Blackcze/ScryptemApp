package com.example.scryptem.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferences(private val dataStore: DataStore<Preferences>) {

    private val THEME_KEY = stringPreferencesKey("theme")
    private val CURRENCY_KEY = stringPreferencesKey("currency")
    private val DEFAULT_SCREEN_KEY = stringPreferencesKey("default_screen")
    private val REFRESH_INTERVAL_KEY = stringPreferencesKey("refresh_interval")
    private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")

    val theme: Flow<String> = dataStore.data.map { it[THEME_KEY] ?: "system" }
    val currency: Flow<String> = dataStore.data.map { it[CURRENCY_KEY] ?: "USD" }
    val defaultScreen: Flow<String> = dataStore.data.map { it[DEFAULT_SCREEN_KEY] ?: "List of all" }
    val refreshInterval: Flow<String> = dataStore.data.map { it[REFRESH_INTERVAL_KEY] ?: "15 min" }
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { it[NOTIFICATIONS_KEY] ?: true }

    suspend fun setTheme(value: String) {
        dataStore.edit { it[THEME_KEY] = value }
    }

    suspend fun setCurrency(value: String) {
        dataStore.edit { it[CURRENCY_KEY] = value }
    }

    suspend fun setDefaultScreen(value: String) {
        dataStore.edit { it[DEFAULT_SCREEN_KEY] = value }
    }

    suspend fun setRefreshInterval(value: String) {
        dataStore.edit { it[REFRESH_INTERVAL_KEY] = value }
    }

    suspend fun setNotifications(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_KEY] = enabled }
    }
}
