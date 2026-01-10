package com.project.mynoize.activities.main.presentation.favorite_screen


sealed interface FavoriteScreenEvent {

    data class OnArtistClicked(val artistId: String) : FavoriteScreenEvent

    class OnPlaylistClicked(val playlistId: String, val isPlaylist: Boolean) : FavoriteScreenEvent

    object OnCreatePlaylist: FavoriteScreenEvent

}