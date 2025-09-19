package com.project.mynoize.activities.main.presentation.playlist_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.AlertDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistScreenViewModel(
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository
): ViewModel() {

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private val _state = MutableStateFlow(PlaylistScreenState())
    val state = _state.asStateFlow()


    fun onEvent(event: PlaylistScreenEvent){
        when(event){
            is PlaylistScreenEvent.SetPlaylistId -> setPlaylistData(event.playlistId)
            is PlaylistScreenEvent.OnMoreClick -> onMoreClicked(event.index)
            is PlaylistScreenEvent.OnRemoveSongClick -> {
                val songs = state.value.playlist.songs - state.value.selectedSong().id

                viewModelScope.launch {
                    playlistRepository.updateSongsInPlaylist(songs, state.value.playlist.id).onSuccess {
                        _state.update { it.copy(isSheetOpen = false, playlist = it.playlist.copy(songs = songs), songs = it.songs - it.selectedSong()) }
                    }
                }
            }
            is PlaylistScreenEvent.OnDismissAlertDialog -> _state.update { it.copy(isSheetOpen = false) }
            else ->{

            }
        }
    }



    private fun onMoreClicked(index: Int){
        viewModelScope.launch {

            artistRepository.getArtist(state.value.songs[index].artistId).onSuccess { artist ->
                _state.update { it.copy(artist = artist, isSheetOpen = true, selectedSongIndex = index) }
            }

        }
    }

    private fun setPlaylistData(playlistId: String){
        _state.update { state->
            state.copy(
                playlist = playlistRepository.lastLoadedPlaylists.find{ it.id == playlistId }!!
            )
        }
        viewModelScope.launch {
            songRepository.getSongByIds(state.value.playlist.songs).onSuccess { songs ->
                _state.update {
                    it.copy(
                        songs = songs
                    )
                }
            }
        }

    }
}
