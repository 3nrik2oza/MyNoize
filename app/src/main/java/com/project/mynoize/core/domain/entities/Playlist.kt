package com.project.mynoize.core.domain.entities

import com.google.firebase.Timestamp
import com.project.mynoize.activities.main.presentation.create_playlist.domain.models.Tag


data class Playlist(
    var id: String = "",
    val name: String = "",
    val creator: String = "",
    val tags: List<Tag> = emptyList(),
    val imageLink: String = "",
    val imagePath: String = "",
    val favorite: Boolean = false,
    val songs: List<String> = listOf(),
    val localImagePath: String = "",
    val songsDownloaded: Boolean = false,
    val lastModified: Timestamp = Timestamp.now()
)
//TODO() you need to add tags for playlists