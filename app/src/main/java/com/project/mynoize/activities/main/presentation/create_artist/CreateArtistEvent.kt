package com.project.mynoize.activities.main.presentation.create_artist

import android.net.Uri

sealed class CreateArtistEvent {

    data class OnImageChange(val artistImage: Uri?): CreateArtistEvent()
    data class OnArtistNameChange(val artistName: String): CreateArtistEvent()
    object OnAddArtistClick: CreateArtistEvent()
    object OnDismissAlertDialog: CreateArtistEvent()
    object OnBackClick: CreateArtistEvent()

}