package com.project.mynoize.activities.main.presentation.create_song

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_song.components.CreateAlbumAlertDialog
import com.project.mynoize.activities.main.ui.CustomDropdown
import com.project.mynoize.activities.main.ui.CustomDropdownMulti
import com.project.mynoize.activities.main.ui.CustomSelectFileButton
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.core.presentation.toErrorMessage
import com.project.mynoize.util.Era
import com.project.mynoize.util.Genre
import com.project.mynoize.util.Language
import com.project.mynoize.util.Mood
import com.project.mynoize.util.SubGenre


@Composable
fun CreateSongScreen(
    state: CreateSongState,
    alertDialogState: AlertDialogState,
    createAlbumDialogState: AlertDialogState,
    onEvent: (CreateSongEvent) -> Unit,
    onCreateAlbumEvent: (CreateAlbumEvent) -> Unit,
){
    val localFocusManager = LocalFocusManager.current

    var showArtistDropdown by rememberSaveable { mutableStateOf(false) }
    var showAlbumDropdown by rememberSaveable { mutableStateOf(false) }
    var showGenreDropdown by rememberSaveable { mutableStateOf(false) }
    var showSubgenreDropdown by rememberSaveable { mutableStateOf(false) }
    var showLanguageDropdown by rememberSaveable { mutableStateOf(false) }
    var showEraDropdown by rememberSaveable { mutableStateOf(false) }
    var showMoodDropdown by rememberSaveable { mutableStateOf(false) }
    
    val closeDropdowns = {
        showArtistDropdown = false
        showAlbumDropdown = false
        showGenreDropdown = false
        showSubgenreDropdown = false
        showLanguageDropdown = false
        showEraDropdown = false
    }

    val context = LocalContext.current

    val songPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            onEvent(CreateSongEvent.OnSelectSongClick(
                context = context,
                songUri = it.toString()
            ))
        }
    )

    if(state.showCreateAlbum){
        CreateAlbumAlertDialog(
            onEvent = onCreateAlbumEvent,
            createAlbumState = createAlbumDialogState
        )
    }

    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                if(alertDialogState.message is UiText.StringResource &&
                    alertDialogState.message.id == R.string.song_added_successfully
                ){
                    onEvent(CreateSongEvent.OnBackClick)
                }else{
                    onEvent(CreateSongEvent.OnDismissAlertDialog)
                }

            },
            message = alertDialogState.message?.asString() ?: "",
            warning = alertDialogState.warning
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(top=15.dp, start = 15.dp, end = 15.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                    closeDropdowns()
                })
            },
        horizontalAlignment = CenterHorizontally
    ) {

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.CenterStart
        ){

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                Modifier.size(35.dp)
                    .clickable{
                        onEvent(CreateSongEvent.OnBackClick)
                    }
            )

            Text(
                text = "Add new song",
                fontFamily = LatoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }



        CustomTextField(
            title = "Song name",
            hintText = "Your song name",
            inputValue = state.songName,
            onValueChange = {
                onEvent(CreateSongEvent.OnSongNameChange(it))
            },
            isError = state.nameError,
            enabled = !alertDialogState.loading,
            errorMessage =  state.error?.toErrorMessage()?.asString() ?: ""
        )

        CustomDropdown(
            itemList = state.artistList,
            hint = "Select Artist",
            title = "Artist",
            selectedItem = state.selectedArtist,
            onItemClick = {
                onEvent(CreateSongEvent.OnArtistClick(it))
            },
            displayText = { it.name },
            isError = state.error == InputError.CreateSong.SELECT_ARTIST,
            showDropdown = showArtistDropdown,
            onToggleShow = { showArtistDropdown = !showArtistDropdown },
            enabled = !alertDialogState.loading,
            errorMessage = state.error?.toErrorMessage()?.asString() ?: ""
        )


        Spacer(modifier = Modifier.height(10.dp))

        if(state.selectedArtist != null){
            CustomDropdown(
                itemList = state.albumList,
                hint = "Select Album",
                title = "Album",
                selectedItem = state.selectedAlbum,
                onItemClick = {
                    if(!alertDialogState.loading){
                        onEvent(CreateSongEvent.OnAlbumClick(it))
                    }
                },
                displayText = { it.name },
                onAddClick = {
                    onEvent(CreateSongEvent.OnAddAlbumClick)
                },
                isError = state.error == InputError.CreateSong.SELECT_ALBUM,
                showDropdown = showAlbumDropdown,
                onToggleShow = { showAlbumDropdown = !showAlbumDropdown },
                addNew = true,
                enabled = !alertDialogState.loading,
                errorMessage = state.error?.toErrorMessage()?.asString() ?: ""

            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomDropdown(
            itemList = Genre.entries,
            hint = "Select a Genre",
            title = "Genre",
            selectedItem = state.songGenre,
            onItemClick = {
                onEvent(CreateSongEvent.OnGenreClick(it))
            },
            displayText = { it.displayName },
            isError = state.error == InputError.CreateSong.SELECT_GENRE,
            showDropdown = showGenreDropdown,
            onToggleShow = { showGenreDropdown = !showGenreDropdown },
            enabled = !alertDialogState.loading,
            errorMessage = state.error?.toErrorMessage()?.asString() ?: ""
        )

        Spacer(modifier = Modifier.height(10.dp))

        if(state.songGenre != null){
            CustomDropdown(
                itemList = SubGenre.forGenre(state.songGenre),
                hint = "Select Subgenre",
                title = "Subgenre",
                selectedItem = state.songSubgenre,
                onItemClick = {
                    onEvent(CreateSongEvent.OnSubgenreClick(it))
                },
                displayText = { it.displayName },
                isError = state.error == InputError.CreateSong.SELECT_SUBGENRE,
                showDropdown = showSubgenreDropdown,
                onToggleShow = { showSubgenreDropdown = !showSubgenreDropdown },
                enabled = !alertDialogState.loading,
                errorMessage = state.error?.toErrorMessage()?.asString() ?: ""
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        CustomDropdown(
            itemList = Language.entries,
            hint = "Select language",
            title = "Language",
            selectedItem = state.language,
            onItemClick = { onEvent(CreateSongEvent.OnLanguageClick(it)) },
            displayText = { it.displayName },
            isError = state.error == InputError.CreateSong.SELECT_LANGUAGE,
            showDropdown = showLanguageDropdown,
            onToggleShow = { showLanguageDropdown = !showLanguageDropdown },
            enabled = !alertDialogState.loading,
            errorMessage = state.error?.toErrorMessage()?.asString() ?: ""
        )

        Spacer(modifier = Modifier.height(10.dp))
        
        CustomDropdown(
            itemList = Era.entries,
            hint = "Select era",
            title = "Era",
            selectedItem = state.era,
            onItemClick = { onEvent(CreateSongEvent.OnEraClick(it)) },
            displayText = { it.displayName },
            isError = state.error == InputError.CreateSong.SELECT_ERA,
            showDropdown = showEraDropdown,
            onToggleShow = { showEraDropdown = !showEraDropdown  },
            enabled = !alertDialogState.loading,
            errorMessage = state.error?.toErrorMessage()?.asString() ?: ""
        )

        CustomDropdownMulti(
            itemList = Mood.entries,
            hint = "Select mood/moods",
            title = "Mood",
            selectedItems = state.moods,
            onItemClick = { onEvent(CreateSongEvent.OnMoodClick(it)) },
            displayText = { it.displayName },
            isError = false,
            showDropdown = showMoodDropdown,
            onToggleShow = { showMoodDropdown = !showMoodDropdown },
            enabled = !alertDialogState.loading,
            errorMessage = ""
        )

        CustomSelectFileButton(
            text = state.songTitle,
            onClick = {
                songPickerLauncher.launch("audio/*")
            },
            isError = state.error == InputError.CreateSong.SELECT_SONG_FILE,
            enabled = !alertDialogState.loading,
            errorMessage = state.error?.toErrorMessage()?.asString() ?: ""
        )

        CustomButton(
            text = "ADD SONG",
            enabled = !alertDialogState.loading,
            onClick = {
                onEvent(CreateSongEvent.OnAddSongClick)
            }
        )

        Spacer(Modifier.height(100.dp))
    }
}

@Preview
@Composable
fun CreateSongScreenPreview(){
    CreateSongScreen(
        state = CreateSongState(),
        alertDialogState = AlertDialogState(),
        createAlbumDialogState = AlertDialogState(),
        onEvent = {},
        onCreateAlbumEvent = {})

}


