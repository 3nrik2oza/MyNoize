package com.project.mynoize.activities.main.presentation.main_screen

import com.project.mynoize.core.data.Song

data class MainScreenState(
    val songList: List<Song> = emptyList(),
    val isPlaying: Boolean = false,
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L
)
