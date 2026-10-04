package com.fingoal.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {

    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    val userId: Flow<String> = dataStore.data
        .map { prefs ->
            prefs[USER_ID_KEY] ?: ""
        }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDarkMode
        }
    }

    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[DARK_MODE_KEY] ?: false
        }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
        }
    }
}
