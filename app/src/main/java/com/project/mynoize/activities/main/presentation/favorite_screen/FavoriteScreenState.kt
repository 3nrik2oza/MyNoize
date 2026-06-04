package com.project.mynoize.activities.main.presentation.favorite_screen

import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.domain.entities.Playlist

data class FavoriteScreenState(
    val playlists: List<Playlist> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Playlist> = emptyList(),
    val addSheetActive: Boolean = false,
)
