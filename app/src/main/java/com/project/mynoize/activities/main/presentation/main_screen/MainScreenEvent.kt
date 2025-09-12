package com.project.mynoize.activities.main.presentation.main_screen



sealed interface MainScreenEvent {
    class OnSongClick(val position: Int): MainScreenEvent
    class SeekTo(val position: Long): MainScreenEvent
    object OnPrevSongClick: MainScreenEvent
    object OnNextSongClick: MainScreenEvent
    object OnPlayPauseToggleClick: MainScreenEvent
}