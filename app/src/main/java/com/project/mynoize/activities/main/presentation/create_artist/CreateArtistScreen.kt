package com.project.mynoize.activities.main.presentation.create_artist


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.mynoize.R
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.CustomButton
import com.project.mynoize.core.presentation.components.CustomTextField
import com.project.mynoize.core.presentation.components.Surrounding

@Composable
fun CreateArtistScreen(
    state: CreateArtistState,
    alertDialogState: AlertDialogState,
    onEvent: (CreateArtistEvent) -> Unit
){

    val localFocusManager = LocalFocusManager.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            onEvent(CreateArtistEvent.OnImageChange(it))
        }
    )


    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                if(alertDialogState.warning){

                    onEvent(CreateArtistEvent.OnDismissAlertDialog)
                }else{
                    onEvent(CreateArtistEvent.OnBackClick)
                }

            },
            message = alertDialogState.message?.asString() ?: "",
            warning = alertDialogState.warning
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
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
                onEvent(CreateArtistEvent.OnBackClick)
            }
    )

    Text(
        text = "ADD NEW ARTIST",
        fontFamily = LatoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

}


        Spacer(Modifier.height(20.dp))

        Surrounding(
            isError = state.artistImageError != null,
            errorMessage = state.artistImageError?.asString() ?: ""
        ) {

            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                if(state.artistImage == null){


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
                            contentDescription = "Artist image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                        )
                    }


                }else {
                    AsyncImage(
                        model = state.artistImage,
                        contentDescription = "Artist image",
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
            title = "Artist name",
            hintText = "Enter artist name...",
            inputValue = state.artistName,
            onValueChange = {
                onEvent(CreateArtistEvent.OnArtistNameChange(it))
            },
            isError =  state.artistNameError != null,
            errorMessage = state.artistNameError?.asString() ?: ""
        )

        Spacer(Modifier.height(50.dp))

        if(!state.loading){
            CustomButton(
                text = "ADD",
                onClick = {
                    onEvent(CreateArtistEvent.OnAddArtistClick)
                }
            )
        }else{
            CircularProgressIndicator()
        }



    }
}

