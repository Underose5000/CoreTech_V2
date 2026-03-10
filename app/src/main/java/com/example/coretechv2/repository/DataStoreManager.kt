package com.example.coretechv2.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "app_preferences")

class DataStoreManager(private val context: Context) {

    private val API_URL_KEY = stringPreferencesKey("apiUrl")
    private val API_PORT_KEY = stringPreferencesKey("apiPort")
    private val API_KEY_KEY = stringPreferencesKey("apiKey")


    suspend fun saveApiSettings(apiUrl: String, apiPort: String, apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[API_URL_KEY] = apiUrl
        }
        context.dataStore.edit { preferences ->
            preferences[API_PORT_KEY] = apiPort
        }
        context.dataStore.edit { preferences ->
            preferences[API_KEY_KEY] = apiKey
        }
    }

    val apiUrlFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[API_URL_KEY]
        }

    val apiPortFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[API_PORT_KEY]
        }

    val apiKeyFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[API_KEY_KEY]
        }
}