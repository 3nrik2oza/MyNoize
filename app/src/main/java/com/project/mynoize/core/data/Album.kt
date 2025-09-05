package com.project.mynoize.core.data

class Album (
    var id: String = "",
    val name: String = "",
    val image: String = "",
    val creator: String = "",
    val artist: String = "",
    val songs: List<String> = listOf()
)