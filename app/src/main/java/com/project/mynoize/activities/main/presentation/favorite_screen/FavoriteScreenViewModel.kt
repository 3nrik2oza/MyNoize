package com.project.mynoize.activities.main.presentation.favorite_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val auth: AuthRepository
): ViewModel() {

    private var _state = MutableStateFlow(FavoriteScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            playlistRepository.getPlaylist(auth.getCurrentUserId()).onSuccess {
                _state.value = _state.value.copy(playlists = it)

            }
        }

    }

    fun onEvent(event: FavoriteScreenEvent){
        when(event){
            is FavoriteScreenEvent.OnPlaylistClicked -> {}
        }
    }

}