package com.project.mynoize.activities.main.presentation.music_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        ) {/*
            items(10){
                PlaylistScrollElement(
                    playlist = Playlist(),
                    onEvent = {}
                )
            }*/
        }

        /* Row with playlists create for user*/

        Spacer(Modifier.size(25.dp))

        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text="Playlists that you might like")

        LazyRow(
            modifier = Modifier.height(200.dp)
        ) {/*
            items(10){
                PlaylistScrollElement(
                    playlist = Playlist(),
                    onEvent = {}
                )
            }*/
        }

        /* Row with playlists that you might like*/

        Spacer(Modifier.size(25.dp))

        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text="Trending")

        LazyRow(
            modifier = Modifier.height(200.dp)
        ) {/*
            items(10){
                PlaylistScrollElement(
                    playlist = Playlist(),
                    onEvent = {}
                )
            }*/
        }
    }

}



@Preview(showBackground = true)
@Composable
fun ShowMusicScreen(){
    MusicScreen()
}