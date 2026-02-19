package com.project.mynoize.activities.main.presentation.main_screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.activities.main.domain.use_case.MainUseCases
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.network.NetworkMonitor
import com.project.mynoize.util.UserInformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
class MainScreenViewModel (
    val playerManager: ExoPlayerManager,
    private val auth: AuthRepository,
    private val dataStore: UserInformation,
    private val networkMonitor: NetworkMonitor,
    private val mainUseCases: MainUseCases,
    application: Application
) : AndroidViewModel(application) {


    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()


    private val _uiEvent = MutableSharedFlow<MainActivityUiEvent>()
    val uiEvent = _uiEvent

    val isConnected = networkMonitor.isConnected.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), true)

    init{
        observePlayer()

        restoreLastSession()

        syncFavoritePlaylists()

        syncFavoriteSongs()

        syncFavoriteAlbums()

        removeLocalNonFavoriteSongs()
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
            is MainScreenEvent.OnLogoutClick -> {
                auth.signOut()
                viewModelScope.launch {
                    dataStore.clearAll()

                    delay(1000)
                    _uiEvent.emit(MainActivityUiEvent.NavigateToSignIn)
                }
            }
        }
    }


    private fun observePlayer() {
        combine(
            playerManager.currentSong,
            playerManager.isPlaying,
            playerManager.currentPosition,
            playerManager.duration
        ) { song, playing, position, duration ->
            PlayerState(song, playing, position, duration)
        }
            .onEach { playerState ->
                _state.update {
                    it.copy(
                        currentSong = playerState.song,
                        isPlaying = playerState.isPlaying,
                        currentPosition = playerState.position,
                        duration = playerState.duration
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun restoreLastSession(){
        viewModelScope.launch {
            val playlistId = dataStore.playlistId.first() ?: return@launch
            mainUseCases.restoreLastSessionUseCase.invoke(playlistId).onSuccess { songs ->
                _state.update {
                    it.copy(songList = songs)
                }

                playerManager.initializePlayer(songs = songs, play = false,
                    scope = viewModelScope, playlistId = playlistId)
            }
        }
    }

    private fun syncFavoritePlaylists(){
        viewModelScope.launch {
            mainUseCases.syncFavoritePlaylistUseCase(null)
        }
    }

    private fun syncFavoriteSongs(){
        viewModelScope.launch {
            mainUseCases.syncFavoriteSongsUseCase()
        }
    }

    private fun syncFavoriteAlbums(){
        viewModelScope.launch {
            mainUseCases.syncFavoriteAlbumsUseCase(null)
        }
    }

    private fun removeLocalNonFavoriteSongs(){
        viewModelScope.launch {
            mainUseCases.removeLocalNonFavoriteSongsUseCase()
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
        onEventUi(MainActivityUiEvent.ShowNotification)
    }

    fun prevSong(){
        playerManager.prevSong()
        onEventUi(MainActivityUiEvent.ShowNotification)
    }

}