package com.project.mynoize.activities.main.presentation.main_screen

import androidx.media3.common.MediaMetadata

data class PlayerState(
    val song: MediaMetadata?,
    val isPlaying: Boolean,
    val position: Long,
    val duration: Long
)
