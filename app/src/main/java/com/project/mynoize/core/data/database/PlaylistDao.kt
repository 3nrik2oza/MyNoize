package com.project.mynoize.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Upsert
    suspend fun upsertPlaylist(playlist: LocalPlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: LocalPlaylistEntity)

    @Query("SELECT * FROM LocalPlaylistEntity")
    fun getPlaylists(): Flow<List<LocalPlaylistEntity>>

    @Query("UPDATE LocalPlaylistEntity SET songsDownloaded = :songsDownloaded WHERE id = :id")
    suspend fun updateDownloadField(id: String, songsDownloaded: Boolean)

    @Query("SELECT * FROM LocalPlaylistEntity WHERE id = :id")
    fun getPlaylist(id: String): Flow<LocalPlaylistEntity?>

}