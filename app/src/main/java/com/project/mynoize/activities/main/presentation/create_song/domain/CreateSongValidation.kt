package com.project.mynoize.activities.main.presentation.create_song.domain

import com.project.mynoize.activities.main.presentation.create_song.CreateSongState
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.entities.Album
import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.domain.entities.Song
import com.project.mynoize.core.domain.onError
import com.project.mynoize.util.Era
import com.project.mynoize.util.Genre
import com.project.mynoize.util.Language
import com.project.mynoize.util.Mood
import com.project.mynoize.util.SubGenre

class CreateSongValidation {

    fun execute(state: CreateSongState): Result<Song, InputError.CreateSong>{
        validateSongName(songName = state.songName).onError {
            return Result.Error(it)
        }

        validateArtistSelected(state.selectedArtist).onError {
            return Result.Error(it)
        }

        validateAlbumSelected(state.selectedAlbum).onError {
            return Result.Error(it)
        }

        validateGenreSelected(state.songGenre, state.songSubgenre).onError {
            return Result.Error(it)
        }

        validateSongUri(uri = state.songUri).onError {
            return Result.Error(it)
        }

        validateLanguage(state.language).onError {
            return Result.Error(it)
        }

        validateEra(state.era).onError {
            return Result.Error(it)
        }

        val song = Song(
            title = state.songName,
            artistId = state.selectedArtist!!.id,
            artistName = state.selectedArtist.name,
            genre = state.songGenre,
            subgenre = state.songSubgenre,
            mood = state.moods,
            language = state.language,
            era = state.era,
            albumName = state.selectedAlbum!!.name,
            albumId = state.selectedAlbum.id,
            imageUrl = state.selectedAlbum.imageLink
        )
        return Result.Success(song)
    }

    private fun validateSongName(songName: String): EmptyResult<InputError.CreateSong>{
        val result = if(songName.isEmpty()){
            Result.Error(InputError.CreateSong.ENTER_SONG_NAME)
        } else if(songName.length > 30){
            Result.Error(InputError.CreateSong.SONG_NAME_TOO_LONG)
        } else{
            Result.Success(Unit)
        }
        return result
    }

    private fun validateArtistSelected(artist: Artist?): EmptyResult<InputError.CreateSong>{
        return if(artist == null){
            Result.Error(InputError.CreateSong.SELECT_ARTIST)
        }else{
            Result.Success(Unit)
        }
    }

    private fun validateGenreSelected(genre: Genre?, subgenre: SubGenre?): EmptyResult<InputError.CreateSong>{
        return if(genre == null){
            Result.Error(InputError.CreateSong.SELECT_GENRE)
        }else if(subgenre == null){
            Result.Error(InputError.CreateSong.SELECT_SUBGENRE)
        }else{
            Result.Success(Unit)
        }
    }

    private fun validateAlbumSelected(album: Album?): EmptyResult<InputError.CreateSong>{
        return if(album == null){
            Result.Error(InputError.CreateSong.SELECT_ALBUM)
        }else{
            Result.Success(Unit)
        }
    }

    private fun validateSongUri(uri: String): EmptyResult<InputError.CreateSong>{
        return if(uri.isEmpty()){
            Result.Error(InputError.CreateSong.SELECT_SONG_FILE)
        }else{
            Result.Success(Unit)
        }
    }

    private fun validateLanguage(language: Language?): EmptyResult<InputError.CreateSong>{
        return if(language == null){
            Result.Error(InputError.CreateSong.SELECT_LANGUAGE)
        }else{
            Result.Success(Unit)
        }
    }

    private fun validateEra(era: Era?): EmptyResult<InputError.CreateSong>{
        return if(era == null){
            Result.Error(InputError.CreateSong.SELECT_ERA)
        }else{
            Result.Success(Unit)
        }
    }

}