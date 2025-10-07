package com.project.mynoize.activities.main.presentation.playlist_screen


sealed interface PlaylistScreenEvent {
    object OnAddSongClick: PlaylistScreenEvent
    data class SetPlaylistId(val playlistId: String): PlaylistScreenEvent
    data class OnMoreClick(val index: Int): PlaylistScreenEvent
    data class OnSongClicked(val index: Int): PlaylistScreenEvent

    object OnRemoveSongClick: PlaylistScreenEvent

    object OnDismissAlertDialog: PlaylistScreenEvent
    object OnBackClick: PlaylistScreenEvent

}