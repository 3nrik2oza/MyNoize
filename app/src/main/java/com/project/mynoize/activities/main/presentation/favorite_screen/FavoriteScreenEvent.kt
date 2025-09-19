package com.project.mynoize.activities.main.presentation.favorite_screen


sealed class FavoriteScreenEvent {

    class OnPlaylistClicked(val playlistId: String) : FavoriteScreenEvent()

}