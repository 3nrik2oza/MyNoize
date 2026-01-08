package com.project.mynoize.activities.main.presentation.create_artist


import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_artist.domain.CreateArtistValidation
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.AuthRepository
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

class CreateArtistViewModel(
    private val artistRepository: ArtistRepository,
    private val storageRepository: StorageRepository,
    private val artistValidation: CreateArtistValidation,
    private val auth: AuthRepository
): ViewModel() {

    private val _alertDialogState = MutableStateFlow(AlertDialogState())
    val alertDialogState = _alertDialogState.asStateFlow()

    private val _state = MutableStateFlow(CreateArtistState())
    val state = _state.asStateFlow()


    fun onEvent(event: CreateArtistEvent){
        when(event){
            is CreateArtistEvent.OnArtistNameChange -> {
                if(!state.value.loading){
                    _state.update { it.copy(artistName = event.artistName) }
                }
            }
            is CreateArtistEvent.OnAddArtistClick -> { addArtistToFirestore() }
            is CreateArtistEvent.OnImageChange -> {
                if(!state.value.loading) {
                    _state.update { it.copy(artistImage = event.artistImage) }
                }

            }
            is CreateArtistEvent.OnDismissAlertDialog -> {
                _alertDialogState.update { it.copy(show = false) }
            }

            is CreateArtistEvent.OnModifyArtist -> {
                viewModelScope.launch {
                    val artist = artistRepository.artists.first().find { it.id == event.artistId }!!
                    _state.update { it.copy(artistName = artist.name, artistImage = artist.imageLink.toUri(), artistToModify = artist)}
                }
            }
            else -> Unit
        }
    }

    fun addArtistToFirestore(){
        _state.update { it.copy(loading = true) }

        artistValidation.execute(state.value.artistName, state.value.artistImage).onError { error->
            _alertDialogState.update {
                it.copy(show = true, message = error.toErrorMessage())
            }
            _state.update { it.copy(artistNameError = null, artistImageError = null) }

            when (error) {
                InputError.CreateArtist.ENTER_ARTIST_NAME -> {
                    _state.update { it.copy(artistNameError = error.toErrorMessage()) }
                }
                InputError.CreateArtist.ARTIST_NAME_TOO_LONG -> {
                    _state.update { it.copy(artistNameError = error.toErrorMessage()) }
                }
                InputError.CreateArtist.SELECT_ARTIST_IMAGE -> {
                    _state.update { it.copy(artistImageError = error.toErrorMessage()) }
                }
            }

            _state.update { it.copy(loading = false) }
            return
        }

        if(state.value.artistToModify != null){
            val oldArtist = state.value.artistToModify!!
            var artist = oldArtist.copy(name = state.value.artistName)
            if(oldArtist.imageLink.toUri() != state.value.artistImage){
                val fileName = "artist_images/${state.value.artistImage!!.lastPathSegment}"
                viewModelScope.launch {
                    storageRepository.addToStorage(
                        file = state.value.artistImage!!,
                        path = fileName
                    ).onError { error ->
                        _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }
                        _state.update { it.copy(loading = false) }
                        return@launch
                    }.onSuccess {
                        try {
                            storageRepository.removeFromStorage(oldArtist.imagePath)
                        }catch (_: Exception){

                        }

                        artist = artist.copy(imageLink = it, imagePath = fileName)

                        viewModelScope.launch {
                            artistRepository.updateArtist(artist).onError { error ->
                                _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }
                                _state.update { it.copy(loading = false) }
                                return@launch
                            }.onSuccess {
                                _state.update { it.copy(loading = false) }
                                _alertDialogState.update {
                                    it.copy(
                                        show = true,
                                        message = UiText.StringResource(R.string.artist_added_successfully),
                                        warning = false)
                                }
                            }
                        }
                    }
                }
                return
            }
            viewModelScope.launch {
                artistRepository.updateArtist(artist).onError { error ->
                    _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }
                    _state.update { it.copy(loading = false) }
                    return@launch
                }.onSuccess {
                    _state.update { it.copy(loading = false) }
                    _alertDialogState.update {
                        it.copy(
                            show = true,
                            message = UiText.StringResource(R.string.artist_added_successfully),
                            warning = false)
                    }
                }
            }
            return
        }

        val fileName = "artist_images/${state.value.artistImage!!.lastPathSegment}"

        var artist = Artist()
        viewModelScope.launch {
            storageRepository.addToStorage(
                file = state.value.artistImage!!,
                path = fileName
            ).onError { error ->
                _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }
                _state.update { it.copy(loading = false) }
                return@launch
            }.onSuccess {
                artist = createArtist(imageUri = it, imagePath = fileName)

            }
            artistRepository.createArtist(artist).onError { error ->
                _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }
                _state.update { it.copy(loading = false) }
                return@launch
            }.onSuccess {
                _state.update { it.copy(loading = false) }
                _alertDialogState.update {
                    it.copy(
                        show = true,
                        message = UiText.StringResource(R.string.artist_added_successfully),
                        warning = false)
                }
            }
        }
    }

    fun createArtist(imageUri: String, imagePath: String): Artist{
        return Artist(
            name = state.value.artistName,
            imageLink = imageUri,
            imagePath = imagePath,
            creator = auth.getCurrentUserId()
        )
    }


}