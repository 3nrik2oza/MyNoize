package com.project.mynoize.activities.main.presentation.select_songs_screen

import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.Song

data class SelectSongsState(
    val songs: List<Song> = emptyList(),
    val selectedSongs: List<Song> = emptyList(),
    val playlist: Playlist = Playlist()
)
