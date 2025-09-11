package com.project.mynoize.activities.main.presentation.main_screen

import com.project.mynoize.core.data.Song


sealed interface MainScreenEvent {
    class OnSongClick(val song: Song): MainScreenEvent
    class SeekTo(val position: Long): MainScreenEvent
    object OnPrevSongClick: MainScreenEvent
    object OnNextSongClick: MainScreenEvent
    object OnPlayPauseToggleClick: MainScreenEvent
}