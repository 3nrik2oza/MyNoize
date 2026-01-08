package com.project.mynoize.core.data

data class Artist (
    var id: String = "",
    val name: String = "",
    val creator: String = "",
    val imageLink: String = "",
    val imagePath: String = "",
    val songs: List<String> = listOf()
)