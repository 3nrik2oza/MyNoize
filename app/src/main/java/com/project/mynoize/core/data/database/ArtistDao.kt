package com.project.mynoize.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Upsert
    suspend fun upsertArtist(artist: LocalArtistEntity)

    @Delete
    suspend fun deleteArtist(artist: LocalArtistEntity)

    @Query("SELECT * FROM LocalArtistEntity")
    fun getArtists(): Flow<List<LocalArtistEntity>>


}