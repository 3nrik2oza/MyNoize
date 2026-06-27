package com.project.mynoize.activities.main.presentation.playlist_screen

import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.domain.entities.Playlist
import com.project.mynoize.core.domain.entities.Song
import com.project.mynoize.data_collecting.data.model.SourceType
import com.project.mynoize.util.BottomSheetType

data class PlaylistScreenState(
    val playlist: Playlist = Playlist(),
    val isPlaylist: Boolean = true,
    val favoriteList: Playlist = Playlist(),
    val songs: List<Song> = listOf(),
    val isSheetOpen: Boolean = false,
    val selectPlaylistSheet: Boolean = false,
    val sheetType: BottomSheetType = BottomSheetType.SONG,
    val deletePlaylistSheetOpen : Boolean = false,
    val selectedSong: Song = Song(),
    val artist: Artist = Artist(),
    val userPlaylists: List<Playlist> = listOf(),
    val isUserCreator: Boolean = false,
    val isConnected: Boolean = false,
    val type: SourceType = SourceType.PLAYLIST
){


}
