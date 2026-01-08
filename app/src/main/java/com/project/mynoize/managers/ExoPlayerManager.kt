package com.project.mynoize.managers

// ExoPlayerManager.kt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.toMediaItem
import com.project.mynoize.util.UserInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ExoPlayerManager(
    private val context: Context,
    private val dataStore: UserInformation
) {
    private var exoPlayer: ExoPlayer? = null

    private var songList = mutableStateOf(listOf<Song>())

    private val _currentSong = MutableStateFlow<MediaMetadata?>(null)
    val currentSong: StateFlow<MediaMetadata?> get() = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition= MutableStateFlow(exoPlayer?.currentPosition ?: 0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()


    private val _duration= MutableStateFlow(exoPlayer?.currentPosition ?: 0L)
    val duration: StateFlow<Long> = _duration


    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    fun initializePlayer(
        songs: List<Song> = listOf(), play: Boolean = true, shuffle: Boolean = false,
        scope: CoroutineScope, index: Int = -1, playlistId: String) {

        if(exoPlayer != null){
            //exoPlayer?.release()
            songList.value = songs
            val mediaItem = songList.value.map { song -> song.toMediaItem() }
            exoPlayer?.setMediaItems(mediaItem)
            playerScope.launch {
                exoPlayer?.shuffleModeEnabled = shuffle
                if(!shuffle){
                    val song = if(index == -1) songs.find{ song -> song.id == dataStore.mediaId.first().toString() } else songs[index]
                    _currentSong.update { song?.toMediaItem()?.mediaMetadata  }
                    _currentPosition.value = if(index == -1) dataStore.position.first()?.toLong() ?: 0L else 0L
                    exoPlayer?.seekTo(songs.indexOf(song),_currentPosition.value*1000)
                }else{
                    _currentSong.value =  exoPlayer?.currentMediaItem?.mediaMetadata
                }

                exoPlayer?.prepare()
                exoPlayer?.playWhenReady = play
                _isPlaying.update { play }
                dataStore.updatePlaylist(playlistId = playlistId)
            }
            return
        }

        songList.value = songs
        val mediaItems = songList.value.map { song -> song.toMediaItem() }

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItems(mediaItems)
        }

        playerScope.launch {
            exoPlayer?.shuffleModeEnabled = shuffle
            if(!shuffle){
                val song = if(index == -1) songs.find{ song -> song.id == dataStore.mediaId.first().toString() } else songs[index]
                _currentSong.update { song?.toMediaItem()?.mediaMetadata  }
                _currentPosition.value = if(index == -1) dataStore.position.first()?.toLong() ?: 0L else 0L
                exoPlayer?.seekTo(songs.indexOf(song),_currentPosition.value*1000)
            }else{
                _currentSong.value = exoPlayer?.currentMediaItem?.mediaMetadata
            }

            exoPlayer?.prepare()
            exoPlayer?.playWhenReady = play
            _isPlaying.update { play }
            dataStore.updatePlaylist(playlistId = playlistId)

        }


        exoPlayer!!.addListener(object : Player.Listener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }


            override fun onPlaybackStateChanged(playbackState: Int) {
                if(playbackState == Player.STATE_READY){
                    _duration.value = exoPlayer!!.duration
                    playerScope.launch {
                        dataStore.updateMediaId(currentSong.value?.description.toString())
                    }

                }
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _currentSong.value = mediaItem?.mediaMetadata
            }
        })

        playerScope.launch {
            while(isActive){
                _currentPosition.value = exoPlayer?.currentPosition ?: 0L
                delay(500)
                if(_isPlaying.value){
                    dataStore.updatePosition((exoPlayer?.currentPosition?.div(1000)) ?: 0L)
                }

            }
        }
    }


    fun playSong(position: Int){
        exoPlayer?.seekTo(position, 0)
    }

    fun nextSong(){
        exoPlayer?.seekToNextMediaItem()

    }

    fun prevSong(){
        exoPlayer?.seekToPreviousMediaItem()
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
        runBlocking {
            dataStore.updatePosition((exoPlayer?.currentPosition?.div(1000)) ?: 0L)
        }

        exoPlayer?.release()
        exoPlayer = null
    }

}
