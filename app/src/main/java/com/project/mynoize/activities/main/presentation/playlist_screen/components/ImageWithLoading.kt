package com.project.mynoize.activities.main.presentation.playlist_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImageWithLoading(image: String){

    val painter = rememberAsyncImagePainter(model = image)

    val painterState = painter.state
    when(painterState){
        is AsyncImagePainter.State.Loading ->{
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            CircularProgressIndicator()
        }
        is AsyncImagePainter.State.Error ->{
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "Error loading image"
            )
        }
        else -> {
            Image(
                painter = painter,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }

}