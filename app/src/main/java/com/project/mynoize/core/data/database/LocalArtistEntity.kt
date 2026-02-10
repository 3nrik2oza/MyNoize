package com.project.mynoize.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalArtistEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val creator: String,
    val imageLink: String,
    val imagePath: String,
    val favorite: Boolean,
    val songs: List<String>,
    val localImagePath: String
)
