package com.project.mynoize.activities.main.presentation.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.SearchItem
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.UserRepository
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.managers.ExoPlayerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchScreenViewModel(
    private val userRepo: UserRepository,
    private val songsRepository: SongRepository,
    private val artistsRepository: ArtistRepository,
    private val playlistsRepository: PlaylistRepository,
    private val albumsRepository: AlbumRepository,
    private val exoPlayerManager: ExoPlayerManager
): ViewModel() {

    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            playlistsRepository.userPlaylists.collect { playlists ->
                _state.update { it.copy(userPlaylists = playlists) }
            }
        }
    }


    fun onEvent(event: SearchScreenEvent){
        if(state.value.isInvisibleLoading) return
        when(event){
            is SearchScreenEvent.OnSearchQueryChange -> {
                if(event.query.length > 30) return
                _state.value = _state.value.copy(searchQuery = event.query, isLoading = true)
                updateSearchJob()
            }
            is SearchScreenEvent.OnSearchTypeChange -> {
                _state.value = _state.value.copy(selectedSearchType = event.type, isLoading = true)
                updateSearchJob()
            }
            is SearchScreenEvent.OnSearchItemFavoriteClicked -> favoriteClicked(event.item)
            is SearchScreenEvent.OnSearchItemClicked -> {
                when(event.item){
                    is SearchItem.SongItem -> {
                        exoPlayerManager.initializePlayer(songs = listOf(event.item.song), play = true,
                            shuffle = false,viewModelScope, index = 0, playlistId = "")
                    }
                    else -> {}
                }
            }
            is SearchScreenEvent.OnMoreOptionsSongClick -> onMoreSongClicked(event.selectedSong)
            is SearchScreenEvent.OnToggleMoreOptionsSheet -> { _state.update { it.copy(isSheetOpen = !_state.value.isSheetOpen) } }
            is SearchScreenEvent.OnToggleSelectPlaylistSheet -> { _state.update { it.copy(selectPlaylistSheet = !_state.value.selectPlaylistSheet) } }
            is SearchScreenEvent.OnUpdateSearchItems -> {
                _state.update { it.copy(isInvisibleLoading = true) }
                updateSearchJob(0)
            }

            is SearchScreenEvent.OnPlaylistSelected ->{
                _state.update { it.copy(selectPlaylistSheet = false) }
                viewModelScope.launch {
                    playlistsRepository.updateSongsInPlaylist(songs = event.playlist.songs + state.value.selectedSong!!.id, id = event.playlist.id)
                }

            }

        }
    }

    private fun favoriteClicked(selectedItem: SearchItem){
        when(selectedItem){
            is SearchItem.SongItem -> {
                viewModelScope.launch {
                    userRepo.updateFavoriteSongs(selectedItem.song.id, selectedItem.song.favorite)
                    _state.update { it.copy(searchItems = it.searchItems.map { item -> if(item.id == selectedItem.id) SearchItem.SongItem(selectedItem.song.copy(favorite = !selectedItem.song.favorite)) else item }) }
                }
            }
            is SearchItem.ArtistItem ->{
                viewModelScope.launch {
                    userRepo.updateFavoriteArtists(selectedItem.artist.id, selectedItem.artist.favorite)
                    _state.update { it.copy(searchItems = it.searchItems.map { item -> if(item.id == selectedItem.id) SearchItem.ArtistItem(selectedItem.artist.copy(favorite = !selectedItem.artist.favorite)) else item }) }
                }
            }
            is SearchItem.PlaylistItem ->{
                viewModelScope.launch {
                    userRepo.updateFavoritePlaylist(selectedItem.playlist.id, selectedItem.playlist.favorite)
                    _state.update { it.copy(searchItems = it.searchItems.map { item -> if(item.id == selectedItem.id) SearchItem.PlaylistItem(selectedItem.playlist.copy(favorite = !selectedItem.playlist.favorite)) else item }) }
                }
            }
            is SearchItem.AlbumItem ->{
                viewModelScope.launch {
                    userRepo.updateFavoriteAlbums(selectedItem.album.id, selectedItem.album.favorite)
                    _state.update { it.copy(searchItems = it.searchItems.map { item -> if(item.id == selectedItem.id) SearchItem.AlbumItem(selectedItem.album.copy(favorite = !selectedItem.album.favorite)) else item }) }
                }
            }
        }
    }

    private fun onMoreSongClicked(selectedSong: SearchItem.SongItem){
        viewModelScope.launch {
            artistsRepository.getArtist(selectedSong.song.artistId).onSuccess { artist ->
                _state.update { it.copy(selectedArtist = artist, isSheetOpen = true, selectedSong = selectedSong) }
            }
        }
    }
    
    private fun updateSearchJob(delay: Long = 500){
        searchJob?.cancel()
        searchJob = when(state.value.selectedSearchType){
            SearchTypes.SONGS -> getSongs(delay)
            SearchTypes.ARTISTS -> getArtists(delay)
            SearchTypes.PLAYLISTS -> getPlaylists(delay)
            SearchTypes.ALBUMS -> getAlbums(delay)
        }
    }

    private fun getSongs(delay: Long) =
        viewModelScope.launch {
            delay(delay)
            val favSongs = userRepo.user.first().favoriteSongs

            songsRepository.getAllSongsContaining(state.value.searchQuery.lowercase()).onSuccess { songs ->
                _state.update { it.copy(searchItems = songs.map { song -> SearchItem.SongItem(song.copy(favorite = favSongs.contains(song.id))) }, isLoading = false, isInvisibleLoading = false) }
            }
        }

    private fun getArtists(delay: Long) =
        viewModelScope.launch {
            delay(delay)
            val favArtists = userRepo.user.first().favoriteArtists

            artistsRepository.getArtistsContaining(state.value.searchQuery.lowercase()).onSuccess { artists ->
                _state.update { it.copy(searchItems = artists.map { artist -> SearchItem.ArtistItem(artist.copy(favorite = favArtists.contains(artist.id))) }, isLoading = false, isInvisibleLoading = false) }
            }

        }

    private fun getPlaylists(delay: Long) =
        viewModelScope.launch {
            delay(delay)
            val favPlaylists = userRepo.user.first().favoritePlaylists

            playlistsRepository.getPlaylistsContaining(state.value.searchQuery.lowercase()).onSuccess { playlists ->
                _state.update { it.copy(searchItems = playlists.map { playlist -> SearchItem.PlaylistItem(playlist.copy(favorite = favPlaylists.contains(playlist.id))) }, isLoading = false, isInvisibleLoading = false) }
            }
        }

    private fun getAlbums(delay: Long) =
        viewModelScope.launch {
            delay(delay)
            val favAlbums = userRepo.user.first().favoriteAlbums

            albumsRepository.getAllAlbumsContaining(state.value.searchQuery.lowercase()).onSuccess { albums ->
                _state.update { it.copy(searchItems = albums.map { album -> SearchItem.AlbumItem(album.copy(favorite = favAlbums.contains(album.id))) }, isLoading = false) }
            }
        }

}