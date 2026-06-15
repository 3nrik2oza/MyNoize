package com.project.mynoize.activities.main.presentation.create_song

import com.project.mynoize.core.domain.InputError
import com.project.mynoize.util.Era

sealed interface CreateAlbumEvent {
    object OnDismissMessageDialog: CreateAlbumEvent
    object OnDismissCreateAlbumDialog: CreateAlbumEvent
    data class OnShowAlertDialog(val error: InputError.CreateAlbum) : CreateAlbumEvent
    data class OnCreateAlbum(val imageUri: String, val albumName: String, val era: Era) : CreateAlbumEvent
}