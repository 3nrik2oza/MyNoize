package com.project.mynoize.activities.main.presentation.create_artist

import android.net.Uri
import com.project.mynoize.util.Country

sealed interface CreateArtistEvent {

    data class OnImageChange(val artistImage: Uri?): CreateArtistEvent
    data class OnArtistNameChange(val artistName: String): CreateArtistEvent
    data class OnArtistCountryChange(val selected: Country): CreateArtistEvent

    data class OnModifyArtist(val artistId: String): CreateArtistEvent

    object OnAddArtistClick: CreateArtistEvent
    object OnDismissAlertDialog: CreateArtistEvent
    object OnBackClick: CreateArtistEvent

}