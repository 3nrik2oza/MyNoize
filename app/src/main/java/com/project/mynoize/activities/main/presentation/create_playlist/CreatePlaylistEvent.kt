package com.project.mynoize.activities.main.presentation.create_playlist

import android.net.Uri
import com.project.mynoize.activities.main.presentation.create_playlist.domain.models.Tag

sealed class CreatePlaylistEvent {
    data class OnImageChange(val uri: Uri?): CreatePlaylistEvent()
    data class OnPlaylistNameChange(val name: String): CreatePlaylistEvent()
    data class OnTagSelected(val selected: Tag): CreatePlaylistEvent()

    data class OnModifyPlaylist(val playlistId: String): CreatePlaylistEvent()

    object OnAddPlaylistClick: CreatePlaylistEvent()
    object OnDismissAlertDialog: CreatePlaylistEvent()
    object OnBackClick: CreatePlaylistEvent()

}