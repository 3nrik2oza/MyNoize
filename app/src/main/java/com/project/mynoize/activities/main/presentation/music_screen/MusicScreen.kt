package com.project.mynoize.activities.main.presentation.music_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun MusicScreen(){

    Column(
        Modifier.fillMaxSize()
            .padding(start = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 23.sp,
            text = "My Noize",
            modifier = Modifier.padding(top = 20.dp)
        )


        Spacer(Modifier.size(25.dp))

        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text="Playlists made for you")

        LazyRow(
            modifier = Modifier.height(200.dp)
        ) {
            items(10){
                PlaylistScrollElement()
            }
        }

        /* Row with playlists create for user*/

        Spacer(Modifier.size(25.dp))

        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text="Playlists that you might like")

        LazyRow(
            modifier = Modifier.height(200.dp)
        ) {
            items(10){
                PlaylistScrollElement()
            }
        }

        /* Row with playlists that you might like*/

        Spacer(Modifier.size(25.dp))

        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text="Trending")

        LazyRow(
            modifier = Modifier.height(200.dp)
        ) {
            items(10){
                PlaylistScrollElement()
            }
        }
    }

}

@Composable
fun PlaylistScrollElement(
    modifier: Modifier = Modifier.size(150.dp)
){
    Column (
        modifier = Modifier.padding(start= 10.dp, end=10.dp, top = 10.dp)
    ){
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = modifier
        ){
            AsyncImage(
                model="",
                contentDescription = "Playlist image",
                modifier = modifier
                    .border(2.dp, Color.Black)

            )

            Icon(
                imageVector = Icons.Default.PlayArrow, contentDescription = "Play button",
                modifier = Modifier
                    .padding(20.dp)
                    .background(Color.Cyan, CircleShape)
                    .padding(5.dp)
                    .size(18.dp)

            )

        }
        Text(
            fontSize = 12.sp,
            text = "Playlist name"
        )
    }

}


@Preview(showBackground = true)
@Composable
fun ShowMusicScreen(){
    MusicScreen()
}