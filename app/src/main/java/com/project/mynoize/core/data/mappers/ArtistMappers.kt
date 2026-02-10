package com.project.mynoize.core.data.mappers

import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.database.LocalArtistEntity

fun LocalArtistEntity.toArtist(): Artist{
    return Artist(
        id = id,
        name = name,
        nameLower = name.lowercase(),
        creator = creator,
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = favorite,
        songs = songs
    )
}

fun Artist.toLocalArtistEntity(localImagePath: String = ""): LocalArtistEntity {
    return LocalArtistEntity(
        id = id,
        name = name,
        creator = creator,
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = favorite,
        songs = songs,
        localImagePath = localImagePath
    )
}