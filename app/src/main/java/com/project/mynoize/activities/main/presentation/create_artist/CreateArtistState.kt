package com.project.mynoize.activities.main.presentation.create_artist

import android.net.Uri
import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.util.Country

data class CreateArtistState(
    val loading: Boolean = false,
    val artistImage: Uri? = null,
    val artistImageError: UiText? = null,
    val artistName: String = "",
    val artistNameError: UiText? = null,
    val country: Country? = null,
    val countryError: UiText? = null,
    val artistToModify: Artist? = null,
)