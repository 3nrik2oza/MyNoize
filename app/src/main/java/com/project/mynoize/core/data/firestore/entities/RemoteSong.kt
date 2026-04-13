package com.project.mynoize.core.data.firestore.entities

data class RemoteSong (
    var id: String = "",
    val title: String = "",
    val titleLower: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val genre: String = "",
    val subGenre: String = "",
    val songUrl: String = "",
    val albumName: String = "",
    val albumId: String = "",
    val creatorId: String = "",
    val imageUrl: String = "",
)