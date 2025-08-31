package com.project.mynoize.activities.main.state

data class CreateSongState (
    val songName: String = "Name",
    val songUri: String = "",
    val songTitle: String = "Select Song",
    val showCreateAlbum: Boolean = false,
)