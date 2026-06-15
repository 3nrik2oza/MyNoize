package com.project.mynoize.activities.main.presentation.create_song

import com.project.mynoize.activities.main.state.ListOfState
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.entities.Album
import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.util.Era
import com.project.mynoize.util.Genre
import com.project.mynoize.util.Language
import com.project.mynoize.util.Mood
import com.project.mynoize.util.SubGenre

data class CreateSongState (
    val songName: String = "",
    val songUri: String = "",
    val songTitle: String = "Select Song",
    val songGenre: Genre? = null,
    val songSubgenre: SubGenre? = null,
    val language: Language? = null,
    val error: InputError.CreateSong? = null,
    val era: Era? = null,
    val moods: List<Mood> = emptyList(),
    val showCreateAlbum: Boolean = false,
    val artistList: List<Artist> = emptyList(),
    val selectedArtist: Artist? = null,
    val albumList: List<Album> = emptyList(),
    val selectedAlbum: Album? = null,
){
    val nameError: Boolean
        get() = when(error){
            InputError.CreateSong.ENTER_SONG_NAME -> true
            InputError.CreateSong.SONG_NAME_TOO_LONG -> true
            else -> false
        }
}