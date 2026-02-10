package com.project.mynoize.core.data.mappers


import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.database.LocalSongsEntity

fun LocalSongsEntity.toSong(): Song {
    return Song(
        id = id,
        title = title,
        titleLower = title.lowercase(),
        artistId = artistId,
        artistName = artistName,
        songUrl = songUrl,
        albumName = albumName,
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl,
        favorite = favorite,
        localSongUrl = localSongUrl,
        localImageUrl = localImageUrl
    )
}

fun Song.toLocalSongEntity(): LocalSongsEntity {
    return LocalSongsEntity(
        id = id,
        title = title,
        artistId = artistId,
        artistName = artistName,
        songUrl = songUrl,
        albumName = albumName,
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl,
        favorite = favorite,
        localSongUrl = localSongUrl,
        localImageUrl = localImageUrl
    )
}