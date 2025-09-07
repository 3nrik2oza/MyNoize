package com.project.mynoize.activities.main.presentation.create_artist.domain

import android.net.Uri
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onError

class CreateArtistValidation {

    fun execute(artistName: String, artistImage: Uri?): EmptyResult<InputError.CreateArtist>{

        validateArtistImage(artistImage).onError {
            return Result.Error(it)
        }

        validateArtistName(artistName).onError {
            return Result.Error(it)
        }

        return Result.Success(Unit)
    }

    private fun validateArtistImage(artistImage: Uri?): EmptyResult<InputError.CreateArtist>{
        return if(artistImage == null){
            Result.Error(InputError.CreateArtist.SELECT_ARTIST_IMAGE)
        }else{
            Result.Success(Unit)
        }
    }

    private fun validateArtistName(artistName: String): EmptyResult<InputError.CreateArtist>{
        return if(artistName.isEmpty()){
            Result.Error(InputError.CreateArtist.ENTER_ARTIST_NAME)
        } else if(artistName.length > 30) {
            Result.Error(InputError.CreateArtist.ARTIST_NAME_TOO_LONG)
        }
        else {
            Result.Success(Unit)
        }
    }


}