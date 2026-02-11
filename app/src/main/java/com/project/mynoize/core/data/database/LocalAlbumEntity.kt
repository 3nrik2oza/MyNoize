package com.project.mynoize.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalAlbumEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val image: String,
    val creator: String,
    val artist: String,
    val favorite: Boolean,
    val songs: List<String>,
    val localImageUrl: String,
    val lastModified: Long,
    val songsDownloaded: Boolean = false
)
