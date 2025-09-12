package com.project.mynoize.core.presentation

import com.project.mynoize.R
import com.project.mynoize.core.domain.DataError


fun DataError.Remote.toErrorMessage() : UiText{
    val stringRes = when(this){
        DataError.Remote.SERVER -> R.string.error_server
        else -> R.string.error_server
    }
    return UiText.StringResource(stringRes)
}