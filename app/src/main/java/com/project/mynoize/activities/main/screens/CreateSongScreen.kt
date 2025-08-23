package com.project.mynoize.activities.main.screens


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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.mynoize.activities.main.events.CreateSongEvent
import com.project.mynoize.activities.main.viewmodels.CreateSongViewModel
import com.project.mynoize.activities.main.ui.CreateAlbumAlertDialog
import com.project.mynoize.activities.main.ui.CustomDropdown
import com.project.mynoize.activities.main.ui.CustomSelectFileButton
import com.project.mynoize.activities.signin.ui.CustomButton
import com.project.mynoize.activities.signin.ui.CustomTextField
import com.project.mynoize.activities.signin.ui.MessageAlertDialog
import com.project.mynoize.util.Constants


@Composable
fun CreateSongScreen(
    vm: CreateSongViewModel,
    context: android.content.Context,
    navController: NavHostController
){
    val localFocusManager = LocalFocusManager.current

    val songPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            vm.onEvent(CreateSongEvent.OnSelectSongClick(context, it.toString()))
        }
    )

    if(vm.createAlbum){
        CreateAlbumAlertDialog(
            onDismiss = {
                vm.createAlbum = false
            },
            createAlbum = {imageUri, albumName ->
                vm.onEvent(CreateSongEvent.OnCreateAlbumClick(imageUri, albumName))
            },
            showMessage = vm.showAlertDialogCreateAlbum,
            message = vm.messageTextCreateAlbum,
            loading = vm.loadingCreatingAlbum
        )
    }

    if(vm.showAlertDialog){
        MessageAlertDialog(
            onDismiss = {
                if(vm.messageText == Constants.SONG_ADDED_SUCCESSFULLY){
                    navController.popBackStack()
                }else{
                    vm.showAlertDialog = false
                }

            },
            message = vm.messageText
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
            "Song name",
            "Your song name",
            vm.songName,
            onValueChange = {
                if(!vm.loading){
                    vm.onEvent(CreateSongEvent.OnSongNameChange(it))
                }
            }
        )

        CustomDropdown(
            itemList = vm.listOfArtists.value,
            title = "Artist",
            selectedIndex = vm.artistIndex,
            onItemClick = {
                if(!vm.loading){
                    vm.onEvent(event = CreateSongEvent.OnArtistClick(it))
                }

            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if(vm.artistSelected.value){
            CustomDropdown(
                itemList = vm.listOfAlbums.value,
                title = "Album",
                selectedIndex = vm.albumIndex,
                onItemClick = {
                    if(!vm.loading){
                        vm.onEvent(event = CreateSongEvent.OnAlbumClick(it))
                    }
                },
                canAdd = true,
                onAddClick = {
                    if(!vm.loading){
                        vm.onEvent(CreateSongEvent.OnAddAlbumClick)
                    }

                }

            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        if(!vm.loading){
            CustomSelectFileButton(
                text = vm.songTitle,
                onClick = {
                    songPickerLauncher.launch("audio/*")
                }
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

