package com.project.mynoize.core.data

data class Playlist(
    val id: String = "",
    val name: String = "",
    val creator: String = "",
    val image: String = "",
    val songs: List<String> = listOf(),
    val artists: List<String> = listOf()
)