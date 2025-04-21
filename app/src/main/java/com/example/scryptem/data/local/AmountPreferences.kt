package com.example.scryptem.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "amount_settings")

class AmountPreferences(private val context: Context) {

    fun getAmountFlow(coinId: String): Flow<String> {
        val key = stringPreferencesKey("amount_$coinId")
        return context.dataStore.data.map { prefs -> prefs[key] ?: "" }
    }

    suspend fun saveAmount(coinId: String, amount: String) {
        val key = stringPreferencesKey("amount_$coinId")
        context.dataStore.edit { prefs ->
            prefs[key] = amount
        }
    }
}
