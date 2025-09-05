package com.project.mynoize.core.presentation


import com.project.mynoize.R
import com.project.mynoize.core.domain.InputError

fun InputError.CreateSong.toErrorMessage() : UiText{
    val stringRes = when(this){
        InputError.CreateSong.ENTER_SONG_NAME -> R.string.error_song_name
        InputError.CreateSong.SELECT_ARTIST -> R.string.error_select_artist
        InputError.CreateSong.SELECT_ALBUM -> R.string.error_select_album
        InputError.CreateSong.SELECT_SONG_FILE -> R.string.error_select_song
    }
    return UiText.StringResource(stringRes)
}

fun InputError.CreateAlbum.toErrorMessage(): UiText{
    val stringRes = when(this){
        InputError.CreateAlbum.ENTER_ALBUM_NAME -> R.string.error_enter_album_name
        InputError.CreateAlbum.SELECT_IMAGE -> R.string.error_select_image
    }
    return UiText.StringResource(stringRes)
}