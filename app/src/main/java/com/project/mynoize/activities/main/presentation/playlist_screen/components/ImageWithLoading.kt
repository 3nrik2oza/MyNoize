package com.project.mynoize.activities.main.presentation.playlist_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.project.mynoize.R

@Composable
fun ImageWithLoading(image: String, isFavorite: Boolean = false, boxSize: Dp = 150.dp){

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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(boxSize)
                    .border(1.dp, Color.Black)
            ){
                if(isFavorite){
                    Icon(
                        painter = painterResource(R.drawable.ic_heart),
                        contentDescription = "Playlist icon",
                        modifier = Modifier.size(40.dp)
                    )
                }else{
                    Icon(
                        imageVector =  Icons.Default.BrokenImage,
                        contentDescription = "Error loading image"
                    )
                }

            }
        }
        else -> {
            Image(
                painter = painter,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

}