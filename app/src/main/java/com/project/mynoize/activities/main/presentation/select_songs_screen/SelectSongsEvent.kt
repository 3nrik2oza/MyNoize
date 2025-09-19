package com.project.mynoize.activities.main.presentation.select_songs_screen

sealed interface SelectSongsEvent {
    data class OnSongClicked(val index: Int, val add: Boolean): SelectSongsEvent
    data class SetPlaylist(val playlistId: String): SelectSongsEvent
    object OnFinishClick: SelectSongsEvent
    object OnDismissAlertDialog: SelectSongsEvent
    object OnBackClick: SelectSongsEvent
}