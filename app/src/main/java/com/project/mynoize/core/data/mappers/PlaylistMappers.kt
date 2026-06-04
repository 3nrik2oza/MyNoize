package com.project.mynoize.core.data.mappers

import com.google.firebase.Timestamp
import com.project.mynoize.activities.main.presentation.create_playlist.domain.models.Tag
import com.project.mynoize.core.domain.entities.Playlist
import com.project.mynoize.core.data.database.LocalPlaylistEntity
import com.project.mynoize.core.data.firestore.entities.PlaylistDto

fun LocalPlaylistEntity.toPlaylist(): Playlist{
    return Playlist(
        id = id,
        name = name,
        creator = creator,
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = favorite,
        songs = songs,
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
        lastModified = lastModified.seconds,
        localImagePath = localImagePath
    )
}

fun Playlist.toFirebasePlaylist() : PlaylistDto{
    return PlaylistDto(
        id = id,
        name = name,
        nameLower = name.lowercase(),
        creator = creator,
        tags = tags.map { it.displayName },
        imageLink = imageLink,
        imagePath = imagePath,
        songs = songs,
        lastModified = lastModified
    )
}

fun PlaylistDto.toPlaylist(): Playlist{
    return Playlist(
        id = id,
        name = name,
        creator = creator,
        tags = tags.map { Tag.fromDisplayName(it)!! },
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = false,
        songs = songs,
        lastModified = lastModified
    )
}