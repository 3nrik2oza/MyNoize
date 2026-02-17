package com.project.mynoize.core.data

data class Song (
    var id: String = "",
    val title: String = "",
    val titleLower: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val genre: String = "",
    val subgenre: String = "",
    val songUrl: String = "",
    val localSongUrl: String = "",
    val albumName: String = "",
    val albumId: String = "",
    val creatorId: String = "",
    val imageUrl: String = "",
    val localImageUrl: String = "",
    var position: Int = 0,
    val favorite: Boolean = false,
)