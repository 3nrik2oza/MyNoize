package com.project.mynoize.activities.main.events

sealed class CreateArtistEvent {

    data class OnImageChange(val artistImage: String): CreateArtistEvent()
    data class OnArtistNameChange(val artistName: String): CreateArtistEvent()
    object OnAddArtistClick: CreateArtistEvent()
    object OnDismissAlertDialog: CreateArtistEvent()

}