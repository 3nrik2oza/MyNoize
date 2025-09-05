package com.project.mynoize.activities.main.state

import com.project.mynoize.core.presentation.UiText

data class ListOfState<T>(
    val list: List<T> = emptyList(),
    val index: Int = -1,
    val listError: UiText? = null
){
    fun selectedElement(): T = list[index]
}