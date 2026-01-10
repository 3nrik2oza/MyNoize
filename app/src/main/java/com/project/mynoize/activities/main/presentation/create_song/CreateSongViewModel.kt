package com.project.mynoize.activities.main.presentation.create_song

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_song.domain.CreateSongValidation
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.activities.main.state.ListOfState
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.Song
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.toErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus

class CreateSongViewModel(
    private val artistRepository: ArtistRepository,
    private val storageRepository: StorageRepository,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val createSongValidation: CreateSongValidation,
    private val auth: AuthRepository
): ViewModel() {

    private val _createSongState = MutableStateFlow(CreateSongState())
    val createSongState = _createSongState

    private val _artistListState = MutableStateFlow(ListOfState<Artist>())
    var artistListState = _artistListState

    private val _albumListState = MutableStateFlow(ListOfState<Album>())
    val albumListState = _albumListState

    private val _createAlbumDialogState = MutableStateFlow(AlertDialogState())
    val createAlbumDialogState = _createAlbumDialogState

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState

    init{
        viewModelScope.launch {
            artistRepository.getArtists().onSuccess { list ->
                artistListState.update { it.copy(list = list) }
            }.onError { _ ->
                alertDialogState.update {
                    it.copy(show = true, message = UiText.StringResource(R.string.error_loading_artist_failed))
                }
            }
        }
    }

    fun onEvent(event: CreateSongEvent) {
        when(event){
            is CreateSongEvent.OnSongNameChange -> {
                createSongState.update { it.copy(songName = event.songName) }
            }
            is CreateSongEvent.OnArtistClick -> {
                artistListState.update { it.copy(index = event.index) }

                loadAlbums(event.index)


                albumListState.update { it.copy(index = -1) }
            }
            is CreateSongEvent.OnAlbumClick -> {
                albumListState.update { it.copy(index = event.index) }
            }
            is CreateSongEvent.OnAddAlbumClick -> {
                createSongState.update { it.copy(showCreateAlbum = true) }
            }
            is CreateSongEvent.OnSelectSongClick -> {
                loadSongTitle(event.context, event.songUri)
            }
            is CreateSongEvent.OnAddSongClick -> {
                addSongToStorage()
            }
            is CreateSongEvent.OnDismissAlertDialog -> {
                alertDialogState.update {
                    it.copy(show = false)
                }
            }
            else -> Unit
        }
    }

    fun onCreateAlbumEvent(event: CreateAlbumEvent){
        when(event){
            is CreateAlbumEvent.OnDismissMessageDialog -> {
                createAlbumDialogState.update { it.copy(show = false) }
            }
            is CreateAlbumEvent.OnShowAlertDialog -> {
                createAlbumDialogState.update {
                    it.copy(show = true, message = event.error.toErrorMessage())
                }
            }
            is CreateAlbumEvent.OnDismissCreateAlbumDialog -> {
                createSongState.update { it.copy(showCreateAlbum = false) }
            }
            is CreateAlbumEvent.OnCreateAlbum -> {
                createAlbum(
                    imageUri = event.imageUri,
                    albumName = event.albumName
                )

            }
        }

    }



    fun createSong(songUrl: String): Song{
        return Song(
            title = createSongState.value.songName,
            artistId = artistListState.value.selectedElement().id,
            artistName = artistListState.value.selectedElement().name,
            songUrl = songUrl,
            imageUrl = albumListState.value.selectedElement().image,
            albumId = albumListState.value.selectedElement().id,
            albumName = albumListState.value.selectedElement().name,
            creatorId = auth.getCurrentUserId()
        )
    }

    fun createAlbumType(albumName: String, imageUrl: String): Album{
        return Album(
            name = albumName,
            image = imageUrl,
            creator = auth.getCurrentUserId(),
            artist = artistListState.value.selectedElement().id
        )
    }

    fun loadAlbums(index: Int){
        viewModelScope.launch{
            albumRepository.getAlbums(artistListState.value.list[index].id)
                .onSuccess { list ->
                    albumListState.update { it.copy(list = list) }
                }
                .onError {
                    alertDialogState.update {
                        it.copy(show = true, message = UiText.StringResource(R.string.error_loading_album_failed))
                    }
                }
        }
    }

    fun loadSongTitle(context: Context, uri: String) {
        createSongState.update { it.copy(songUri = uri) }
        val cursor = context.contentResolver.query(uri.toUri(), null, null, null, null)
        cursor?.use { cursor->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    createSongState.update { it.copy(songTitle = cursor.getString(index).removeSuffix(".mp3")) }
                }
            }
        }
    }

    fun addSongToStorage(){
        alertDialogState.update { it.copy(loading = true) }

        createSongValidation.execute(
            songName = createSongState.value.songName,
            artistIndex = artistListState.value.index,
            albumIndex = albumListState.value.index,
            uri = createSongState.value.songUri
        ).onError { error->

            createSongState.update { it.copy(songNameError = null, songUriError = null) }
            artistListState.update { it.copy(listError = null) }
            albumListState.update { it.copy(listError = null) }

            if(error == InputError.CreateSong.ENTER_SONG_NAME){
                createSongState.update { it.copy(songNameError = error.toErrorMessage()) }
            }
            if(error == InputError.CreateSong.SELECT_ARTIST){
                artistListState.update { it.copy(listError = error.toErrorMessage()) }
            }
            if(error == InputError.CreateSong.SELECT_ALBUM){
                albumListState.update { it.copy(listError = error.toErrorMessage()) }
            }
            if(error == InputError.CreateSong.SELECT_SONG_FILE){
                createSongState.update { it.copy(songUriError = error.toErrorMessage()) }
            }


            alertDialogState.update { it.copy(loading = false) }
            return
        }
        var song = Song()
        val fileName = "songs/${createSongState.value.songName}"
        viewModelScope.launch {
            storageRepository.addToStorage(
                createSongState.value.songUri.toUri(),
                fileName
            ).onError { error ->
                alertDialogState.update {
                    it.copy(show = true, loading = false, message = error.toErrorMessage())
                }
            }.onSuccess {
                song = createSong(it)

            }
            addSong(song, fileName)

        }
    }

    suspend fun addSong(song: Song, fileName: String){
        songRepository.addSongToFirebase(song)
            .onSuccess {
                alertDialogState.update {
                    it.copy(
                        show = true,
                        message = UiText.StringResource(R.string.song_added_successfully),
                        warning = false)
                }

            }.onError { error ->
                alertDialogState.update {it.copy(
                    show = true,
                    loading = false,
                    message = error.toErrorMessage(),
                    warning = true
                ) }
                storageRepository.removeFromStorage(fileName)
            }
    }

    fun createAlbum(imageUri: String, albumName: String){
        createAlbumDialogState.update { it.copy(loading = true) }

        val path = "album_images/${imageUri.toUri().lastPathSegment}"
        var album = Album()
        viewModelScope.launch {
            storageRepository.addToStorage(
                file = imageUri.toUri(),
                path = path
            ).onError { error ->
                createAlbumDialogState.update {
                    it.copy(show = true, loading = false, message = error.toErrorMessage())
                }
            }.onSuccess {
                album = createAlbumType(albumName = albumName, imageUrl =  it)
            }
            albumRepository.createAlbum(album)
                .onSuccess {
                    albumListState.update { it.copy(list = albumListState.value.list + album) }
                    createSongState.update { it.copy(showCreateAlbum = false) }
                    createAlbumDialogState.update { it.copy(loading = false) }
                }
                .onError { error->
                    createAlbumDialogState.update { it.copy(
                        show = true,
                        loading = false,
                        message = error.toErrorMessage()
                    ) }
                    storageRepository.removeFromStorage(path)
                }
        }
    }
}