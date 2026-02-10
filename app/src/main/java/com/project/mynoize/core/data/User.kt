package com.project.mynoize.core.data

import com.google.firebase.Timestamp

data class User(
    val favoriteArtists: List<String> = listOf(),
    val favoriteSongs: List<String> = listOf(),
    val favoritePlaylists: List<String> = listOf(),
    val favoriteAlbums: List<String> = listOf(),

    val lastModifiedFavoriteArtists: Timestamp = Timestamp.now(),
    val lastModifiedFavoriteSongs: Timestamp = Timestamp.now(),
    val lastModifiedFavoritePlaylists: Timestamp = Timestamp.now(),
    val lastModifiedFavoriteAlbums: Timestamp = Timestamp.now(),
)
