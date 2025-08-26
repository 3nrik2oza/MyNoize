package com.project.mynoize.activities.main.events

sealed class CreateAlbumEvent {
    object OnDismissMessageDialog: CreateAlbumEvent()
    object OnDismissCreateAlbumDialog: CreateAlbumEvent()
    data class OnShowAlertDialog(val message: String) : CreateAlbumEvent()
    data class OnCreateAlbum(val imageUri: String, val albumName: String) : CreateAlbumEvent()
}