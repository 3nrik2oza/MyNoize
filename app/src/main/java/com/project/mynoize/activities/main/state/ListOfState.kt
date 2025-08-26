package com.project.mynoize.activities.main.state

data class ListSelectionState<T>(
    val list: List<T> = emptyList(),
    val index: Int = -1
){
    fun selectedElement(): T = list[index]
}