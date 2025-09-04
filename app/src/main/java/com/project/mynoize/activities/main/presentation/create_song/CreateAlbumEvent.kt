package com.project.mynoize.activities.main.presentation.create_song

import com.project.mynoize.core.domain.InputError

sealed interface CreateAlbumEvent {
    object OnDismissMessageDialog: CreateAlbumEvent
    object OnDismissCreateAlbumDialog: CreateAlbumEvent
    data class OnShowAlertDialog(val error: InputError.CreateAlbum) : CreateAlbumEvent
    data class OnCreateAlbum(val imageUri: String, val albumName: String) : CreateAlbumEvent
}