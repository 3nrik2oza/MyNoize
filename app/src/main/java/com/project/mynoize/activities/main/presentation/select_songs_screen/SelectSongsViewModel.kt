package com.project.mynoize.activities.main.presentation.select_songs_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.R
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.toErrorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus

class SelectSongsViewModel(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
): ViewModel() {

    private val _state = MutableStateFlow(SelectSongsState())
    val state = _state.asStateFlow()

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private var searchJob: Job? = null

    init {
        updateSongs(0)
    }

    fun onEvent(event: SelectSongsEvent){
        when(event){
            is SelectSongsEvent.OnSongClicked -> { addRemoveSong(event.index, event.add)
            }
            is SelectSongsEvent.SetPlaylist -> {
                viewModelScope.launch {
                    _state.update {  state ->
                        state.copy(playlist = playlistRepository.userPlaylists.first().find { it.id == event.playlistId }!!)
                    }
                }
            }
            is SelectSongsEvent.OnFinishClick -> { addSongsToPlaylist()
            }
            is SelectSongsEvent.OnSearchQueryChange -> {
                if (event.query.length > 30) return
                _state.value = _state.value.copy(query = event.query)
                searchJob?.cancel()
                searchJob = updateSongs()
            }
            else -> {}
        }
    }

    fun updateSongs(delay: Long = 500) =
        viewModelScope.launch {
            delay(delay)
            songRepository.getAllSongsContaining(state.value.query).onSuccess { songs ->
                val idsSet = _state.value.playlist.songs.toSet()
                _state.update { state ->
                    state.copy(
                        songs = songs.filterNot { it.id in idsSet }.map { it.copy(favorite = state.selectedSongs.contains(it)) }
                    )
                }
            }

    }

    private fun addSongsToPlaylist(){
        viewModelScope.launch {
            val playlistSongs = _state.value.playlist.songs + _state.value.selectedSongs.map { it.id }
            playlistRepository.updateSongsInPlaylist(songs = playlistSongs, id = _state.value.playlist.id).onError { error ->
                _alertDialogState.update {
                    it.copy(
                        show = true,
                        message = error.toErrorMessage(),
                        warning = true
                    )
                }
            }.onSuccess {
                _state.update {
                    it.copy(
                        playlist = it.playlist.copy(songs = playlistSongs)
                    )
                }
                _alertDialogState.update {
                    it.copy(
                        show = true,
                        message = UiText.StringResource(R.string.successfully_added_songs_to_playlist),
                        warning = false
                    )
                }
            }
        }

    }

    private fun addRemoveSong(index: Int, add: Boolean){
        val songsUpdated = _state.value.songs.toMutableList()
        songsUpdated[index] = songsUpdated[index].copy(favorite = add)
        if(add){
            _state.update {
                it.copy(
                    selectedSongs = it.selectedSongs + it.songs[index],
                    songs = songsUpdated
                )
            }
        }else{
            _state.update {
                it.copy(
                    selectedSongs = it.selectedSongs - it.songs[index],
                    songs = songsUpdated
                )
            }
        }
    }

}

