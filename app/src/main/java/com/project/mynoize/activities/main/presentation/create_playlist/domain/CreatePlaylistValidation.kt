package com.project.mynoize.activities.main.presentation.create_playlist.domain

import android.net.Uri
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onError

class CreatePlaylistValidation {

    fun execute(playlistName: String, playlistImage: Uri?): EmptyResult<InputError.CreatePlaylist>{

        validatePlaylistName(playlistName).onError {
            return Result.Error(it)
        }

        validatePlaylistImage(playlistImage).onError {
            return Result.Error(it)
        }

        return Result.Success(Unit)
    }

    private fun validatePlaylistName(name: String): EmptyResult<InputError.CreatePlaylist>{
        if(name.isEmpty()){
            return Result.Error(InputError.CreatePlaylist.ENTER_PLAYLIST_NAME)
        }else if(name.length > 30){
            return Result.Error(InputError.CreatePlaylist.PLAYLIST_NAME_TOO_LONG)
        }
        return Result.Success(Unit)
    }

    private fun validatePlaylistImage(image: Uri?): EmptyResult<InputError.CreatePlaylist>{
        if(image == null){
            return Result.Error(InputError.CreatePlaylist.SELECT_PLAYLIST_IMAGE)
        }
        return Result.Success(Unit)

    }

}