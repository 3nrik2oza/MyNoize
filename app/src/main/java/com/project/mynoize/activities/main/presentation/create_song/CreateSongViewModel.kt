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
import com.project.mynoize.core.domain.entities.Album
import com.project.mynoize.core.data.AuthRepository

import com.project.mynoize.core.domain.entities.Song
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.toErrorMessage
import com.project.mynoize.util.Era
import com.project.mynoize.util.toLanguage
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

    private val _state = MutableStateFlow(CreateSongState())
    val state = _state

    private val _createAlbumDialogState = MutableStateFlow(AlertDialogState())
    val createAlbumDialogState = _createAlbumDialogState

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState

    init{
        viewModelScope.launch {
            artistRepository.getArtists().onSuccess { list ->
                state.update { it.copy(artistList = list) }
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
                state.update { it.copy(songName = event.songName) }
            }
            is CreateSongEvent.OnArtistClick -> {
                state.update {
                    it.copy(
                        selectedArtist = event.artist,
                        language = event.artist.country?.toLanguage(),
                        songGenre = event.artist.genre,
                        selectedAlbum = null
                    )
                }
                loadAlbums(event.artist.id)
            }
            is CreateSongEvent.OnAlbumClick -> {
                state.update { it.copy(selectedAlbum = event.album, era = event.album.era) }
            }
            is CreateSongEvent.OnAddAlbumClick -> {
                state.update { it.copy(showCreateAlbum = true) }
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
            is CreateSongEvent.OnGenreClick -> {
                state.update { it.copy(songGenre = event.selected, songSubgenre = null) }
            }
            is CreateSongEvent.OnSubgenreClick -> {
                state.update { it.copy(songSubgenre = event.selected) }
            }
            is CreateSongEvent.OnLanguageClick -> state.update { it.copy(language = event.selected) }
            is CreateSongEvent.OnEraClick -> state.update { it.copy(era = event.selected) }
            is CreateSongEvent.OnMoodClick -> state.update {
                val containsMood = it.moods.contains(event.selected)
                val moods = if(containsMood) it.moods - event.selected else  it.moods + event.selected
                it.copy(moods = if(moods.size < 4) moods else it.moods )
            }
            is CreateSongEvent.OnBackClick -> {}
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
                state.update { it.copy(showCreateAlbum = false) }
            }
            is CreateAlbumEvent.OnCreateAlbum -> {
                createAlbum(
                    imageUri = event.imageUri,
                    albumName = event.albumName,
                    era = event.era
                )
            }
        }
    }

    fun createAlbumType(albumName: String, imageUrl: String, imagePath: String, artistId: String, era: Era): Album{
        return Album(
            name = albumName,
            imageLink = imageUrl,
            imagePath = imagePath,
            creator = auth.getCurrentUserId(),
            era = era,
            artist = artistId
        )
    }

    fun loadAlbums(artistId: String){
        viewModelScope.launch{
            albumRepository.getAlbums(artistId)
                .onSuccess { list ->
                    state.update { it.copy(albumList = list) }
                }
                .onError {
                    alertDialogState.update {
                        it.copy(show = true, message = UiText.StringResource(R.string.error_loading_album_failed))
                    }
                }
        }
    }

    fun loadSongTitle(context: Context, uri: String) {
        state.update { it.copy(songUri = uri) }
        val cursor = context.contentResolver.query(uri.toUri(), null, null, null, null)
        cursor?.use { cursor->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    val loadedTitle = cursor.getString(index).removeSuffix(".mp3")
                    state.update {
                        it.copy(
                            songTitle = loadedTitle,
                            songName = loadedTitle
                                .replace(Regex("\\[.*?]"), "")
                                .replace(Regex("\\(.*?\\)"), "")
                                .trim()
                        )
                    }
                }
            }
        }
    }

    fun addSongToStorage(){
        alertDialogState.update { it.copy(loading = true) }
        state.update { it.copy(error = null) }

        val currentState = state.value

        createSongValidation.execute(
            state = currentState
        ).onError { error->
            state.update { it.copy(error = error) }

            alertDialogState.update { it.copy(loading = false) }
            return
        }.onSuccess { song ->
            val fileName = "songs/${song.title}"
            viewModelScope.launch {
                storageRepository.addToStorage(
                    currentState.songUri.toUri(),
                    fileName
                ).onError { error ->
                    alertDialogState.update {
                        it.copy(show = true, loading = false, message = error.toErrorMessage())
                    }
                }.onSuccess {
                    addSong(
                        song.copy(
                            songUrl = it.downloadLink,
                            audioPath = it.path,
                            creatorId = auth.getCurrentUserId(),
                            )
                    )
                }
            }
        }
    }

    suspend fun addSong(remoteSong: Song){
        songRepository.addSongToFirebase(remoteSong)
            .onSuccess {
                alertDialogState.update {
                    it.copy(
                        show = true,
                        message = UiText.StringResource(R.string.song_added_successfully),
                        warning = false
                    )
                }
            }.onError { error ->
                alertDialogState.update {
                    it.copy(
                        show = true,
                        loading = false,
                        message = error.toErrorMessage(),
                        warning = true
                    )
                }
                storageRepository.removeFromStorage(remoteSong.audioPath)
            }
    }

    fun createAlbum(imageUri: String, albumName: String, era: Era){
        val artistId = state.value.selectedArtist?.id ?: return
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
                album = createAlbumType(
                    albumName = albumName,
                    imageUrl =  it.downloadLink,
                    imagePath = it.path,
                    artistId = artistId,
                    era = era
                )
            }
            albumRepository.createAlbum(album)
                .onSuccess { id ->
                    state.update { it.copy(showCreateAlbum = false, albumList = it.albumList + album.copy(id = id)) }
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