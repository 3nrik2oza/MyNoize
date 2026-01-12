package com.project.mynoize.activities.main.presentation.music_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.mynoize.activities.main.presentation.main_screen.MainActivityUiEvent
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.UserRepository
import com.project.mynoize.core.domain.map
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.managers.ExoPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicScreenViewModel(
    //private val playlistRepository: PlaylistRepository,
    //private val songRepository: SongRepository,
    //private val userRepository: UserRepository,
    // private val exoPlayerManager: ExoPlayerManager
): ViewModel() {

    val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(MusicScreenState())
    val state = _state

    private val _uiEvent = MutableSharedFlow<MainActivityUiEvent>()
    val uiEvent = _uiEvent


    init{
        //generateRandomPlaylist()
    }

    fun onEvent(event: MusicScreenEvent){
        when(event){
            is MusicScreenEvent.OnLogoutClick -> {
                signOut()
            }
            is MusicScreenEvent.OnPlaySongsForUser -> {
               // exoPlayerManager.initializePlayer(songs = state.value.songsForUser, play = true, false,viewModelScope, index = 0, playlistId = "")
            }
        }
    }

    private fun generateRandomPlaylist(){
        /*viewModelScope.launch {
            val favoriteArtists = userRepository.user.first().favoriteArtists
            songRepository.getSongByAuthors(favoriteArtists).onSuccess {
                val songs = it.shuffled()
                _state.update { it.copy(songsForUser = songs) }
            }
        }*/
    }

    private fun signOut() {
        auth.signOut()

        viewModelScope.launch {
            delay(1000)
            _uiEvent.emit(MainActivityUiEvent.NavigateToSignIn)
        }
    }

}