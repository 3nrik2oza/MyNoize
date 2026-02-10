package com.project.mynoize.core.data.mappers

import com.google.firebase.Timestamp
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.database.LocalAlbumEntity

fun LocalAlbumEntity.toAlbum(): Album {
    return Album(
        id = id,
        name = name,
        nameLower = name.lowercase(),
        image = image,
        creator = creator,
        artist = artist,
        favorite = favorite,
        songs = songs,
        lastModified = Timestamp(seconds = lastModified, 0)
    )
}

fun Album.toLocalAlbumEntity(): LocalAlbumEntity {
    return LocalAlbumEntity(
        id = id,
        name = name,
        image = image,
        creator = creator,
        artist = artist,
        favorite = favorite,
        songs = songs,
        localImageUrl = localImageUrl,
        lastModified = lastModified.seconds
    )
}