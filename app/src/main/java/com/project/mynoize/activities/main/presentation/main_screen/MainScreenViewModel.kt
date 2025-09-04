package com.project.mynoize.activities.main.presentation.main_screen

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.mynoize.core.data.Song
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.util.Constants
import com.project.mynoize.util.UserInformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainScreenViewModel (application: Application) : AndroidViewModel(application){

    var playerManager: ExoPlayerManager = ExoPlayerManager(getApplication())

    var songList = mutableStateOf(listOf<Song>())

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    val dataStore = UserInformation(application)

    var lastId = ""
    var lastPosition = 0L


    init{
        viewModelScope.launch {
            lastId = dataStore.mediaId.first().toString()
            lastPosition = dataStore.position.first()?.toLong() ?: 0L
        }

            Firebase.firestore.collection(Constants.SONG_COLLECTION).get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        val song = document.toObject(Song::class.java)
                        song.position = songList.value.size
                        song.mediaId = document.id

                        if(lastId == song.mediaId){
                            _currentSong.value = song
                            playerManager.initializePlayer(song.songUrl, play = false)
                            playerManager.seekTo(lastPosition)
                        }

                        songList.value += song
                    }
                }

        viewModelScope.launch {
            savePosition()
        }


    }

    suspend fun savePosition(){
        while (true){
            dataStore.updatePosition(playerManager.getPosition())
            delay(1000)
        }

    }

    fun onSongClick(song: Song){
        playerManager.initializePlayer(song.songUrl)
        _currentSong.value = song

        viewModelScope.launch {
            dataStore.updateMediaId(song.mediaId)
        }


    }

    fun nextSong(){
        val position = _currentSong.value?.position ?: 0
        val newSong = if(position < songList.value.size-1)  songList.value[position+1] else songList.value[0]
        playerManager.initializePlayer(newSong.songUrl)
        _currentSong.value = newSong

        viewModelScope.launch {
            dataStore.updateMediaId(newSong.mediaId)
        }
    }

    fun prevSong(){
        val position = _currentSong.value?.position ?: 0
        val newSong = if(position > 0)  songList.value[position-1] else songList.value[songList.value.size-1]
        playerManager.initializePlayer(newSong.songUrl)
        _currentSong.value = newSong

        viewModelScope.launch {
            dataStore.updateMediaId(newSong.mediaId)
        }
    }



}