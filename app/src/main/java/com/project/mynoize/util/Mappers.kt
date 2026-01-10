package com.project.mynoize.util

import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.Playlist

fun Album.toPlaylist(): Playlist {
    return Playlist(
        id = id,
        name = name,
        nameLower = nameLower,
        creator = creator,
        imageLink = image,
        imagePath = "",
        favorite = favorite,
        songs = songs,
        artists = if (artist.isNotBlank()) listOf(artist) else emptyList()
    )
}