package com.project.mynoize.activities.main.presentation.favorite_screen

import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.Playlist

data class FavoriteScreenState(
    val playlists: List<Playlist> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Playlist> = emptyList(),
    val addSheetActive: Boolean = false,
)
