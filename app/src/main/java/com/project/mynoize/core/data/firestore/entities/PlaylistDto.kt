package com.project.mynoize.core.data.firestore.entities

import com.google.firebase.Timestamp


data class PlaylistDto(
    var id: String,
    val name: String,
    val nameLower: String,
    val creator: String,
    val tags: List<String>,
    val imageLink: String,
    val imagePath: String,
    val songs: List<String>,
    val lastModified: Timestamp
)