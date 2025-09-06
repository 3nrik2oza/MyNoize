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

fun InputError.SingIn.toErrorMessage(): UiText{
    val stringRes = when(this){
        InputError.SingIn.ENTER_EMAIL -> R.string.error_enter_email
        InputError.SingIn.INCORRECT_EMAIL -> R.string.error_incorrect_email
        InputError.SingIn.ENTER_PASSWORD -> R.string.error_enter_password
    }
    return UiText.StringResource(stringRes)
}

fun InputError.SignUp.toErrorMessage(): UiText{
    val stringRes = when(this){
        InputError.SignUp.ENTER_USERNAME -> R.string.error_enter_username
        InputError.SignUp.USERNAME_TOO_LONG -> R.string.error_username_too_long
        InputError.SignUp.ENTER_EMAIL -> R.string.error_enter_email
        InputError.SignUp.INCORRECT_EMAIL -> R.string.error_incorrect_email
        InputError.SignUp.ENTER_PASSWORD -> R.string.error_enter_password
        InputError.SignUp.PASSWORD_TOO_SHORT -> R.string.error_password_too_short
        InputError.SignUp.PASSWORD_TOO_LONG -> R.string.error_password_too_long
        InputError.SignUp.PASSWORD_NO_NUMBERS -> R.string.error_password_no_numbers
        InputError.SignUp.PASSWORD_NO_LETTERS -> R.string.error_password_no_letters
        InputError.SignUp.ENTER_REPEATED_PASSWORD -> R.string.error_enter_repeated_password
        InputError.SignUp.PASSWORDS_DO_NOT_MATCH -> R.string.error_passwords_do_not_match
    }
    return UiText.StringResource(stringRes)
}