package com.project.mynoize.activities.main.presentation.playlist_screen

import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.Song


sealed interface PlaylistScreenEvent {
    object OnAddSongClick: PlaylistScreenEvent
    data class SetPlaylistId(val playlistId: String, val isPlaylist: Boolean): PlaylistScreenEvent
    data class OnSongClicked(val index: Int): PlaylistScreenEvent

    data class OnSongFavoriteToggle(val song: Song): PlaylistScreenEvent

    data class OnPlaylistModifyClicked(val playlistId: String) : PlaylistScreenEvent

    data class OnArtistClick(val artistId: String) : PlaylistScreenEvent

    data class OnMoreSongClick(val index: Int): PlaylistScreenEvent
    object OnMorePlaylistClick: PlaylistScreenEvent

    data class OnPlaylistSelected(val playlist: Playlist): PlaylistScreenEvent

    object OnToggleSelectPlaylistSheet: PlaylistScreenEvent

    object OnToggleDeletePlaylist: PlaylistScreenEvent

    object OnDeletePlaylist: PlaylistScreenEvent

    object OnPlayRandom: PlaylistScreenEvent

    object OnRemoveSongClick: PlaylistScreenEvent

    object OnDismissAlertDialog: PlaylistScreenEvent
    object OnBackClick: PlaylistScreenEvent

}