package com.project.mynoize.activities.main.presentation.create_song

import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.util.Era
import com.project.mynoize.util.Genre
import com.project.mynoize.util.Language
import com.project.mynoize.util.Mood
import com.project.mynoize.util.SubGenre

data class CreateSongState (
    val songName: String = "",
    val songNameError: UiText? = null,
    val songUri: String = "",
    val songUriError: UiText? = null,
    val songTitle: String = "Select Song",
    val songGenre: Genre? = null,
    val songGenreError: UiText? = null,
    val songSubgenre: SubGenre? = null,
    val songSubgenreError: UiText? = null,
    val language: Language? = null,
    val languageError: UiText? = null,
    val era: Era? = null,
    val eraError: UiText? = null,
    val songsMoods: List<Mood>? = null,
    val showCreateAlbum: Boolean = false,
)