package com.project.mynoize.activities.main.presentation.create_song.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.mynoize.activities.main.presentation.create_song.CreateAlbumEvent
import com.project.mynoize.activities.main.ui.CustomDropdown
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.util.Era

@Composable
fun CreateAlbumAlertDialog(
    onEvent: (CreateAlbumEvent) -> Unit,
    createAlbumState: AlertDialogState,
){

    var imageUri by remember { mutableStateOf("") }
    var albumName by remember { mutableStateOf("") }
    var era by remember { mutableStateOf<Era?>(null) }

    var albumNameError by remember { mutableStateOf(false) }
    var albumEraError by remember { mutableStateOf(false) }

    var showEraDropdown by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            imageUri = it.toString()
        }
    )

    if(createAlbumState.show){
        MessageAlertDialog(
            onDismiss = {

                onEvent(CreateAlbumEvent.OnDismissMessageDialog)
            },
            message = createAlbumState.message?.asString() ?: "",
            warning = createAlbumState.warning
        )
    }

    AlertDialog(
        onDismissRequest = {onEvent(CreateAlbumEvent.OnDismissCreateAlbumDialog)},
        confirmButton = {},
        modifier = Modifier,
        shape = RectangleShape,
        containerColor = Color.White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(imageVector = Icons.Default.Create, contentDescription = "")
                Text(
                    text = "Add new Album".uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LatoFontFamily
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                AsyncImage(
                    model = imageUri,
                    contentDescription = "Album image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RectangleShape)
                        .border(1.dp, Color.Black, RectangleShape)
                        .clickable{
                            if(!createAlbumState.loading){
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomTextField(
                    title = "Album name",
                    hintText = "Enter album name...",
                    inputValue = albumName,
                    onValueChange = {
                        albumName = it
                    },
                    enabled = !createAlbumState.loading,
                    isError = albumNameError
                )

                CustomDropdown(
                    itemList = Era.entries,
                    hint = "Select era",
                    title = "Era",
                    selectedItem = era,
                    onItemClick = { era = it },
                    displayText = { it.displayName },
                    isError = albumEraError,
                    showDropdown = showEraDropdown,
                    onToggleShow = { showEraDropdown = !showEraDropdown  },
                    enabled = !createAlbumState.loading,
                )

                CustomButton(
                    text = "ADD",
                    enabled = !createAlbumState.loading,
                    onClick = {
                        if (imageUri == "") {
                            onEvent(CreateAlbumEvent.OnShowAlertDialog(InputError.CreateAlbum.SELECT_IMAGE))
                            return@CustomButton
                        }
                        if (albumName == "") {
                            albumNameError = true
                            onEvent(CreateAlbumEvent.OnShowAlertDialog(InputError.CreateAlbum.ENTER_ALBUM_NAME))
                            return@CustomButton
                        }
                        if(albumName.length > 30){
                            albumNameError = true
                            onEvent(CreateAlbumEvent.OnShowAlertDialog(InputError.CreateAlbum.ALBUM_NAME_TOO_LONG))
                            return@CustomButton
                        }
                        if(era == null){
                            albumEraError = true
                            onEvent(CreateAlbumEvent.OnShowAlertDialog(InputError.CreateAlbum.SELECT_ERA))
                            return@CustomButton
                        }
                        albumNameError = false
                        onEvent(CreateAlbumEvent.OnCreateAlbum(
                            imageUri = imageUri,
                            albumName = albumName,
                            era = era!!
                        )
                        )
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreateAlbumAlertDialogPrev(){
    CreateAlbumAlertDialog(
        onEvent = {},
        createAlbumState = AlertDialogState()
    )

}