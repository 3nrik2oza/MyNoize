package com.project.mynoize.activities.main

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.mynoize.data.Song
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.util.UserInformation
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



    init{
        viewModelScope.launch {
            lastId = dataStore.mediaId.first().toString()
        }

            Firebase.firestore.collection("songs").get()
                .addOnSuccessListener { result ->
                    val updatedList = songList.value.toMutableList()
                    for(document in result){
                        val song = document.toObject(Song::class.java)
                        song.position = updatedList.size
                        song.mediaId = document.id

                        if(lastId == song.mediaId){
                            _currentSong.value = song
                            playerManager.initializePlayer(song.songUrl, play = false)
                        }

                        updatedList.add(song)
                    }
                    songList.value = updatedList
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