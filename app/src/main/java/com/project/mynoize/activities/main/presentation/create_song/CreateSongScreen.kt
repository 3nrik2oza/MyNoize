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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.project.mynoize.activities.main.state.ListOfState
import com.project.mynoize.activities.main.ui.CustomDropdown
import com.project.mynoize.activities.main.ui.CustomSelectFileButton
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.util.genres
import com.project.mynoize.util.subGenreMap


@Composable
fun CreateSongScreen(
    createSongState: CreateSongState,
    alertDialogState: AlertDialogState,
    createAlbumDialogState: AlertDialogState,
    albumListState: ListOfState<Album>,
    artistListState: ListOfState<Artist>,
    onEvent: (CreateSongEvent) -> Unit,
    onCreateAlbumEvent: (CreateAlbumEvent) -> Unit,
){
    val localFocusManager = LocalFocusManager.current

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

    if(createSongState.showCreateAlbum){
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
            inputValue = createSongState.songName,
            onValueChange = {
                if(!alertDialogState.loading){
                    onEvent(CreateSongEvent.OnSongNameChange(it))
                }
            },
            isError = createSongState.songNameError != null,
            errorMessage = createSongState.songNameError?.asString() ?: ""
        )

        CustomDropdown(
            itemList = artistListState.list.map { it.name },
            hint = "Select Artist",
            title = "Artist",
            selectedIndex = artistListState.index,
            onItemClick = {
                if(!alertDialogState.loading){
                    onEvent(CreateSongEvent.OnArtistClick(it))
                }
            },
            isError = artistListState.listError != null,
            errorMessage = artistListState.listError?.asString() ?: ""
        )


        Spacer(modifier = Modifier.height(10.dp))

        if(artistListState.index != -1){
            CustomDropdown(
                itemList = albumListState.list.map {it.name},
                hint = "Select Album",
                title = "Album",
                selectedIndex = albumListState.index,
                onItemClick = {
                    if(!alertDialogState.loading){
                        onEvent(CreateSongEvent.OnAlbumClick(it))
                    }
                },
                canAdd = true,
                onAddClick = {
                    if(!alertDialogState.loading){
                        onEvent(CreateSongEvent.OnAddAlbumClick)
                    }
                },
                isError = albumListState.listError != null,
                errorMessage = albumListState.listError?.asString() ?: ""

            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomDropdown(
            itemList = genres,
            hint = "Select Genre",
            title = "Genre",
            selectedIndex = createSongState.songGenre,
            onItemClick = {
                if(!alertDialogState.loading){
                    onEvent(CreateSongEvent.OnGenreClick(it))
                }
            },
            isError = createSongState.songGenreError != null,
            errorMessage = createSongState.songGenreError?.asString() ?: ""
        )

        Spacer(modifier = Modifier.height(10.dp))

        if(createSongState.songGenre != -1){
            CustomDropdown(
                itemList = subGenreMap[genres[createSongState.songGenre]]!!,
                hint = "Select Subgenre",
                title = "Subgenre",
                selectedIndex = createSongState.songSubgenre,
                onItemClick = {
                    if(!alertDialogState.loading){
                        onEvent(CreateSongEvent.OnSubgenreClick(it))
                    }
                },
                isError = createSongState.songSubgenreError != null,
                errorMessage = createSongState.songSubgenreError?.asString() ?: ""
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        if(!alertDialogState.loading){
            CustomSelectFileButton(
                text = createSongState.songTitle,
                onClick = {
                    songPickerLauncher.launch("audio/*")
                },
                isError = createSongState.songUriError != null,
                errorMessage = createSongState.songUriError?.asString() ?: ""
            )

            CustomButton(
                text = "ADD SONG",
                onClick = {
                    onEvent(CreateSongEvent.OnAddSongClick)
                }
            )
        }else{
            CircularProgressIndicator()
        }


        Spacer(Modifier.height(100.dp))
    }
}

@Preview
@Composable
fun CreateSongScreenPreview(){
    CreateSongScreen(
        createSongState = CreateSongState(),
        alertDialogState = AlertDialogState(),
        createAlbumDialogState = AlertDialogState(),
        albumListState = ListOfState(),
        artistListState = ListOfState(),
        onEvent = {},
        onCreateAlbumEvent = {})

}


