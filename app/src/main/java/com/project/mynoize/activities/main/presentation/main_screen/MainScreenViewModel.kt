package com.project.mynoize.activities.main.presentation.main_screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.domain.onError
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
class MainScreenViewModel (
    val playerManager: ExoPlayerManager,
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val storageRepository: StorageRepository,
    private val auth: AuthRepository,
    private val dataStore: UserInformation,
    private val networkMonitor: NetworkMonitor,
    application: Application) : AndroidViewModel(application) {



    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()



    private val _uiEvent = MutableSharedFlow<MainActivityUiEvent>()
    val uiEvent = _uiEvent

    val isConnected = networkMonitor.isConnected.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), true)

    init{

        viewModelScope.launch {

            combine(
                playerManager.currentSong,
                playerManager.isPlaying,
                playerManager.currentPosition,
                playerManager.duration
            ){song, isPlaying, currentPosition, duration ->
                MainScreenState(
                    currentSong = song,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    songList = _state.value.songList
                )
            }.collect { newState ->
                _state.update { newState }
            }

        }


        viewModelScope.launch {
            try {
                val lastPlayedPlaylistId = dataStore.playlistId.first() ?: return@launch
                val playlist = playlistRepository.getPlaylist(lastPlayedPlaylistId).first()

                songRepository.getLocalSongsAsPrimary(ids = playlist.songs).onSuccess { songs ->
                    _state.update {
                        it.copy(
                            songList = songs
                        )
                    }
                    playerManager.initializePlayer(songs = songs, play = false, scope = viewModelScope, playlistId = playlist.id)
                }
            }catch (e: Exception){
                print(e)
            }


        }

        viewModelScope.launch {
            playlistRepository.userPlaylists.collect { playlists ->
                if(playlists.isEmpty()){
                    return@collect
                }
                val localUpdate = dataStore.lastModifiedFavPlaylists.first()?.toLong()
                val ops = playlistRepository.updateFavoritePlaylists(localUpdate)
                ops.forEach {
                    it()
                }
                delay(1000)
                val localPlaylists = playlistRepository.localFavoritePlaylists.first()

                try {
                    localPlaylists
                        .filter { !it.songsDownloaded }
                        .forEach { playlist ->
                            val songList = playlist.songs
                            val missingSongsIds = songList.toSet() - songRepository.getExistingSongs(songList).toSet()
                            var songs = emptyList<Song>()
                            songRepository.getSongsByIdsFirebase(missingSongsIds.toList()).onSuccess { listOfSong ->
                                songs = listOfSong
                            }.onError {
                                return@forEach
                            }

                            songs.forEach { song -> downloadMissingSong(song) }
                            playlistRepository.setPlaylistAsDownloaded(playlist.id, true)
                        }
                }catch (e: Exception){
                    print(e)
                }

            }


        }
        viewModelScope.launch {
            try {
                songRepository.favoriteSongsList.collect { songs ->
                    val songsIds = songs.map { it.id }
                    val localSongs = songRepository.getExistingSongs(songsIds)
                    val missingSongsIds = songsIds.toSet() - localSongs.toSet()

                    songs.filter { it.id in missingSongsIds }.forEach { downloadMissingSong(it)}
                }
            }catch (e: Exception){
                print(e)
            }
        }

        viewModelScope.launch {


            try {
                albumRepository.remoteFavoriteAlbums.collect { albums ->
                    if(albums.isEmpty()){
                        return@collect
                    }
                    val localUpdate = dataStore.lastModifiedFavAlbums.first()?.toLong()
                    val ops = albumRepository.updateFavoriteAlbums(localUpdate)
                    ops.forEach {
                        it()
                    }

                    delay(2000)
                    val localAlbums = albumRepository.localFavoriteAlbums.first()

                    localAlbums.filter { !it.songsDownloaded }.forEach { album ->
                        var albumSongs = emptyList<Song>()
                        songRepository.getSongByAlbumId(album.id, album.songsDownloaded).onSuccess {
                            albumSongs = it
                        }
                        val songsInAlbumIds = albumSongs.map { it.id }

                        val missingSongsIds = songsInAlbumIds.toSet() - songRepository.getExistingSongs(songsInAlbumIds).toSet()

                        albumSongs.filter { it.id in missingSongsIds }.forEach { downloadMissingSong(it, album.localImageUrl) }
                        albumRepository.updateAlbumDownloadedField(album.id, true)
                    }
                }
            }catch (e: Exception){
                print(e)
            }
        }
        viewModelScope.launch {
            savePosition()
        }
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
                }
                viewModelScope.launch {
                    delay(1000)
                    _uiEvent.emit(MainActivityUiEvent.NavigateToSignIn)
                }
            }
            is MainScreenEvent.OnStartListening -> {
                /*
                */
            }
        }
    }

    suspend fun savePosition(){
        while (true){
            delay(1000)
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

        viewModelScope.launch {
         //   dataStore.updateMediaId(currentSong.value!!.mediaId)
        }

        onEventUi(MainActivityUiEvent.ShowNotification)
    }

    fun prevSong(){
        playerManager.prevSong()

        viewModelScope.launch {
         //   dataStore.updateMediaId(currentSong.value!!.mediaId)
        }
        onEventUi(MainActivityUiEvent.ShowNotification)
    }

    private suspend fun downloadMissingSong(song: Song){
        val localAlbum: MutableList<Album> = albumRepository.doesAlbumExist(song.albumId).toMutableList()
        if(localAlbum.isEmpty()){
            var album = albumRepository.getAlbum(song.artistId+"/"+song.albumId).first()
            storageRepository.downloadToLocalMemory(album.image, "album_images").onSuccess {
                album = album.copy(localImageUrl = it)
            }.onError {
                return@downloadMissingSong
            }
            albumRepository.saveAlbumLocally(album)

            localAlbum.add(album)
        }
        downloadMissingSong(song, localAlbum.first().localImageUrl)
    }

    private suspend fun downloadMissingSong(song: Song, imageUrl: String){
        var localSongUrl = ""
        storageRepository.downloadToLocalMemory(song.songUrl, "songs").onSuccess {
            localSongUrl = it
        }
        songRepository.saveSongLocally(
            song.copy(localSongUrl = localSongUrl, localImageUrl = imageUrl)
        )
    }


}