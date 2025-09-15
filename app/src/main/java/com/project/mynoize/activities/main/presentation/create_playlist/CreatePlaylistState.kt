package com.project.mynoize.activities.main.presentation.create_playlist

import android.net.Uri
import com.project.mynoize.core.presentation.UiText

data class CreatePlaylistState(
    val playlistName: String = "",
    val playlistNameError: UiText? = null,
    val playlistImage: Uri?  = null,
    val playlistImageError: UiText? = null,
    val loading: Boolean = false
)
