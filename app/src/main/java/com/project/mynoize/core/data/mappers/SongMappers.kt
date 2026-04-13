package com.project.mynoize.core.data.mappers


import com.project.mynoize.core.data.database.LocalSongsEntity
import com.project.mynoize.core.data.firestore.entities.RemoteSong
import com.project.mynoize.core.domain.entities.Song

fun LocalSongsEntity.toSong(): Song {
    return Song(
        id = id,
        title = title,
        artistId = artistId,
        artistName = artistName,
        genre = genre,
        subgenre = subGenre,
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
        genre = genre,
        subGenre = subgenre,
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl,
        favorite = favorite,
        localSongUrl = localSongUrl,
        localImageUrl = localImageUrl
    )
}

fun Song.toFirebaseSong(): RemoteSong{
    return RemoteSong(
        id = id,
        title = title,
        titleLower = title.lowercase(),
        artistId = artistId,
        artistName = artistName,
        genre = genre,
        subGenre = subgenre,
        songUrl = songUrl,
        albumName = albumName,
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl
    )
}

fun RemoteSong.toSong(): Song{
    return Song(
        id = id,
        title = title,
        artistId = artistId,
        artistName = artistName,
        genre = genre,
        subgenre = subGenre,
        songUrl = songUrl,
        albumName = albumName,
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl,
    )
}