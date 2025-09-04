package com.project.mynoize.activities.main.presentation.create_artist

sealed class CreateArtistEvent {

    data class OnImageChange(val artistImage: String): CreateArtistEvent()
    data class OnArtistNameChange(val artistName: String): CreateArtistEvent()
    object OnAddArtistClick: CreateArtistEvent()
    object OnDismissAlertDialog: CreateArtistEvent()

}