package com.project.mynoize.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalPlaylistEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val creator: String,
    val imageLink: String,
    val imagePath: String,
    val favorite: Boolean,
    val songs: List<String>,
    val artists: List<String>,
    val localImagePath: String,
    val lastModified: Long,
    val songsDownloaded: Boolean = false
)
