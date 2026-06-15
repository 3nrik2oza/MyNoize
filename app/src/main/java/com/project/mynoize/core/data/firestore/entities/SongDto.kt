package com.project.mynoize.core.data.firestore.entities

data class SongDto (
    var id: String = "",
    val title: String ="",
    val titleLower: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val genre: String = "",
    val subGenre: String = "",
    val language: String = "",
    val mood: List<String> = emptyList(),
    val era: String = "",
    val songUrl: String = "",
    val audioPath: String = "",
    val albumName: String = "",
    val albumId: String = "",
    val creatorId: String = "",
    val imageUrl: String = "",
)