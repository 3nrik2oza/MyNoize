package com.project.mynoize.core.data.mappers

import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.data.database.LocalArtistEntity
import com.project.mynoize.core.data.firestore.entities.ArtistDto
import com.project.mynoize.util.Country
import com.project.mynoize.util.Genre

fun LocalArtistEntity.toArtist(): Artist{
    return Artist(
        id = id,
        name = name,
        creator = creator,
        country = null,
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = favorite,
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
        songs = emptyList(),
        localImagePath = localImagePath
    )
}

fun Artist.toDto(): ArtistDto{
    return ArtistDto(
        id = id,
        name = name,
        nameLower = name.lowercase(),
        creator = creator,
        genre = genre?.displayName ?: "",
        country = country?.displayName ?: "",
        imageLink = imageLink,
        imagePath = imagePath
    )
}

fun ArtistDto.toArtist(): Artist{
    return Artist(
        id = id,
        name = name,
        creator = creator,
        genre = Genre.fromDisplayName(genre),
        country = Country.fromDisplayName(country),
        imageLink = imageLink,
        imagePath = imagePath,
        favorite = false
    )
}