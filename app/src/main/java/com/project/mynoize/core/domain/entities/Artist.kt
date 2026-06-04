package com.project.mynoize.core.domain.entities

import com.project.mynoize.util.Country

data class Artist (
    var id: String = "",
    val name: String = "",
    val creator: String = "",
    val country: Country? = null,
    val imageLink: String = "",
    val imagePath: String = "",
    val favorite: Boolean = false,
)