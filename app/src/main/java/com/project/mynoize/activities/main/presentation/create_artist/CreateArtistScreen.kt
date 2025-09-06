package com.project.mynoize.activities.main.presentation.create_artist

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField

@Composable
fun CreateArtistScreen(
    vm: CreateArtistViewModel,
    navController: NavController
){

    val localFocusManager = LocalFocusManager.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            vm.onEvent(CreateArtistEvent.OnImageChange(it.toString()))
        }
    )

    if(vm.showAlertDialog){
        MessageAlertDialog(
            onDismiss = {
                if(vm.messageText == "Artist added successfully"){
                    navController.popBackStack()
                }else{
                    vm.onEvent(CreateArtistEvent.OnDismissAlertDialog)
                }

            },
            message = vm.messageText,
            warning = true //TODO: change this
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 15.dp)
        .pointerInput(Unit){
            detectTapGestures(onTap = {
                localFocusManager.clearFocus()
            })
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Spacer(Modifier.height(50.dp))
        AsyncImage(
            model = vm.artistImage,
            contentDescription = "Artist image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)
                .clickable{
                    if(!vm.loading){
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }

                }
        )

        Spacer(Modifier.height(20.dp))

        CustomTextField(
            title = "Artist name",
            hintText = "Enter artist name...",
            inputValue = vm.artistName,
            onValueChange = {
                vm.onEvent(CreateArtistEvent.OnArtistNameChange(it))
            },
            isError =  vm.artistNameError
        )

        Spacer(Modifier.height(250.dp))

        if(!vm.loading){
            CustomButton(
                text = "ADD",
                onClick = {
                    vm.onEvent(CreateArtistEvent.OnAddArtistClick)
                }
            )
        }else{
            CircularProgressIndicator()
        }



    }
}

