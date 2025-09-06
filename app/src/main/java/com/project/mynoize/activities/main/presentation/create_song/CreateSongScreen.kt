package com.project.mynoize.activities.main.presentation.create_song


import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_song.components.CreateAlbumAlertDialog
import com.project.mynoize.activities.main.ui.CustomDropdown
import com.project.mynoize.activities.main.ui.CustomSelectFileButton
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.UiText
import com.project.mynoize.core.presentation.asString


@Composable
fun CreateSongScreen(
    vm: CreateSongViewModel,
    context: Context,
    navController: NavHostController
){
    val localFocusManager = LocalFocusManager.current

    val createSongState by vm.createSongState.collectAsState()
    val alertDialogState by vm.alertDialogState.collectAsState()
    val createAlbumDialogState by vm.createAlbumDialogState.collectAsState()
    val albumListState by vm.albumListState.collectAsState()
    val artistListState by vm.artistListState.collectAsState()


    val songPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            vm.onEvent(CreateSongEvent.OnSelectSongClick(context, it.toString()))
        }
    )

    if(createSongState.showCreateAlbum){
        CreateAlbumAlertDialog(
            onEvent = vm::onCreateAlbumEvent,
            createAlbumState = createAlbumDialogState
        )
    }

    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                if(alertDialogState.message is UiText.StringResource &&
                    (alertDialogState.message as UiText.StringResource).id == R.string.song_added_successfully
                ){
                    navController.popBackStack()
                }else{
                    vm.onEvent(CreateSongEvent.OnDismissAlertDialog)
                }

            },
            message = alertDialogState.message?.asString() ?: "",
            warning = alertDialogState.warning
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top=15.dp, start = 15.dp, end = 15.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 23.sp,
            text = "Add new song"
        )

        CustomTextField(
            title = "Song name",
            hintText = "Your song name",
            inputValue = createSongState.songName,
            onValueChange = {
                if(!alertDialogState.loading){
                    vm.onEvent(CreateSongEvent.OnSongNameChange(it))
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
                    vm.onEvent(event = CreateSongEvent.OnArtistClick(it))
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
                        vm.onEvent(event = CreateSongEvent.OnAlbumClick(it))
                    }
                },
                canAdd = true,
                onAddClick = {
                    if(!alertDialogState.loading){
                        vm.onEvent(CreateSongEvent.OnAddAlbumClick)
                    }
                },
                isError = albumListState.listError != null,
                errorMessage = albumListState.listError?.asString() ?: ""

            )
        }

        Spacer(modifier = Modifier.height(25.dp))

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
                text = "Add song",
                onClick = {
                    vm.onEvent(CreateSongEvent.OnAddSongClick)
                }
            )
        }else{
            CircularProgressIndicator()
        }

    }
}

