package com.project.mynoize.activities.main.presentation.artist_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.UserRepository
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.managers.ExoPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArtistScreenViewModel(
    private val artistRepository: ArtistRepository,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository,
    private val exoPlayerManager: ExoPlayerManager
): ViewModel() {

    private val _state = MutableStateFlow(ArtistScreenState())
    val state = _state.asStateFlow()


    fun onEvent(event: ArtistScreenEvent){
        when(event){
            is ArtistScreenEvent.SetArtistId -> {
                setArtistData(artistId = event.artistId)
            }
            is ArtistScreenEvent.ArtistFavoriteToggle -> {
                viewModelScope.launch {
                    userRepository.updateFavoriteArtists(
                        artistId = state.value.artist.id, favorite = state.value.favorite)
                }
            }
            is ArtistScreenEvent.OnSongFavoriteToggle -> {
                viewModelScope.launch {
                    userRepository.updateFavoriteSongs(event.song.id, event.song.favorite)
                }
            }
            is ArtistScreenEvent.OnSongClick -> {
                exoPlayerManager.initializePlayer(songs = state.value.songs, play = true,
                    shuffle = false,viewModelScope, index = event.index, playlistId = "")
            }
            else -> {}
        }
    }

    private fun setArtistData(artistId: String){
        viewModelScope.launch {
            artistRepository.getArtist(artistId).onSuccess {
                _state.update { state -> state.copy(artist = it) }
            }
        }
        viewModelScope.launch {
            val fav = userRepository.user.first().favoriteSongs
            songRepository.getSongByArtist(artistId = artistId).onSuccess { songs->
                _state.update { state -> state.copy(songs = songs.map { it.copy(favorite = fav.contains(it.id)) }) }
            }
        }
        viewModelScope.launch {
            userRepository.user.collect { user ->
                _state.update { state ->
                    state.copy(favorite = user.favoriteArtists.contains(artistId),
                        songs = state.songs.map { song -> song.copy(favorite = user.favoriteSongs.contains(song.id)) }
                    ) }
            }
        }

    }

}