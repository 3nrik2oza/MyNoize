package com.project.mynoize.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class UserInformation(
    private val context: Context
){
    val Context.dataStore by preferencesDataStore("user_info")

    val mediaId = context.dataStore.data.map { preferences ->
        preferences[mediaKey]
    }

    suspend fun updateMediaId(id: String) =
        context.dataStore.edit { settings ->
            settings[mediaKey] = id
        }

    companion object {
        val mediaKey = stringPreferencesKey("media_id_key")
    }

}