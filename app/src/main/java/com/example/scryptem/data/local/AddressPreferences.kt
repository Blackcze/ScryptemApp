package com.example.scryptem.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class AddressPreferences(private val context: Context) {
    companion object {
        private val ADDRESS_KEY = stringPreferencesKey("wallet_address")
    }

    val address: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ADDRESS_KEY]
    }

    suspend fun saveAddress(address: String) {
        context.dataStore.edit { prefs ->
            prefs[ADDRESS_KEY] = address
        }
    }
}
