package com.project.mynoize.activities.main.presentation.main_screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.util.UserInformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
class MainScreenViewModel (
    val playerManager: ExoPlayerManager,
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val dataStore: UserInformation,
    application: Application) : AndroidViewModel(application) {



    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()



    private val _uiEvent = MutableSharedFlow<MainActivityUiEvent>()
    val uiEvent = _uiEvent

    init{


        viewModelScope.launch {

            combine(
                playerManager.currentSong,
                playerManager.isPlaying,
                playerManager.currentPosition,
                playerManager.duration
            ){song, isPlaying, currentPosition, duration ->
                MainScreenState(
                    currentSong = song,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    songList = _state.value.songList
                )
            }.collect { newState ->
                _state.update { newState }
            }

        }

        viewModelScope.launch {
            val playlist = playlistRepository.list.first().find{ it.id == dataStore.playlistId.first().toString()} ?: return@launch

            songRepository.getSongByIds(playlist.songs).onSuccess { songs ->
                _state.update {
                    it.copy(
                        songList = songs
                    )
                }
                playerManager.initializePlayer(songs = songs, play = false, scope = viewModelScope, playlistId = playlist.id)
            }

        }

        /*
        var list = mutableListOf<Song>()
            Firebase.firestore.collection(Constants.SONG_COLLECTION).get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        val song = document.toObject(Song::class.java)
                        song.position = list.size
                        song.id = document.id


                        list += song
                    }
                    _state.update { it.copy(songList = list) }

                    playerManager.initializePlayer(songs = list, play = false, scope = viewModelScope)
                }*/

        viewModelScope.launch {
            savePosition()
        }
    }

    fun onEventUi(event: MainActivityUiEvent)
    {
        when (event) {
            is MainActivityUiEvent.ShowNotification -> {
                viewModelScope.launch {
                    _uiEvent.emit(MainActivityUiEvent.ShowNotification)
                }
            }
            else -> Unit
        }
    }

    fun onEvent(event: MainScreenEvent){
        when(event){
            is MainScreenEvent.OnNextSongClick -> nextSong()
            is MainScreenEvent.OnPrevSongClick -> prevSong()
            is MainScreenEvent.OnPlayPauseToggleClick -> playPauseToggle()
            is MainScreenEvent.OnSongClick -> onSongClick(event.position)
            is MainScreenEvent.SeekTo -> playerManager.seekTo(event.position)
        }
    }

    suspend fun savePosition(){
        while (true){
            delay(1000)
        }

    }


    fun onSongClick(position: Int){
        playerManager.playSong(position)


        onEventUi(MainActivityUiEvent.ShowNotification)


    }

    fun playPauseToggle(){
        playerManager.playPauseToggle()
        onEventUi(MainActivityUiEvent.ShowNotification)
    }

    fun nextSong(){

        playerManager.nextSong()

        viewModelScope.launch {
         //   dataStore.updateMediaId(currentSong.value!!.mediaId)
        }

        onEventUi(MainActivityUiEvent.ShowNotification)
    }

    fun prevSong(){
        playerManager.prevSong()

        viewModelScope.launch {
         //   dataStore.updateMediaId(currentSong.value!!.mediaId)
        }
        onEventUi(MainActivityUiEvent.ShowNotification)
    }



}