package com.project.mynoize.activities.main.presentation.create_song

import com.project.mynoize.core.presentation.UiText

data class CreateSongState (
    val songName: String = "",
    val songNameError: UiText? = null,
    val songUri: String = "",
    val songUriError: UiText? = null,
    val songTitle: String = "Select Song",
    val showCreateAlbum: Boolean = false,
)