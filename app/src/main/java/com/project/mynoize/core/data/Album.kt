package com.project.mynoize.core.data

data class Album (
    var id: String = "",
    val name: String = "",
    val nameLower: String = "",
    val image: String = "",
    val creator: String = "",
    val artist: String = "",
    val favorite: Boolean = false,
    val songs: List<String> = listOf()
)