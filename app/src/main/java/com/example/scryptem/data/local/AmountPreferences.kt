package com.example.scryptem.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AmountPreferences(private val dataStore: DataStore<Preferences>) {

    fun getAmountFlow(coinId: String): Flow<String> {
        val key = stringPreferencesKey("amount_$coinId")
        return dataStore.data.map { prefs -> prefs[key] ?: "" }
    }

    suspend fun saveAmount(coinId: String, amount: String) {
        val key = stringPreferencesKey("amount_$coinId")
        dataStore.edit { prefs ->
            prefs[key] = amount
        }
    }
}
