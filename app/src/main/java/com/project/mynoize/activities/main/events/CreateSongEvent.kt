package com.project.mynoize.activities.main.events

sealed class CreateSongEvent {
    data class OnSongNameChange(val songName: String): CreateSongEvent()
    data class OnArtistClick(val index: Int): CreateSongEvent()
    data class OnAlbumClick(val index: Int): CreateSongEvent()
    data class OnCreateAlbumClick(val image: String, val name: String): CreateSongEvent()
    data class OnSelectSongClick(val context: android.content.Context, val songUri: String): CreateSongEvent()
    object OnAddAlbumClick: CreateSongEvent()
    object OnAddSongClick: CreateSongEvent()
    object OnDismissAlertDialog: CreateSongEvent()
}