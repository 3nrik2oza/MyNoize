package com.project.mynoize.activities.main.presentation.create_playlist

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.create_playlist.domain.models.Tag
import com.project.mynoize.activities.main.ui.CustomDropdownMulti
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.Surrounding

@Composable
fun CreatePlaylistScreen(
    alertDialogState: AlertDialogState,
    state: CreatePlaylistState,
    onEvent: (CreatePlaylistEvent) -> Unit,
){
    val localFocusManager = LocalFocusManager.current

    var showMoodDropdown by rememberSaveable { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            onEvent(CreatePlaylistEvent.OnImageChange(it))
        }
    )

    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                if(alertDialogState.warning){

                    onEvent(CreatePlaylistEvent.OnDismissAlertDialog)
                }else{
                    onEvent(CreatePlaylistEvent.OnBackClick)
                }

            },
            message = alertDialogState.message?.asString() ?: "",
            warning = alertDialogState.warning
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(horizontal = 25.dp, vertical = 50.dp)
        .pointerInput(Unit){
            detectTapGestures(onTap = {
                localFocusManager.clearFocus()
            })
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.CenterStart
        ){

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                Modifier.size(35.dp)
                    .clickable{
                        onEvent(CreatePlaylistEvent.OnBackClick)
                    }
            )

            Text(
                text = "CREATE PLAYLIST",
                fontFamily = LatoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

        }


        Spacer(Modifier.height(20.dp))

        Surrounding(
            isError = state.playlistImageError != null,
            errorMessage = state.playlistImageError?.asString() ?: ""
        ) {

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                if(state.playlistImage == null){


                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                            .clip(RectangleShape)
                            .border(1.dp, Color.Black)
                            .clickable{
                                if(!state.loading){
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                    ){
                        Image(
                            painter = painterResource(R.drawable.signer),
                            contentDescription = "Playlist Image placeholder",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                        )
                    }


                }else {
                    AsyncImage(
                        model = state.playlistImage,
                        contentDescription = "Playlist image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(160.dp)
                            .clip(RectangleShape)
                            .border(1.dp, Color.Black)
                            .clickable{
                                if(!state.loading){
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                    )
                }
            }

        }



        Spacer(Modifier.height(20.dp))

        CustomTextField(
            title = "Playlist name",
            hintText = "Enter playlist name...",
            inputValue = state.playlistName,
            onValueChange = {
                onEvent(CreatePlaylistEvent.OnPlaylistNameChange(it))
            },
            enabled = !state.loading,
            isError =  state.playlistNameError != null,
            errorMessage = state.playlistNameError?.asString() ?: ""
        )

        CustomDropdownMulti(
            itemList = Tag.entries,
            hint = "Select tag/tags",
            title = "Tags",
            selectedItems = state.tags,
            onItemClick = { onEvent(CreatePlaylistEvent.OnTagSelected(it)) },
            displayText = { it.displayName },
            isError = state.tagsError != null,
            enabled = !state.loading,
            showDropdown = showMoodDropdown,
            onToggleShow = { showMoodDropdown = !showMoodDropdown },
            errorMessage = state.tagsError?.asString() ?: ""
        )

        Spacer(modifier = Modifier.height(10.dp))

        Spacer(Modifier.height(50.dp))

        CustomButton(
            text = "ADD",
            enabled = !state.loading,
            onClick = {
                onEvent(CreatePlaylistEvent.OnAddPlaylistClick)
            }
        )

    }
}

@Preview
@Composable
fun CreatePlaylistScreenPreview(){
    CreatePlaylistScreen(
        onEvent = {},
        alertDialogState = AlertDialogState(),
        state = CreatePlaylistState()
    )

}


