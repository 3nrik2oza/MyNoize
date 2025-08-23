package com.project.mynoize.activities.main

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppScreen

    @Serializable
    object MusicScreen : AppScreen

    @Serializable
    object FavoriteScreen : AppScreen

    @Serializable
    object ShowMusic : AppScreen

    @Serializable
    object ProfileScreen : AppScreen

    @Serializable
    object CreateArtistScreen : AppScreen

    @Serializable
    object CreateSongScreen : AppScreen
