package com.project.mynoize.activities.main.ui

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
import androidx.compose.runtime.MutableState
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
import com.project.mynoize.activities.signin.ui.CustomButton
import com.project.mynoize.activities.signin.ui.CustomTextField
import com.project.mynoize.activities.signin.ui.MessageAlertDialog

@Composable
fun CreateAlbumAlertDialog(
    createAlbum: (imageUri: String, albumName: String)-> Unit,
    onDismiss: () -> Unit,
    loading: Boolean,
    showMessage: MutableState<Boolean>,
    message: MutableState<String>
){

    var imageUri by remember { mutableStateOf("") }
    var albumName by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            imageUri = it.toString()
        }
    )

    if(showMessage.value){
        MessageAlertDialog(
            onDismiss = {
                showMessage.value = false
            },
            message = message.value
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                            if(!loading){
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.height(10.dp))

                if(loading){
                    CircularProgressIndicator()
                }else{
                    CustomTextField(
                        title = "Album name",
                        hintText = "Enter album name...",
                        albumName,
                        onValueChange = {
                            albumName = it
                        }
                    )

                    CustomButton(
                        text = "ADD",
                        onClick = {
                            if (imageUri == "") {
                                message.value = "Please select image"
                                showMessage.value = true
                                return@CustomButton
                            }
                            if (albumName == "") {
                                message.value = "Please enter album name"
                                showMessage.value = true
                                return@CustomButton
                            }
                            createAlbum(imageUri, albumName)
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
        onDismiss = {},
        createAlbum = {_,_->},
        showMessage = remember { mutableStateOf(false) },
        message = remember { mutableStateOf("") },
        loading = false
    )

}