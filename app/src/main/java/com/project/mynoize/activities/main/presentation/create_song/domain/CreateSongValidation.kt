package com.project.mynoize.activities.main.presentation.create_song.domain

import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onError

class CreateSongValidation {

    fun execute(songName: String, artistIndex: Int, albumIndex: Int, uri: String): EmptyResult<InputError.CreateSong>{
        validateSongName(songName = songName).onError {
            return Result.Error(it)
        }

        validateArtistSelected(index = artistIndex).onError {
            return Result.Error(it)
        }

        validateAlbumSelected(index = albumIndex).onError {
            return Result.Error(it)
        }

        validateSongUri(uri = uri).onError {
            return Result.Error(it)
        }


        return Result.Success(Unit)
    }

    private fun validateSongName(songName: String): EmptyResult<InputError.CreateSong>{
        val result = if(songName.isEmpty()){
            Result.Error(InputError.CreateSong.ENTER_SONG_NAME)
        } else {
            Result.Success(Unit)
        }
        return result
    }

    private fun validateArtistSelected(index: Int): EmptyResult<InputError.CreateSong>{
        return if(index == -1){
            Result.Error(InputError.CreateSong.SELECT_ARTIST)
        }else{
            Result.Success(Unit)
        }
    }

    private fun validateAlbumSelected(index: Int): EmptyResult<InputError.CreateSong>{
        return if(index == -1){
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

}