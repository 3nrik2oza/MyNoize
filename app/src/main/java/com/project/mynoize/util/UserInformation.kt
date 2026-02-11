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

    val lastModifiedArtist = context.dataStore.data.map { preferences ->
        preferences[lastModifiedArtistKey]
    }

    val lastModifiedSong = context.dataStore.data.map { preferences ->
        preferences[lastModifiedSongKey]
    }

    val lastModifiedFavPlaylists = context.dataStore.data.map { preferences ->
        preferences[lastModifiedPlaylistKey]
    }

    val lastModifiedFavAlbums = context.dataStore.data.map { preferences ->
        preferences[lastModifiedAlbumKey]
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

    suspend fun updateLastModifiedArtist(lastModified: Long) =
        context.dataStore.edit { settings ->
            settings[lastModifiedArtistKey] = lastModified.toString()
        }

    suspend fun updateLastModifiedSong(lastModified: Long) =
        context.dataStore.edit { settings ->
            settings[lastModifiedSongKey] = lastModified.toString()
        }

    suspend fun updateLastModifiedPlaylist(lastModified: Long) =
        context.dataStore.edit { settings ->
            settings[lastModifiedPlaylistKey] = lastModified.toString()
        }

    suspend fun updateLastModifiedAlbum(lastModified: Long) =
        context.dataStore.edit { settings ->
            settings[lastModifiedAlbumKey] = lastModified.toString()
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

        val lastModifiedArtistKey = stringPreferencesKey("last_modified_artist_key")
        val lastModifiedSongKey = stringPreferencesKey("last_modified_song_key")
        val lastModifiedPlaylistKey = stringPreferencesKey("last_modified_playlist_key")
        val lastModifiedAlbumKey = stringPreferencesKey("last_modified_album_key")
    }

}