package com.project.mynoize.activities.main.presentation.playlist_screen

import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.Song
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
    val selectedSongIndex: Int = 0,
    val artist: Artist = Artist(),
    val userPlaylists: List<Playlist> = listOf(),
){

    fun selectedSong():Song{
        return songs[selectedSongIndex]
    }

}
