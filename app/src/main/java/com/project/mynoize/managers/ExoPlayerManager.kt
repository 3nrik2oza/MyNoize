package com.project.mynoize.managers

// ExoPlayerManager.kt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.project.mynoize.core.data.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ExoPlayerManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    private var exoPlayer: ExoPlayer? = null

    private var songList = mutableStateOf(listOf<Song>())

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> get() = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition= MutableStateFlow(exoPlayer?.currentPosition ?: 0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()


    private val _duration= MutableStateFlow(exoPlayer?.currentPosition ?: 0L)
    val duration: StateFlow<Long> = _duration


    fun initializePlayer(currentSong: Song, play: Boolean = true) {
        if(exoPlayer != null){
            return
        }
        releasePlayer()
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(currentSong.songUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = play
        }
        attachPlayerListener()
        _currentSong.update { currentSong }
        _isPlaying.update { play }

        exoPlayer!!.addListener(object : Player.Listener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if(playbackState == Player.STATE_READY){
                    _duration.value = exoPlayer!!.duration
                }
            }
        })

        scope.launch {
            while(isActive){
                _currentPosition.value = exoPlayer?.currentPosition ?: 0L
                delay(500)
            }
        }

    }



    fun setSongList(list: List<Song>){
        songList.value = list
    }


    fun playSong(song: Song){
        val mediaItem = MediaItem.fromUri(song.songUrl)
        exoPlayer.let {
            if(it != null){
                it.setMediaItem(mediaItem)
                it.prepare()
                it.play()
                _currentSong.update { song }
            }
        }
    }

    fun nextSong(): Song{
        val position = _currentSong.value?.position ?: 0
        val newSong = if(position < songList.value.size-1)  songList.value[position+1] else songList.value[0]
        playSong(newSong)

        return newSong
    }

    fun prevSong(): Song{
        val position = _currentSong.value?.position ?: 0
        val newSong = if(position > 0)  songList.value[position-1] else songList.value[songList.value.size-1]
        playSong(newSong)

        return newSong
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun playPauseToggle() {
        exoPlayer?.let { exoPlayer ->
            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
            _isPlaying.update { exoPlayer.isPlaying }
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
    private fun attachPlayerListener() {
        exoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.update { isPlaying }
            }
        })
    }
}
