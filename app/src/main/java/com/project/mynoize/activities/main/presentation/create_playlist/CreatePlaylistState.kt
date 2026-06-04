package com.project.mynoize.activities.main.presentation.create_playlist

import android.net.Uri
import com.project.mynoize.activities.main.presentation.create_playlist.domain.models.Tag
import com.project.mynoize.core.domain.entities.Playlist
import com.project.mynoize.core.presentation.UiText

data class CreatePlaylistState(
    val playlistName: String = "",
    val playlistNameError: UiText? = null,
    val playlistImage: Uri?  = null,
    val playlistImageError: UiText? = null,
    val tags: List<Tag> = emptyList(),
    val tagsError: UiText? = null,
    val playlist: Playlist? = null,
    val loading: Boolean = false
)
