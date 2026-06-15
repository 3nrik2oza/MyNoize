package com.project.mynoize.activities.main.presentation.create_song

import android.content.Context
import com.project.mynoize.core.domain.entities.Album
import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.util.Era
import com.project.mynoize.util.Genre
import com.project.mynoize.util.Language
import com.project.mynoize.util.Mood
import com.project.mynoize.util.SubGenre

sealed interface CreateSongEvent {
    data class OnSongNameChange(val songName: String): CreateSongEvent
    data class OnArtistClick(val artist: Artist): CreateSongEvent
    data class OnAlbumClick(val album: Album): CreateSongEvent
    data class OnSelectSongClick(val context: Context, val songUri: String): CreateSongEvent

    data class OnGenreClick(val selected: Genre): CreateSongEvent
    data class OnSubgenreClick(val selected: SubGenre): CreateSongEvent
    data class OnLanguageClick(val selected: Language): CreateSongEvent
    data class OnEraClick(val selected: Era): CreateSongEvent
    data class OnMoodClick(val selected: Mood): CreateSongEvent

    object OnAddAlbumClick: CreateSongEvent
    object OnAddSongClick: CreateSongEvent
    object OnDismissAlertDialog: CreateSongEvent
    object OnBackClick: CreateSongEvent
}