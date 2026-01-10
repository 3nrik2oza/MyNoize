package com.project.mynoize.activities.main.presentation.playlist_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.data.repositories.UserRepository
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.util.BottomSheetType
import com.project.mynoize.util.toPlaylist
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
    private val exoPlayerManager: ExoPlayerManager,
    private val userRepository: UserRepository,
    private val albumRepository: AlbumRepository
): ViewModel() {

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private val _state = MutableStateFlow(PlaylistScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            playlistRepository.favoriteSongsPlaylist.collect { playlist ->
                _state.update { state ->
                    state.copy(favoriteList = playlist) }

                _state.update { state ->
                    state.copy(songs = state.songs.map { it.copy(favorite = playlist.songs.contains(it.id)) })
                }

            }
        }
        viewModelScope.launch {
            playlistRepository.userPlaylists.collect { playlists ->
                _state.update { it.copy(userPlaylists = playlists) }
            }
        }
    }


    fun onEvent(event: PlaylistScreenEvent){
        when(event){
            is PlaylistScreenEvent.SetPlaylistId -> setPlaylistData(event.playlistId, event.isPlaylist)
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

            is PlaylistScreenEvent.OnSongFavoriteToggle ->{
                viewModelScope.launch {
                    userRepository.updateFavoriteSongs(event.song.id, event.song.favorite)
                }
            }
            is PlaylistScreenEvent.OnArtistClick -> {
                _state.update { it.copy(isSheetOpen = false) }
            }
            is PlaylistScreenEvent.OnToggleSelectPlaylistSheet ->{
                _state.update { it.copy(selectPlaylistSheet = !it.selectPlaylistSheet) }
            }
            is PlaylistScreenEvent.OnPlaylistSelected ->{
                _state.update { it.copy(selectPlaylistSheet = false) }
                viewModelScope.launch {
                    playlistRepository.updateSongsInPlaylist(songs = event.playlist.songs + state.value.selectedSong().id, id = event.playlist.id)
                }

            }
            else ->{

            }
        }
    }

    private fun deletePlaylist(){
        storageRepository.removeFromStorage(state.value.playlist.imagePath)
        playlistRepository.deletePlaylist(playlistId = state.value.playlist.id)
    }

    private fun removeSongFromPlaylist(){
        val currentState = state.value
        val songs = currentState.playlist.songs - currentState.selectedSong().id
        if(currentState.playlist.name == "Favorites"){
            viewModelScope.launch {
                userRepository.updateFavoriteSongs(currentState.selectedSong().id, currentState.selectedSong().favorite).onSuccess {
                    _state.update { it.copy(isSheetOpen = false) }
                }
            }
            return
        }

        viewModelScope.launch {
            playlistRepository.updateSongsInPlaylist(songs, currentState.playlist.id).onSuccess {
                _state.update { it.copy(isSheetOpen = false) }
            }
        }

    }

    private fun onMorePlaylistClicked(){
        _state.update { it.copy(isSheetOpen = true, sheetType = BottomSheetType.PLAYLIST) }
    }

    private fun onMoreSongClicked(index: Int){
        viewModelScope.launch {

            val artist = artistRepository.artists.first().find { it.id == state.value.songs[index].artistId }

            if(artist != null){
                _state.update { it.copy(artist = artist, isSheetOpen = true, selectedSongIndex = index) }
            }
        }
    }

    private fun setPlaylistData(playlistId: String, isPlaylist: Boolean){
        if(isPlaylist){
            viewModelScope.launch {
                playlistRepository.playlistsWithFavorites.collect { playlists ->
                    _state.update { state ->
                        state.copy(playlist = playlists.find { it.id == playlistId}!!)
                    }
                    songRepository.getSongByIds(state.value.playlist.songs).onSuccess { songs ->
                        _state.update { state -> state.copy(songs = songs.map { it.copy(favorite = state.favoriteList.songs.contains(it.id)) }) }
                    }
                }
            }
            return
        }
        viewModelScope.launch {
            albumRepository.getAlbum(playlistId).collect { album ->
                _state.update { state ->
                    state.copy(playlist = album.toPlaylist(), isPlaylist = false)
                }

                songRepository.getSongByAlbumId(album.id).onSuccess { songs ->
                    _state.update { state -> state.copy(songs = songs.map { it.copy(favorite = state.favoriteList.songs.contains(it.id)) }) }
                }
            }
        }

        }
}
