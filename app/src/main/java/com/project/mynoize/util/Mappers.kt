package com.project.mynoize.util

import com.project.mynoize.core.domain.entities.Album
import com.project.mynoize.core.domain.entities.Playlist

fun Album.toPlaylist(): Playlist {
    return Playlist(
        id = id,
        name = name,
        creator = creator,
        imageLink = imageLink,
        imagePath = artist,
        favorite = favorite,
        songs = songs,
    )
}