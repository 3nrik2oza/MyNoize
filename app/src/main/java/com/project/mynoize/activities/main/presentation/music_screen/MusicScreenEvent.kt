package com.project.mynoize.activities.main.presentation.music_screen

interface MusicScreenEvent {

    object OnPlaySongsForUser: MusicScreenEvent

    data class OnPlaylistClicked(val id: String, val isPlaylist: Boolean): MusicScreenEvent

}