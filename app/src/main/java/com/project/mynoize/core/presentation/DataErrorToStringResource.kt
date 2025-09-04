package com.project.mynoize.core.presentation

import com.project.mynoize.R
import com.project.mynoize.core.domain.DataError

/*
fun InputError.toErrorMessage() : UiText{
    val stringRes = when(this){
        InputError.CreateSong.ENTER_SONG_NAME -> R.string.error_song_name
        InputError.CreateSong.SELECT_ARTIST -> R.string.error_select_artist
        InputError.CreateSong.SELECT_ALBUM -> R.string.error_select_album
        InputError.CreateSong.SELECT_SONG_FILE -> R.string.error_select_song
    }
    return UiText.StringResource(stringRes)
}
*/

fun DataError.Remote.toErrorMessage() : UiText{
    val stringRes = when(this){
        DataError.Remote.SERVER -> R.string.error_server
        else -> R.string.error_server
    }
    return UiText.StringResource(stringRes)
}