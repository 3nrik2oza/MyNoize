package com.project.mynoize.activities.main.presentation.playlist_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.util.BottomSheetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistScreenViewModel(
    private val storageRepository: StorageRepository,
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val exoPlayerManager: ExoPlayerManager
): ViewModel() {

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private val _state = MutableStateFlow(PlaylistScreenState())
    val state = _state.asStateFlow()


    fun onEvent(event: PlaylistScreenEvent){
        when(event){
            is PlaylistScreenEvent.SetPlaylistId -> setPlaylistData(event.playlistId)
            is PlaylistScreenEvent.OnMoreSongClick -> onMoreSongClicked(event.index)
            is PlaylistScreenEvent.OnMorePlaylistClick -> onMorePlaylistClicked()
            is PlaylistScreenEvent.OnRemoveSongClick -> removeSongFromPlaylist()
            is PlaylistScreenEvent.OnDismissAlertDialog -> _state.update { it.copy(isSheetOpen = false) }
            is PlaylistScreenEvent.OnSongClicked ->{
                exoPlayerManager.initializePlayer(songs = state.value.songs, play = true, false,viewModelScope, index = event.index, playlistId = state.value.playlist.id)
            }
            is PlaylistScreenEvent.OnPlayRandom -> {
                exoPlayerManager.initializePlayer(songs = state.value.songs, play = true, true,viewModelScope, index = 0, playlistId = state.value.playlist.id)
            }
            is PlaylistScreenEvent.OnToggleDeletePlaylist -> { _state.update { it.copy(deletePlaylistSheetOpen = !it.deletePlaylistSheetOpen) } }
            is PlaylistScreenEvent.OnDeletePlaylist -> { deletePlaylist() }
            else ->{

            }
        }
    }

    private fun deletePlaylist(){
        storageRepository.removeFromStorage(state.value.playlist.imagePath)
        playlistRepository.deletePlaylist(playlistId = state.value.playlist.id)
    }

    private fun removeSongFromPlaylist(){
        val songs = state.value.playlist.songs - state.value.selectedSong().id

        viewModelScope.launch {
            playlistRepository.updateSongsInPlaylist(songs, state.value.playlist.id).onSuccess {
                _state.update { it.copy(isSheetOpen = false, playlist = it.playlist.copy(songs = songs), songs = it.songs - it.selectedSong()) }
            }
        }
    }

    private fun onMorePlaylistClicked(){
        _state.update { it.copy(isSheetOpen = true, sheetType = BottomSheetType.PLAYLIST) }
    }

    private fun onMoreSongClicked(index: Int){
        viewModelScope.launch {

            artistRepository.getArtist(state.value.songs[index].artistId).onSuccess { artist ->
                _state.update { it.copy(artist = artist, isSheetOpen = true, selectedSongIndex = index, sheetType = BottomSheetType.SONG) }
            }
        }
    }

    private fun setPlaylistData(playlistId: String){

        viewModelScope.launch {

            _state.update { state->
                state.copy(
                    playlist = playlistRepository.list.first().find{ it.id == playlistId }!!
                )
            }

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
