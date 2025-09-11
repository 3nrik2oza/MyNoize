package com.project.mynoize.activities.main.presentation.main_screen

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.mynoize.core.data.Song
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.util.Constants
import com.project.mynoize.util.UserInformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel (
    val playerManager: ExoPlayerManager,
    application: Application) : AndroidViewModel(application){


    //var songList = mutableStateOf(listOf<Song>())

   // val currentSong: StateFlow<Song?> = playerManager.currentSong

  //  val dataStore = UserInformation(application)


    val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()



    private val _uiEvent = MutableSharedFlow<MainActivityUiEvent>()
    val uiEvent = _uiEvent

    init{


        viewModelScope.launch {
        //    lastId = dataStore.mediaId.first().toString()
        //    lastPosition = dataStore.position.first()?.toLong() ?: 0L

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

        var list = mutableListOf<Song>()
            Firebase.firestore.collection(Constants.SONG_COLLECTION).get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        val song = document.toObject(Song::class.java)
                        song.position = list.size
                        song.mediaId = document.id

                        if(song.position == 2){
                            playerManager.initializePlayer(song, play = false)
                          //  playerManager.seekTo(lastPosition)
                        }

                        list += song
                    }
                    _state.update { it.copy(songList = list) }

                    playerManager.setSongList(list)
                }

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
            is MainScreenEvent.OnSongClick -> onSongClick(event.song)
            is MainScreenEvent.SeekTo -> playerManager.seekTo(event.position)
        }
    }

    suspend fun savePosition(){
        while (true){
        //    dataStore.updatePosition(playerManager.getPosition())
            delay(1000)
        }

    }


    fun onSongClick(song: Song){
        playerManager.playSong(song)

        viewModelScope.launch {
        //    dataStore.updateMediaId(song.mediaId)
        }

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