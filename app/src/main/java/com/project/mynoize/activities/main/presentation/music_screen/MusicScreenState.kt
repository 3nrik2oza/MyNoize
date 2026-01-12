package com.project.mynoize.activities.main.presentation.music_screen

import com.project.mynoize.core.data.Song

data class MusicScreenState(
    val name: String = "",
    val songsForUser : List<Song> = emptyList(),
)