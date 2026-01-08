package com.project.mynoize.core.data

data class User(
    val favoriteArtists: List<String> = listOf(),
    val favoriteSongs: List<String> = listOf(),
    val favoritePlaylists: List<String> = listOf(),
    val favoriteAlbums: List<String> = listOf(),
)
