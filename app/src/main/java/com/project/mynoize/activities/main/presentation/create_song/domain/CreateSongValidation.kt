package com.project.mynoize.activities.main.presentation.create_song.domain

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

    fun execute(songName: String, artist: Artist?, album: Album?, uri: String, genre: Genre?, subgenre: SubGenre?, language: Language?, era: Era?, mood :List<Mood>?): Result<Song, InputError.CreateSong>{
        validateSongName(songName = songName).onError {
            return Result.Error(it)
        }

        validateArtistSelected(artist).onError {
            return Result.Error(it)
        }

        validateAlbumSelected(album).onError {
            return Result.Error(it)
        }

        validateGenreSelected(genre, subgenre).onError {
            return Result.Error(it)
        }

        validateSongUri(uri = uri).onError {
            return Result.Error(it)
        }

        validateLanguage(language).onError {
            return Result.Error(it)
        }

        validateEra(era).onError {
            return Result.Error(it)
        }

        val song = Song(
            title = songName,
            artistId = artist!!.id,
            artistName = artist.name,
            genre = genre,
            subgenre = subgenre,
            mood = mood,
            language = language!!,
            era = era!!,
            albumName = album!!.name,
            albumId = album.id,
        )
        return Result.Success(song)
    }

    private fun validateSongName(songName: String): EmptyResult<InputError.CreateSong>{
        val result = if(songName.isEmpty()){
            Result.Error(InputError.CreateSong.ENTER_SONG_NAME)
        } else {
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