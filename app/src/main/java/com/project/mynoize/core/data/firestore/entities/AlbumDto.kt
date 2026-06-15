package com.project.mynoize.core.data.firestore.entities

import com.google.firebase.Timestamp

data class AlbumDto (
    var id: String = "",
    val name: String = "",
    val nameLower: String = "",
    val imageLink: String = "",
    val imagePath: String = "",
    val songList: List<String> = emptyList(),
    val era: String = "",
    val creator: String = "",
    val artist: String = "",
    val lastModified: Timestamp = Timestamp.now(),
)