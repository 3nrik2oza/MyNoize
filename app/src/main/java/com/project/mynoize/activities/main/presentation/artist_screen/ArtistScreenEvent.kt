package com.project.mynoize.activities.main.presentation.artist_screen


import com.project.mynoize.core.domain.entities.Song

sealed interface ArtistScreenEvent {

    object ArtistFavoriteToggle: ArtistScreenEvent

    object OnModifyArtist: ArtistScreenEvent

    data class OnSongClick(val index: Int): ArtistScreenEvent

    data class OnSongFavoriteToggle(val remoteSong: Song): ArtistScreenEvent

    data class SetArtistId(val artistId: String, val isConnected: Boolean): ArtistScreenEvent

    object OnBackClick: ArtistScreenEvent
}