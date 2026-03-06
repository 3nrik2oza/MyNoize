package com.project.mynoize.core.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Upsert
    suspend fun upsertSong(song: LocalSongsEntity)

    @Query("DELETE FROM LocalSongsEntity WHERE id = :id")
    suspend fun deleteSong(id: String)

    @Query("SELECT * FROM LocalSongsEntity")
    fun getSongs(): Flow<List<LocalSongsEntity>>

    @Query("SELECT id FROM LocalSongsEntity WHERE id IN (:songIds)")
    suspend fun getExistingSongIds(songIds: List<String>): List<String>

    @Query("SELECT * FROM LocalSongsEntity WHERE id IN (:songIds)")
    suspend fun getSongsByIds(songIds: List<String>): List<LocalSongsEntity>

    @Query("SELECT * FROM LocalSongsEntity WHERE albumId = :albumId")
    suspend fun getSongsByAlbumId(albumId: String): List<LocalSongsEntity>

    @Query("SELECT id FROM LocalSongsEntity WHERE albumId IN (:albumIds)")
    suspend fun getSongsFromAlbumLists(albumIds: List<String>): List<String>

    @Query("SELECT * FROM LocalSongsEntity WHERE artistId = :artistId")
    suspend fun getSongsByArtistId(artistId: String): List<LocalSongsEntity>

    @Query("SELECT * from LocalSongsEntity")
    suspend fun getAllSong(): List<LocalSongsEntity>


}