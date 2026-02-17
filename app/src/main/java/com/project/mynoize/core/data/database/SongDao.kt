package com.project.mynoize.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Upsert
    suspend fun upsertSong(song: LocalSongsEntity)

    @Delete
    suspend fun deleteSong(song: LocalSongsEntity)

    @Query("SELECT * FROM LocalSongsEntity")
    fun getSongs(): Flow<List<LocalSongsEntity>>

    @Query("SELECT id FROM LocalSongsEntity WHERE id IN (:songIds)")
    suspend fun getExistingSongIds(songIds: List<String>): List<String>

    @Query("SELECT * FROM LocalSongsEntity WHERE id IN (:songIds)")
    fun getSongsByIds(songIds: List<String>): Flow<List<LocalSongsEntity>>

    @Query("SELECT * FROM LocalSongsEntity WHERE albumId = :albumId")
    fun getSongsByAlbumId(albumId: String): Flow<List<LocalSongsEntity>>

    @Query("SELECT * FROM LocalSongsEntity WHERE artistId = :artistId")
    fun getSongsByArtistId(artistId: String): Flow<List<LocalSongsEntity>>

}