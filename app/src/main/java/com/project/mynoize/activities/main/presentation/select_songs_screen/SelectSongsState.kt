package com.project.mynoize.activities.main.presentation.select_songs_screen

import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.domain.entities.Song

data class SelectSongsState(
    val query: String = "",
    val songs: List<Song> = emptyList(),
    val selectedRemoteSongs: List<Song> = emptyList(),
    val playlist: Playlist = Playlist()
)
