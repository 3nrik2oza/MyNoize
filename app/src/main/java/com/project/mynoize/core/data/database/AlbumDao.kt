package com.project.mynoize.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Upsert
    suspend fun upsertAlbum(album: LocalAlbumEntity)

    @Delete
    suspend fun deleteAlbum(album: LocalAlbumEntity)

    @Query("SELECT * FROM LocalAlbumEntity")
    fun getAlbums(): Flow<List<LocalAlbumEntity>>

    @Query("SELECT * FROM LocalAlbumEntity where id = :id")
    suspend fun getAlbumFromId(id: String): List<LocalAlbumEntity>
}