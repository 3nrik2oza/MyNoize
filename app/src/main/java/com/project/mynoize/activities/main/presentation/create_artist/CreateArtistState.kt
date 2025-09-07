package com.project.mynoize.activities.main.presentation.create_artist

import android.net.Uri
import com.project.mynoize.core.presentation.UiText

data class CreateArtistState(
    val loading: Boolean = false,
    val artistName: String = "",
    val artistNameError: UiText? = null,
    val artistImage: Uri? = null,
    val artistImageError: UiText? = null
)