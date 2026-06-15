package com.project.mynoize.activities.main.presentation.create_artist



import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_artist.domain.CreateArtistValidation
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.toErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                    artistRepository.getArtist(event.artistId).onSuccess { artist ->
                        _state.update { it.copy(
                            artistName = artist.name,
                            artistImage = artist.imageLink.toUri(),
                            country = artist.country,
                            artistToModify = artist)}
                    }

                }
            }
            is CreateArtistEvent.OnArtistGenreChange -> {
                _state.update { it.copy(genre = event.selected) }
            }
            is CreateArtistEvent.OnArtistCountryChange -> _state.update { it.copy(country = event.selected) }
            is CreateArtistEvent.OnBackClick -> {}
        }
    }

    fun addArtistToFirestore(){
        _state.update { it.copy(loading = true) }
        val state = _state.value

        artistValidation.execute(state.artistName, state.artistImage, state.country).onError { error->
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

                InputError.CreateArtist.SELECT_ARTIST_COUNTRY -> {
                    _state.update { it.copy(countryError = error.toErrorMessage()) }
                }
            }

            _state.update { it.copy(loading = false) }
            return
        }

        if(state.artistToModify != null){
            val oldArtist = state.artistToModify
            var artist = oldArtist.copy(name = state.artistName, genre = state.genre ?: oldArtist.genre)
            if(oldArtist.imageLink.toUri() != state.artistImage){
                val fileName = "artist_images/${state.artistImage!!.lastPathSegment}"
                viewModelScope.launch {
                    storageRepository.addToStorage(
                        file = state.artistImage,
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

                        artist = artist.copy(imageLink = it.downloadLink, imagePath = it.path)

                        viewModelScope.launch {
                            artistRepository.updateArtist(artist).onError { error ->
                                _alertDialogState.update { state -> state.copy(show = true, message = error.toErrorMessage()) }
                                _state.update { state ->  state.copy(loading = false) }
                                return@launch
                            }.onSuccess {
                                _state.update { state -> state.copy(loading = false) }
                                _alertDialogState.update { state ->
                                    state.copy(
                                        show = true,
                                        message = UiText.StringResource(R.string.artist_added_successfully),
                                        warning = false
                                    )
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

        val fileName = "artist_images/${state.artistImage!!.lastPathSegment}"

        var artist = Artist()
        viewModelScope.launch {
            storageRepository.addToStorage(
                file = state.artistImage,
                path = fileName
            ).onError { error ->
                _alertDialogState.update { it.copy(show = true, message = error.toErrorMessage()) }
                _state.update { it.copy(loading = false) }
                return@launch
            }.onSuccess {
                artist = createArtist(imageUri = it.downloadLink, imagePath = it.path)

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
        val state = state.value
        return Artist(
            name = state.artistName,
            imageLink = imageUri,
            imagePath = imagePath,
            country = state.country!!,
            genre = state.genre,
            creator = auth.getCurrentUserId()
        )
    }


}