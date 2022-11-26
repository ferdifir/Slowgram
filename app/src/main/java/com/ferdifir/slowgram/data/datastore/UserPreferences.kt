package com.ferdifir.slowgram.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ferdifir.slowgram.utils.Const
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    fun getUserToken(): Flow<String?> = dataStore.data.map { it[USER_TOKEN] }

    suspend fun saveLoginSession(userToken: String) {
        dataStore.edit { pref ->
            pref[USER_TOKEN] = userToken
        }
    }

    suspend fun clearLoginSession() {
        dataStore.edit { it.clear() }
    }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Const.preferencesName)
        private val USER_TOKEN = stringPreferencesKey("user_token")
    }
}