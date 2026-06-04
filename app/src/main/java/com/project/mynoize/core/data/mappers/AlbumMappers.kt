package com.project.mynoize.core.data.mappers

import com.google.firebase.Timestamp
import com.project.mynoize.core.domain.entities.Album
import com.project.mynoize.core.data.database.LocalAlbumEntity
import com.project.mynoize.core.data.firestore.entities.AlbumDto

fun LocalAlbumEntity.toAlbum(): Album {
    return Album(
        id = id,
        name = name,
        imageLink = image,
        creator = creator,
        artist = artist,
        favorite = favorite,
        localImageUrl = localImageUrl,
        lastModified = Timestamp(seconds = lastModified, 0),
        songsDownloaded = songsDownloaded
    )
}

fun Album.toLocalAlbumEntity(): LocalAlbumEntity {
    return LocalAlbumEntity(
        id = id,
        name = name,
        image = imageLink,
        creator = creator,
        artist = artist,
        favorite = favorite,
        songs = emptyList(),
        localImageUrl = localImageUrl!!,
        lastModified = lastModified.seconds,
        songsDownloaded = songsDownloaded
    )
}

fun AlbumDto.toAlbum(): Album{
    return Album(
        id = id,
        name = name,
        imageLink = imageLink,
        imagePath = imagePath,
        localImageUrl = "",
        creator = creator,
        artist = artist,
        favorite = false,
        songs = emptyList(),
        lastModified = lastModified,
        songsDownloaded = false
    )
}

fun Album.toDto(): AlbumDto{
    return AlbumDto(
        id = id,
        name = name,
        nameLower = name.lowercase(),
        imageLink = imageLink,
        imagePath = imagePath,
        creator = creator,
        artist = artist,
        lastModified = lastModified
    )
}