package com.project.mynoize.activities.main.presentation.create_playlist

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_playlist.domain.CreatePlaylistValidation
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.domain.entities.Playlist
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
    private val auth: AuthRepository,
) : ViewModel() {

    private var _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private var _state = MutableStateFlow(CreatePlaylistState())
    var state = _state.asStateFlow()


    fun onEvent(event: CreatePlaylistEvent) {
        when (event) {
            is CreatePlaylistEvent.OnImageChange -> {
                if (!state.value.loading) {
                    _state.update { it.copy(playlistImage = event.uri) }
                }

            }

            is CreatePlaylistEvent.OnPlaylistNameChange -> {
                if (!state.value.loading) {
                    _state.update { it.copy(playlistName = event.name) }
                }

            }

            is CreatePlaylistEvent.OnDismissAlertDialog -> {
                _alertDialogState.update { it.copy(show = false) }
            }

            is CreatePlaylistEvent.OnModifyPlaylist -> {
                viewModelScope.launch {
                    val playlist = playlistRepository.userPlaylists.first()
                        .find { it.id == event.playlistId }!!
                    _state.update {
                        it.copy(
                            playlistName = playlist.name,
                            playlistImage = playlist.imageLink.toUri(),
                            playlist = playlist
                        )
                    }
                }

            }

            is CreatePlaylistEvent.OnTagSelected -> {
                val state = _state.value
                if (state.tags.contains(event.selected)) {
                    _state.update { it.copy(tags = it.tags - event.selected) }
                    return
                }
                if(state.tags.size < 5){
                    _state.update { it.copy(tags = it.tags + event.selected) }
                }
            }

            is CreatePlaylistEvent.OnAddPlaylistClick -> addPlaylist()
            is CreatePlaylistEvent.OnBackClick -> Unit
        }
    }

    fun addPlaylist() {
        val state = _state.value

        _state.update {
            it.copy(
                loading = true,
                playlistNameError = null,
                playlistImageError = null,
                tagsError = null
            )
        }


        playlistValidation.execute(
            playlistName = state.playlistName,
            playlistImage = state.playlistImage,
            tags = state.tags
        ).onError { error ->

            _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }

            when (error) {
                InputError.CreatePlaylist.ENTER_PLAYLIST_NAME -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            playlistNameError = error.toErrorMessage()
                        )
                    }
                }

                InputError.CreatePlaylist.PLAYLIST_NAME_TOO_LONG -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            playlistNameError = error.toErrorMessage()
                        )
                    }
                }

                InputError.CreatePlaylist.SELECT_PLAYLIST_IMAGE -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            playlistImageError = error.toErrorMessage()
                        )
                    }
                }

                InputError.CreatePlaylist.SELECT_AT_LEAST_ONE_TAG -> {
                    _state.update { it.copy(loading = false, tagsError = error.toErrorMessage()) }
                }
            }
            return
        }

        if (state.playlist != null) {
            val oldPlayList = state.playlist
            var playlist = oldPlayList.copy(name = state.playlistName)
            if (oldPlayList.imageLink.toUri() != state.playlistImage) {
                val fileName = "playlist_image/${state.playlistImage!!.lastPathSegment}"


                viewModelScope.launch {
                    storageRepository.addToStorage(
                        file = state.playlistImage,
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
                        playlist = playlist.copy(imagePath = it.path, imageLink = it.downloadLink)

                        viewModelScope.launch {
                            playlistRepository.updatePlaylist(playlist).onError { error ->
                                _alertDialogState.update { st ->
                                    st.copy(
                                        show = true,
                                        warning = true,
                                        message = error.toErrorMessage()
                                    )
                                }
                                _state.update { st -> st.copy(loading = false) }
                            }.onSuccess {
                                _state.update { st -> st.copy(loading = false) }
                                _alertDialogState.update { st ->
                                    st.copy(
                                        show = true,
                                        warning = false,
                                        message = UiText.StringResource(R.string.playlist_updated_successfully)
                                    )
                                }
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
                    _alertDialogState.update {
                        it.copy(
                            show = true,
                            warning = false,
                            message = UiText.StringResource(R.string.playlist_updated_successfully)
                        )
                    }
                }
            }
            return
        }

        val fileName = "playlist_image/${state.playlistImage!!.lastPathSegment}"

        var playlist = Playlist(
            name = state.playlistName,
            creator = auth.getCurrentUserId(),
            tags = state.tags,
        )

        viewModelScope.launch {
            storageRepository.addToStorage(
                file = state.playlistImage,
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
                playlist = playlist.copy(imageLink = it.downloadLink, imagePath = it.path)
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
                _alertDialogState.update {
                    it.copy(
                        show = true,
                        warning = false,
                        message = UiText.StringResource(R.string.playlist_added_successfully)
                    )
                }
            }

        }
    }

}