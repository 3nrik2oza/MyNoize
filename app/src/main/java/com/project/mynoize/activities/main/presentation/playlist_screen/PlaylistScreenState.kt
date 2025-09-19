package com.project.mynoize.activities.main.presentation.playlist_screen

import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.Song

data class PlaylistScreenState(
    val playlist: Playlist = Playlist(),
    val songs: List<Song> = listOf(),
    val isSheetOpen: Boolean = false,
    val selectedSongIndex: Int = 0,
    val artist: Artist = Artist()
){

    fun selectedSong():Song{
        return songs[selectedSongIndex]
    }

}
