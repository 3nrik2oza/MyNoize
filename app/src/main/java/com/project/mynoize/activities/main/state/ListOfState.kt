package com.project.mynoize.activities.main.state

import com.project.mynoize.core.presentation.UiText

data class ListOfState<T>(
    val list: List<T> = emptyList(),
    val selected: T? = null,
    val listError: UiText? = null
){
   // fun selectedElement(): T = list[index]
}