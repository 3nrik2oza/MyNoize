package com.project.mynoize.activities.main.presentation.favorite_screen

import com.project.mynoize.core.data.Playlist

data class FavoriteScreenState(
    val playlists: List<Playlist> = emptyList(),
)
