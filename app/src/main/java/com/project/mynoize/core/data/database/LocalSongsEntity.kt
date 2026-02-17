package com.project.mynoize.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalSongsEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val genre: String,
    val subGenre: String,
    val songUrl: String,
    val albumName: String,
    val albumId: String,
    val creatorId: String,
    val imageUrl: String,
    val localImageUrl: String,
    val favorite: Boolean,
    val localSongUrl: String
)
