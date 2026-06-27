package com.project.mynoize.managers

// ExoPlayerManager.kt

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.project.mynoize.core.data.toMediaItem
import com.project.mynoize.core.domain.entities.Song
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.data_collecting.data.model.ListeningEvent
import com.project.mynoize.data_collecting.data.model.SourceMetadata
import com.project.mynoize.data_collecting.data.model.SourceType
import com.project.mynoize.data_collecting.data.repository.ListeningEventRepository
import com.project.mynoize.data_collecting.util.SessionId
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
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class ExoPlayerManager(
    private val context: Context,
    private val dataStore: UserInformation,
    private val listeningEventRepo: ListeningEventRepository,
) {
    private var exoPlayer: ExoPlayer? = null

    private var songList = mutableStateOf(listOf<Song>())

    private val _currentSong = MutableStateFlow<MediaMetadata?>(null)
    val currentSong: StateFlow<MediaMetadata?> get() = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _listeningEvent = MutableStateFlow<ListeningEvent?>(null)

    private val _sourceMetadata = MutableStateFlow(SourceMetadata(SourceType.SEARCH, ""))

    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    fun initializePlayer(
        songs: List<Song> = listOf(), playWhenReady: Boolean = true, shuffle: Boolean = false,
        scope: CoroutineScope, index: Int = -1, playlistId: String, sourceType: SourceType,
    ) {

        if (exoPlayer != null) {
            val oldPositionMs = exoPlayer?.currentPosition ?: 0L

            songList.value = songs
            val mediaItem = songList.value.map { song -> song.toMediaItem() }
            exoPlayer?.setMediaItems(mediaItem)

            playerScope.launch {
                // Persist whatever was being tracked for the previous playlist before we
                // start tracking the new one. Previously this was just discarded.
                finalizeCurrentListeningEvent(oldPositionMs)
                // The previous fix used the playlist/source from whenever the player was
                // FIRST created. Update it so new events are attributed correctly.
                _sourceMetadata.value = SourceMetadata(sourceType, playlistId)

                exoPlayer?.shuffleModeEnabled = shuffle
                val startingSong = if (!shuffle) {
                    val song = if (index == -1) songs.find { song -> song.id == dataStore.mediaId.first().toString() } else songs.getOrNull(index)
                    _currentSong.update { song?.toMediaItem()?.mediaMetadata }
                    _currentPosition.value = if (index == -1) dataStore.position.first()?.toLong() ?: 0L else 0L
                    song?.let { exoPlayer?.seekTo(songs.indexOf(it), _currentPosition.value * 1000) }
                    song
                } else {
                    _currentSong.value = exoPlayer?.currentMediaItem?.mediaMetadata
                    songs.firstOrNull { it.id == exoPlayer?.currentMediaItem?.mediaMetadata?.description }
                }

                exoPlayer?.prepare()
                exoPlayer?.playWhenReady = playWhenReady
                _isPlaying.update { playWhenReady }
                dataStore.updatePlaylist(playlistId = playlistId)
                dataStore.updateSource(sourceType = sourceType)

                startingSong?.let { createNewListeningEvent(it) }
            }
            return
        }

        songList.value = songs
        _sourceMetadata.value = SourceMetadata(sourceType, playlistId)
        val mediaItems = songList.value.map { song -> song.toMediaItem() }

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItems(mediaItems)
        }

        playerScope.launch {
            exoPlayer?.shuffleModeEnabled = shuffle
            val startingSong = if (!shuffle) {
                val song = if (index == -1) songs.find { song -> song.id == dataStore.mediaId.first().toString() } else songs.getOrNull(index)
                _currentSong.update { song?.toMediaItem()?.mediaMetadata }
                _currentPosition.value = if (index == -1) dataStore.position.first()?.toLong() ?: 0L else 0L
                song?.let { exoPlayer?.seekTo(songs.indexOf(it), _currentPosition.value * 1000) }
                song
            } else {
                _currentSong.value = exoPlayer?.currentMediaItem?.mediaMetadata
                songs.firstOrNull { it.id == exoPlayer?.currentMediaItem?.mediaMetadata?.description }
            }

            exoPlayer?.prepare()
            exoPlayer?.playWhenReady = playWhenReady
            _isPlaying.update { playWhenReady }
            dataStore.updatePlaylist(playlistId = playlistId)

            // This used to never happen: nothing created a listening event for the
            // very first song played in a session.
            startingSong?.let { createNewListeningEvent(it) }
        }

        exoPlayer!!.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duration.value = exoPlayer!!.duration
                    playerScope.launch {
                        dataStore.updateMediaId(currentSong.value?.description.toString())

                        // The listening event for the current song is created right as it
                        // starts, before the player has resolved a real duration (it's 0 or
                        // carried over from the previous song at that point). Patch it in
                        // now that we know it, so saved events don't carry a stale duration.
                        _listeningEvent.value?.let { event ->
                            val correctDuration = (_duration.value / 1000).toInt()
                            if (event.duration != correctDuration) {
                                val updated = event.copy(duration = correctDuration)
                                _listeningEvent.value = updated
                                listeningEventRepo.updateListeningTime(updated)
                            }
                        }
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _currentSong.value = mediaItem?.mediaMetadata
            }

            @OptIn(UnstableApi::class)
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int,
            ) {
                // Only a real track change matters for listening-event tracking;
                // a seek within the same track shouldn't start a new event.
                if (oldPosition.mediaItemIndex == newPosition.mediaItemIndex) return

                playerScope.launch {
                    // Finalize+create run sequentially in the same coroutine, so there's
                    // no window where _listeningEvent is in an inconsistent state, and no
                    // dependency on the old event's songId matching anything - if state
                    // ever drifts, this transition self-heals it instead of getting stuck.
                    finalizeCurrentListeningEvent(oldPosition.positionMs)

                    val newSong = songs.firstOrNull { it.id == newPosition.mediaItem?.mediaMetadata?.description }
                    newSong?.let { createNewListeningEvent(it) }
                }
            }
        })

        playerScope.launch {
            var tick = 0
            while (isActive) {
                _currentPosition.value = exoPlayer?.currentPosition ?: 0L
                delay(500.milliseconds)
                if (_isPlaying.value) {
                    dataStore.updatePosition((exoPlayer?.currentPosition?.div(1000)) ?: 0L)

                    // Checkpoint the active listening event roughly every 10s while
                    // playing, so a crash or killed process doesn't lose all the
                    // listened time that would otherwise only get saved on the next
                    // transition or on a clean releasePlayer() call.
                    tick++
                    if (tick % 20 == 0) {
                        _listeningEvent.value?.let { event ->
                            listeningEventRepo.updateListeningTime(
                                event.copy(listenedSeconds = (exoPlayer?.currentPosition ?: 0L) / 1000f)
                            )
                        }
                    }
                }
            }
        }
    }


    fun playSong(position: Int) {
        exoPlayer?.seekTo(
            position, // int position of song in song list
            0 // position in song in ms
        )
    }

    fun nextSong() {
        if (exoPlayer?.hasNextMediaItem() ?: false) {
            exoPlayer?.seekToNextMediaItem()
        }
    }

    fun prevSong() {
        if (exoPlayer?.hasPreviousMediaItem() ?: false) {
            exoPlayer?.seekToPreviousMediaItem()
        }
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
        val finalPositionMs = exoPlayer?.currentPosition ?: 0L

        runBlocking {
            dataStore.updatePosition(finalPositionMs / 1000L)
            finalizeCurrentListeningEvent(finalPositionMs)
        }

        exoPlayer?.release()
        exoPlayer = null
    }

    private suspend fun createNewListeningEvent(currentSong: Song) {
        if (_listeningEvent.value != null) return

        val newEvent = ListeningEvent(
            id = UUID.randomUUID(),
            songId = currentSong.id,
            artistId = currentSong.artistId,
            albumId = currentSong.albumId,
            sessionId = SessionId.sessionId,
            sourceType = _sourceMetadata.value.type,
            sourceId = _sourceMetadata.value.id,
            listenedSeconds = 0f,
            duration = (duration.value / 1000).toInt(),
            userId = ""
        )

        // Publish synchronously, before the DB write. Previously this was only set
        // inside the save's onSuccess callback, so two calls arriving before the first
        // save finished would both see null and both create a duplicate event.
        _listeningEvent.value = newEvent

        listeningEventRepo.saveToLocalDatabase(newEvent).onSuccess {
            // already published above
        }
    }

    private suspend fun finalizeCurrentListeningEvent(positionMs: Long) {
        val event = _listeningEvent.value ?: return
        _listeningEvent.value = null

        if (positionMs > 5_000) {
            // Was previously: listenedSeconds = currentPos.toFloat() - saving raw
            // milliseconds as seconds, a 1000x error. Fixed below.
            listeningEventRepo.updateListeningTime(
                event.copy(listenedSeconds = positionMs / 1000f)
            )
        }
    }
}