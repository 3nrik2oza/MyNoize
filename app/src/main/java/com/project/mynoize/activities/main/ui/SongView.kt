package com.project.mynoize.activities.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.project.mynoize.R

@Composable
fun SongView(
    exoPlayer: ExoPlayer,
    songName: String = "",
    songArtist: String = "",
    imageUrl: String = "",
    modifier: Modifier = Modifier
){
    Column(modifier.fillMaxWidth().background(Color.DarkGray), Arrangement.Top, Alignment.CenterHorizontally) {




        AsyncImage(
            model = imageUrl,
            contentDescription = "Image",
            Modifier.size(320.dp).padding(top = 50.dp)
        )


        Text(songName, modifier.padding(start = 12.dp, top = 12.dp, bottom = 1.dp), fontWeight = Bold, fontSize = 21.sp)

        Text(songArtist, Modifier.padding(start = 12.dp, top = 8.dp, bottom = 1.dp), fontSize = 15.sp)

        Slider(
            value = 0.5f,
            onValueChange = {},
            valueRange = 0f..1f
        )

        SongViewButtons(exoPlayer)

    }
}

@Composable
fun SongViewButtons(exoPlayer: ExoPlayer){
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying_: Boolean)
            {
                isPlaying = isPlaying_
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    Row(Modifier.padding(top = 15.dp)){
        Button(
            modifier = Modifier.size(39.dp),
            contentPadding = PaddingValues(5.dp),
            onClick = {

            }) {
            Icon(painterResource(R.drawable.ic_prev), contentDescription = null, Modifier.fillMaxSize())

        }

        Spacer(Modifier.width(24.dp))

        Button(
            modifier = Modifier.size(45.dp),
            contentPadding = PaddingValues(5.dp),
            onClick = {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
                isPlaying = exoPlayer.isPlaying
            }) {
            if(isPlaying){
                Icon(painterResource(R.drawable.ic_pause), contentDescription = null, Modifier.fillMaxSize())
            }else{
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, Modifier.fillMaxSize())
            }
        }

        Spacer(Modifier.width(24.dp))

        Button(
            modifier = Modifier.size(39.dp),
            contentPadding = PaddingValues(5.dp),
            onClick = {

            }) {
            Icon(painterResource(R.drawable.ic_next), contentDescription = null, Modifier.fillMaxSize())

        }

    }
}