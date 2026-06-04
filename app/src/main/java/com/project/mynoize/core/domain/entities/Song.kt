package com.project.mynoize.core.domain.entities

import com.project.mynoize.util.Era
import com.project.mynoize.util.Genre
import com.project.mynoize.util.Language
import com.project.mynoize.util.Mood
import com.project.mynoize.util.SubGenre

data class Song(
    var id: String = "",
    val title: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val genre: Genre? = null,
    val subgenre: SubGenre? = null,
    val mood: List<Mood>? = null,
    val language: Language? = null,
    val era: Era? = null,
    val audioPath: String = "",
    val songUrl: String = "",
    val localSongUrl: String? = null,
    val albumName: String = "",
    val albumId: String = "",
    val creatorId: String = "",
    val imageUrl: String = "",
    val localImageUrl: String? = null,
    val favorite: Boolean = false,
)
