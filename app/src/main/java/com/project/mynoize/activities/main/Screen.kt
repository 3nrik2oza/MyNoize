package com.project.mynoize.activities.main

import kotlinx.serialization.Serializable


interface AppScreen

    @Serializable
    object MusicScreen : AppScreen

    @Serializable
    object FavoriteScreen : AppScreen

    @Serializable
    object FavoriteScreenRoot : AppScreen

    @Serializable
    data class PlaylistView(val playlistId: String) : AppScreen

    @Serializable
    data class SelectSongsScreen(val playlistId: String) : AppScreen
/*
    @Serializable
    object ShowMusic : AppScreen*/

    @Serializable
    object ProfileScreen : AppScreen

    @Serializable
    object CreateArtistScreen : AppScreen

    @Serializable
    object CreateSongScreen : AppScreen

    @Serializable
    data class CreatePlaylistScreen(val playlistId: String): AppScreen