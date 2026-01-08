package com.project.mynoize.activities.main.presentation.artist_screen

import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.Song

data class ArtistScreenState(
    val artist: Artist = Artist(),
    val favorite: Boolean = false,
    val songs: List<Song> = listOf(),
)