package com.project.mynoize.core.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText{
    data class DynamicString(val value: String): UiText
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ): UiText
}

@Composable
fun UiText.asString(): String{
    return when(this){
        is UiText.DynamicString -> value
        is UiText.StringResource -> stringResource(id = id, formatArgs = args)
    }
}

@Composable
fun UiText.getId(): Int{
    return when(this){
        is UiText.DynamicString -> 0
        is UiText.StringResource -> id
    }
}