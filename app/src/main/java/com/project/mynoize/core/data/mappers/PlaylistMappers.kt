package com.project.mynoize.core.data.mappers

import com.google.firebase.Timestamp
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.database.LocalPlaylistEntity

fun LocalPlaylistEntity.toPlaylist(): Playlist{
    return Playlist(
        id = id,
        name = name,
        nameLower = name.lowercase(),
        creator = creator,
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = favorite,
        songs = songs,
        artists = artists,
        localImagePath = localImagePath,
        lastModified = Timestamp(seconds = lastModified, 0),
        songsDownloaded = songsDownloaded
    )
}

fun Playlist.toLocalPlaylistEntity(localImagePath: String = ""): LocalPlaylistEntity{
    return LocalPlaylistEntity(
        id = id,
        name = name,
        creator = creator,
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = favorite,
        songs = songs,
        artists = artists,
        lastModified = lastModified.seconds,
        localImagePath = localImagePath
    )
}