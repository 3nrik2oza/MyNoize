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

    @Query("UPDATE LocalAlbumEntity SET songsDownloaded = :songsDownloaded WHERE id = :id")
    suspend fun updateDownloadField(id: String, songsDownloaded: Boolean)

    @Query("SELECT * FROM LocalAlbumEntity")
    fun getAlbums(): Flow<List<LocalAlbumEntity>>

    @Query("SELECT * FROM LocalAlbumEntity where id = :id")
    suspend fun getAlbumFromId(id: String): List<LocalAlbumEntity>

    @Query("SELECT * FROM LocalAlbumEntity where id in (:ids)")
    fun getAlbumsFromIds(ids: List<String>): List<LocalAlbumEntity>

    @Query("SELECT * FROM LocalAlbumEntity where id in (:ids)")
    fun getAlbumsFlowFromIds(ids: List<String>): Flow<List<LocalAlbumEntity>>


}