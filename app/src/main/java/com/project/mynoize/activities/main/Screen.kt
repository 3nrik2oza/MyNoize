package com.project.mynoize.activities.main

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppScreen

    @Serializable
    object MusicScreen : AppScreen

    @Serializable
    object FavoriteScreen : AppScreen

    @Serializable
    object CreateScreen : AppScreen

    @Serializable
    object ProfileScreen : AppScreen
