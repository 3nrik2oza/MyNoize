package com.project.mynoize.core.domain

sealed interface InputError: Error {
    enum class CreateSong : InputError{
        ENTER_SONG_NAME,
        SELECT_ARTIST,
        SELECT_ALBUM,
        SELECT_SONG_FILE
    }

    enum class CreateAlbum: InputError{
        SELECT_IMAGE,
        ENTER_ALBUM_NAME
    }

    enum class SingIn: InputError{
        ENTER_EMAIL,
        INCORRECT_EMAIL,
        ENTER_PASSWORD,
    }

    enum class SignUp: InputError{
        ENTER_USERNAME,
        USERNAME_TOO_LONG,
        ENTER_EMAIL,
        INCORRECT_EMAIL,
        ENTER_PASSWORD,
        PASSWORD_TOO_SHORT,
        PASSWORD_TOO_LONG,
        PASSWORD_NO_NUMBERS,
        PASSWORD_NO_LETTERS,
        ENTER_REPEATED_PASSWORD,
        PASSWORDS_DO_NOT_MATCH
    }
}