package com.example.scryptem.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddressPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val ADDRESS_KEY = stringPreferencesKey("wallet_address")
    }

    val address: Flow<String?> = dataStore.data.map { prefs ->
        prefs[ADDRESS_KEY]
    }

    suspend fun saveAddress(address: String) {
        dataStore.edit { prefs ->
            prefs[ADDRESS_KEY] = address
        }
    }
}
