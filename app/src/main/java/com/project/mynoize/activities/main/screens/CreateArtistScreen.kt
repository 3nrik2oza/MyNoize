package com.project.mynoize.activities.main.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.mynoize.activities.main.events.CreateArtistEvent
import com.project.mynoize.activities.main.viewmodels.CreateArtistViewModel
import com.project.mynoize.activities.signin.ui.CustomAlertDialog
import com.project.mynoize.activities.signin.ui.CustomButton
import com.project.mynoize.activities.signin.ui.CustomTextField

@Composable
fun CreateArtistScreen(
    vm: CreateArtistViewModel,
    navController: NavController
){

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            vm.onEvent(CreateArtistEvent.OnImageChange(it.toString()))
        }
    )

    if(vm.showAlertDialog){
        CustomAlertDialog(
            onDismiss = {
                if(vm.messageText == "Artist added successfully"){
                    navController.popBackStack()
                }else{
                    vm.onEvent(CreateArtistEvent.OnDismissAlertDialog)
                }

            },
            message = vm.messageText
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 15.dp),
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
            vm.artistName,
            onValueChange = {
                vm.onEvent(CreateArtistEvent.OnArtistNameChange(it))
            }
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

