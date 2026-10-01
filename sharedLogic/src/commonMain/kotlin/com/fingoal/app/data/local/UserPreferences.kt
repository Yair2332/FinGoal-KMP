package com.fingoal.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private val USER_ID_KEY = stringPreferencesKey("user_id")

    suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs -> prefs[USER_ID_KEY] = userId }
    }

    val userId: Flow<String> = dataStore.data
        .map { prefs -> prefs[USER_ID_KEY] ?: "" }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}