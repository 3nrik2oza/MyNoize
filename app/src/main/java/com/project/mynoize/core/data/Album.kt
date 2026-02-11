package com.project.mynoize.core.data

import com.google.firebase.Timestamp

data class Album (
    var id: String = "",
    val name: String = "",
    val nameLower: String = "",
    val image: String = "",
    val localImageUrl: String = "",
    val creator: String = "",
    val artist: String = "",
    val favorite: Boolean = false,
    val songs: List<String> = listOf(),
    val lastModified: Timestamp = Timestamp.now(),
    val songsDownloaded: Boolean = false
)