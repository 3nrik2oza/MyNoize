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

    val position = context.dataStore.data.map { preferences ->
        preferences[positionKey]
    }

    val playlistId = context.dataStore.data.map { preferences ->
        preferences[playlistKey]
    }

    suspend fun updateMediaId(id: String) =
        context.dataStore.edit { settings ->
            settings[mediaKey] = id
        }

    suspend fun updatePosition(position: Long) =
        context.dataStore.edit { settings ->
            settings[positionKey] = position.toString()
        }

    suspend fun updatePlaylist(playlistId: String) =
        context.dataStore.edit { settings ->
            settings[playlistKey] = playlistId
        }

    suspend fun clearAll(){
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        val mediaKey = stringPreferencesKey("media_id_key")
        val positionKey = stringPreferencesKey("position_key")
        val playlistKey = stringPreferencesKey("playlist_id_key")
    }

}