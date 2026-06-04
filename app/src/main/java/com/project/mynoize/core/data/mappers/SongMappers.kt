package com.project.mynoize.core.data.mappers


import com.project.mynoize.core.data.database.LocalSongsEntity
import com.project.mynoize.core.data.firestore.entities.SongDto
import com.project.mynoize.core.domain.entities.Song
import com.project.mynoize.util.Era
import com.project.mynoize.util.Genre
import com.project.mynoize.util.Language
import com.project.mynoize.util.Mood
import com.project.mynoize.util.SubGenre

fun LocalSongsEntity.toSong(): Song {
    return Song(
        id = id,
        title = title,
        artistId = artistId,
        artistName = artistName,
        genre = if(genre.isEmpty()) null else Genre.fromDisplayName(genre),
        subgenre = if(subGenre.isEmpty()) null else SubGenre.fromDisplayName(subGenre),
        mood = null,
        language = null,
        era = null,
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
        genre = genre?.displayName ?: "",
        subGenre = subgenre?.displayName ?: "",
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl,
        favorite = favorite,
        localSongUrl = localSongUrl!!,
        localImageUrl = localImageUrl!!
    )
}

fun Song.toFirebaseSong(): SongDto{
    return SongDto(
        id = id,
        title = title,
        titleLower = title.lowercase(),
        artistId = artistId,
        artistName = artistName,
        genre = genre?.displayName ?: "",
        subGenre = subgenre?.displayName ?: "",
        mood = mood?.map { it.displayName } ?: emptyList(),
        language = language!!.displayName,
        era = era!!.displayName,
        songUrl = songUrl,
        audioPath = audioPath,
        albumName = albumName,
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl
    )
}

fun SongDto.toSong(): Song{
    return Song(
        id = id,
        title = title,
        artistId = artistId,
        artistName = artistName,
        genre = if(genre.isEmpty()) null else Genre.fromDisplayName(genre),
        subgenre = if(subGenre.isEmpty()) null else SubGenre.fromDisplayName(subGenre),
        mood = mood.map { Mood.fromDisplayName(it)!! },
        language = Language.fromDisplayName(language),
        era = Era.fromDisplayName(era),
        songUrl = songUrl,
        audioPath = audioPath,
        albumName = albumName,
        albumId = albumId,
        creatorId = creatorId,
        imageUrl = imageUrl,
    )
}