package com.project.mynoize.core.data.firestore.entities

data class ArtistDto (
    var id: String,
    val name: String,
    val nameLower: String,
    val country: String,
    val creator: String,
    val imageLink: String,
    val imagePath: String,
)