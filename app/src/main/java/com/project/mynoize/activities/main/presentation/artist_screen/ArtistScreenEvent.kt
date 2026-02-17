package com.project.mynoize.activities.main.presentation.artist_screen

import com.project.mynoize.core.data.Song

sealed interface ArtistScreenEvent {

    object ArtistFavoriteToggle: ArtistScreenEvent

    object OnModifyArtist: ArtistScreenEvent

    data class OnSongClick(val index: Int): ArtistScreenEvent

    data class OnSongFavoriteToggle(val song: Song): ArtistScreenEvent

    data class SetArtistId(val artistId: String, val isConnected: Boolean): ArtistScreenEvent

    object OnBackClick: ArtistScreenEvent
}