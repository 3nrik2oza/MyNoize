package com.project.mynoize.activities.main.presentation.create_playlist

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_playlist.domain.CreatePlaylistValidation
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.toErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistValidation: CreatePlaylistValidation,
    private val storageRepository: StorageRepository,
    private val playlistRepository: PlaylistRepository,
    private val auth: AuthRepository
): ViewModel() {

    private var _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private var _state = MutableStateFlow(CreatePlaylistState())
    var state = _state.asStateFlow()


    fun onEvent(event: CreatePlaylistEvent){
        when(event){
            is CreatePlaylistEvent.OnImageChange -> {
                if(!state.value.loading){
                    _state.update { it.copy(playlistImage = event.uri) }
                }

            }
            is CreatePlaylistEvent.OnPlaylistNameChange -> {
                if(!state.value.loading){
                    _state.update { it.copy(playlistName = event.name) }
                }

            }
            is CreatePlaylistEvent.OnDismissAlertDialog -> {
                _alertDialogState.update { it.copy(show = false) }
            }
            is CreatePlaylistEvent.OnModifyPlaylist -> {
                viewModelScope.launch {
                    val playlist = playlistRepository.userPlaylists.first().find{ it.id == event.playlistId }!!
                    _state.update { it.copy(playlistName = playlist.name, playlistImage = playlist.imageLink.toUri(), playlist = playlist) }
                }

            }
            is CreatePlaylistEvent.OnAddPlaylistClick -> addPlaylist()
            is CreatePlaylistEvent.OnBackClick -> Unit
        }
    }

    fun addPlaylist(){
        _state.update { it.copy(loading = true) }

        _state.update { it.copy(playlistNameError = null, playlistImageError = null) }


        playlistValidation.execute(playlistName = state.value.playlistName, playlistImage = state.value.playlistImage).onError{ error ->

            _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }

            when(error){
                InputError.CreatePlaylist.ENTER_PLAYLIST_NAME -> {
                    _state.update { it.copy(loading = false, playlistNameError = error.toErrorMessage()) }
                }
                InputError.CreatePlaylist.PLAYLIST_NAME_TOO_LONG -> {
                    _state.update { it.copy(loading = false, playlistNameError = error.toErrorMessage()) }
                }
                InputError.CreatePlaylist.SELECT_PLAYLIST_IMAGE -> {
                    _state.update { it.copy(loading = false, playlistImageError = error.toErrorMessage()) }
                }
            }
            return
        }

        if(state.value.playlist != null){
            val oldPlayList = state.value.playlist!!
            var playlist = oldPlayList.copy(name = state.value.playlistName)
            if(oldPlayList.imageLink.toUri() != state.value.playlistImage){
                val fileName = "playlist_image/${state.value.playlistImage!!.lastPathSegment}"


                viewModelScope.launch {
                    storageRepository.addToStorage(
                        file = state.value.playlistImage!!,
                        path = fileName
                    ).onError { error ->
                        _alertDialogState.update {
                            it.copy(
                                show = true,
                                warning = true,
                                message = error.toErrorMessage()
                            )
                        }
                        _state.update { it.copy(loading = false) }
                    }.onSuccess {
                        storageRepository.removeFromStorage(oldPlayList.imagePath)
                        playlist = playlist.copy(imagePath = fileName, imageLink = it)

                        viewModelScope.launch {
                            playlistRepository.updatePlaylist(playlist).onError { error ->
                                _alertDialogState.update {
                                    it.copy(
                                        show = true,
                                        warning = true,
                                        message = error.toErrorMessage()
                                    )
                                }
                                _state.update { it.copy(loading = false) }
                            }.onSuccess {
                                _state.update { it.copy(loading = false) }
                                _alertDialogState.update { it.copy(show = true, warning = false, message = UiText.StringResource(R.string.playlist_updated_successfully)) }
                            }
                        }
                    }
                }
                return
            }
            viewModelScope.launch {
                playlistRepository.updatePlaylist(playlist).onError { error ->
                    _alertDialogState.update {
                        it.copy(
                            show = true,
                            warning = true,
                            message = error.toErrorMessage()
                        )
                    }
                    _state.update { it.copy(loading = false) }
                }.onSuccess {
                    _state.update { it.copy(loading = false) }
                    _alertDialogState.update { it.copy(show = true, warning = false, message = UiText.StringResource(R.string.playlist_updated_successfully)) }
                }
            }
            return
        }

        val fileName = "playlist_image/${state.value.playlistImage!!.lastPathSegment}"

        var playlist = Playlist()
        viewModelScope.launch {
            storageRepository.addToStorage(
                file = state.value.playlistImage!!,
                path = fileName
            ).onError { error ->
                _alertDialogState.update {
                    it.copy(
                        show = true,
                        warning = true,
                        message = error.toErrorMessage()
                    )
                }
                _state.update { it.copy(loading = false) }
            }.onSuccess {
                playlist = createPlaylist(it, imagePath = fileName)
            }

            playlistRepository.createPlaylist(playlist).onError { error ->
                _alertDialogState.update {
                    it.copy(
                        show = true,
                        warning = true,
                        message = error.toErrorMessage()
                    )
                }
                _state.update { it.copy(loading = false) }
            }.onSuccess {
                _state.update { it.copy(loading = false) }
                _alertDialogState.update { it.copy(show = true, warning = false, message = UiText.StringResource(R.string.playlist_added_successfully)) }
            }

        }
    }

    fun createPlaylist(imageLink: String, imagePath: String): Playlist{
        return Playlist(
            name = state.value.playlistName,
            creator = auth.getCurrentUserId(),
            imageLink = imageLink,
            imagePath = imagePath,
            songs = listOf(),
            artists = listOf()
        )

    }


}