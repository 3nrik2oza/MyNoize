package com.project.mynoize.core.data

import com.google.firebase.Timestamp


data class Playlist(
    var id: String = "",
    val name: String = "",
    val nameLower: String = "",
    val creator: String = "",
    val imageLink: String = "",
    val imagePath: String = "",
    val favorite: Boolean = false,
    val songs: List<String> = listOf(),
    val artists: List<String> = listOf(),
    val localImagePath: String = "",
    val songsDownloaded: Boolean = false,
    val lastModified: Timestamp = Timestamp.now()
)