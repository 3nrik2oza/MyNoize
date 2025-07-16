package com.project.mynoize.managers

// ExoPlayerManager.kt

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerManager(private val context: Context) {
    private var exoPlayer: ExoPlayer? = null

    fun initializePlayer(url: String) {
        releasePlayer()
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun playPauseToggle() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
