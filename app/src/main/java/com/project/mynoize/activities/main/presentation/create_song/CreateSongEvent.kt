package com.project.mynoize.activities.main.presentation.create_song

import android.content.Context

sealed interface CreateSongEvent {
    data class OnSongNameChange(val songName: String): CreateSongEvent
    data class OnArtistClick(val index: Int): CreateSongEvent
    data class OnAlbumClick(val index: Int): CreateSongEvent
    data class OnSelectSongClick(val context: Context, val songUri: String): CreateSongEvent
    object OnAddAlbumClick: CreateSongEvent
    object OnAddSongClick: CreateSongEvent
    object OnDismissAlertDialog: CreateSongEvent
}