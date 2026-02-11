package com.project.mynoize.activities.main.presentation.main_screen


import androidx.media3.common.MediaMetadata
import com.project.mynoize.core.data.Song

data class MainScreenState(
    val songList: List<Song> = emptyList(),
    val isPlaying: Boolean = false,
    val currentSong: MediaMetadata? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isConnected: Boolean? = null,
    val loading: Boolean = true,
)
