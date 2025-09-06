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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.mynoize.activities.main.presentation.create_song.CreateAlbumEvent
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.domain.InputError
import com.project.mynoize.core.presentation.asString

@Composable
fun CreateAlbumAlertDialog(
    onEvent: (CreateAlbumEvent) -> Unit,
    createAlbumState: AlertDialogState,
){

    var imageUri by remember { mutableStateOf("") }
    var albumName by remember { mutableStateOf("") }

    var albumNameError by remember { mutableStateOf(false) }

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
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(imageVector = Icons.Default.Create, contentDescription = "")
                Text(
                    text = "Add new Album",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
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
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                        .clickable{
                            if(!createAlbumState.loading){
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(10.dp))

                if(createAlbumState.loading){
                    CircularProgressIndicator()
                }else{
                    CustomTextField(
                        title = "Album name",
                        hintText = "Enter album name...",
                        inputValue = albumName,
                        onValueChange = {
                            albumName = it
                        },
                        isError = albumNameError
                    )

                    CustomButton(
                        text = "ADD",
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
                            albumNameError = false
                            onEvent(CreateAlbumEvent.OnCreateAlbum(
                                imageUri = imageUri,
                                albumName = albumName)
                            )
                        }
                    )
                }


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