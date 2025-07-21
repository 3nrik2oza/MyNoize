package com.project.mynoize.activities.main.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.project.mynoize.R

@Composable
fun PlayButton(exoPlayer : ExoPlayer){

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

    Button(
        modifier = Modifier.size(51.dp),
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
}