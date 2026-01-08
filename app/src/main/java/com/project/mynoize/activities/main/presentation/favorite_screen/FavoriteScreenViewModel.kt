package com.project.mynoize.activities.main.presentation.favorite_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val auth: AuthRepository
): ViewModel() {

    private var _state = MutableStateFlow(FavoriteScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            playlistRepository.playlistsWithFavorites.collect { playlists ->
                _state.update { it.copy(playlists = playlists) }
            }
        }

    }

    fun onEvent(event: FavoriteScreenEvent){
        when(event){
            is FavoriteScreenEvent.OnPlaylistClicked -> {}
            else -> {}
        }
    }

}