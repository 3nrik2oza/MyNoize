package com.project.mynoize.activities.main.presentation.create_playlist

import android.net.Uri

sealed class CreatePlaylistEvent {
    data class OnImageChange(val uri: Uri?): CreatePlaylistEvent()
    data class OnPlaylistNameChange(val name: String): CreatePlaylistEvent()
    object OnAddPlaylistClick: CreatePlaylistEvent()
    object OnDismissAlertDialog: CreatePlaylistEvent()
    object OnBackClick: CreatePlaylistEvent()

}