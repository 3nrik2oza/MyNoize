package com.project.mynoize.core.domain.entities

import com.google.firebase.Timestamp

data class Album (
    var id: String = "",
    val name: String = "",
    val imageLink: String = "",
    val imagePath: String = "",
    val localImageUrl: String? = null,
    val creator: String = "",
    val artist: String = "",
    val favorite: Boolean = false,
    val songs: List<String> = listOf(),
    val lastModified: Timestamp = Timestamp.now(),
    val songsDownloaded: Boolean = false
)