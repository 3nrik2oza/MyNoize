package com.project.mynoize.core.data

data class Song (
    var id: String = "",
    val title: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val songUrl: String = "",
    val albumName: String = "",
    val albumId: String = "",
    val creatorId: String = "",
    val imageUrl: String = "",
    var position: Int = 0,
    val favorite: Boolean = false,
)