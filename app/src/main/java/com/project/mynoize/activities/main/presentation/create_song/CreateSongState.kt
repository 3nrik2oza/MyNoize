package com.project.mynoize.activities.main.presentation.create_song

import com.project.mynoize.core.presentation.UiText

data class CreateSongState (
    val songName: String = "",
    val songNameError: UiText? = null,
    val songUri: String = "",
    val songUriError: UiText? = null,
    val songTitle: String = "Select Song",
    val songGenre: Int = -1,
    val songGenreError: UiText? = null,
    val songSubgenre: Int = -1,
    val songSubgenreError: UiText? = null,
    val showCreateAlbum: Boolean = false,
)