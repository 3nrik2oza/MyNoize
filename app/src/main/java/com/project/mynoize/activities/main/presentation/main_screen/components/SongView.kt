package com.project.mynoize.activities.main.presentation.main_screen.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.project.mynoize.activities.main.ui.PlayButton
import com.project.mynoize.core.data.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun SongView(
    exoPlayer: ExoPlayer,
    song: Song,
    onPrevSong: (Song) -> Unit,
    onNextSong: (Song) -> Unit,
    modifier: Modifier = Modifier
){
    var sliderPosition by remember { mutableFloatStateOf(exoPlayer.currentPosition / exoPlayer.duration.toFloat() ) }
    var duration by remember { mutableLongStateOf(0L) }


    val scope = rememberCoroutineScope()

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                duration = player.duration
            }
        }

        exoPlayer.addListener(listener)

        val job = scope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying && exoPlayer.duration > 0) {
                    sliderPosition = exoPlayer.currentPosition / exoPlayer.duration.toFloat()
                }
                delay(500)
            }
        }

        onDispose {
            exoPlayer.removeListener(listener)
            job.cancel()
        }
    }

    Column(modifier.fillMaxWidth().background(Color.DarkGray), Arrangement.Top, Alignment.CenterHorizontally) {

        AsyncImage(
            model = song.imageUrl,
            contentDescription = "Image",
            Modifier.size(320.dp).padding(top = 50.dp)
        )


        Text(song.title, modifier.padding(start = 12.dp, top = 12.dp, bottom = 1.dp), fontWeight = Bold, fontSize = 21.sp)

        Text(song.artistName, Modifier.padding(start = 12.dp, top = 8.dp, bottom = 1.dp), fontSize = 15.sp)

        Column {
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it},
                enabled = exoPlayer.duration > 0,
                onValueChangeFinished = { exoPlayer.seekTo((sliderPosition * exoPlayer.duration).toLong())},
                valueRange = 0f..1f,
                modifier = Modifier.padding(horizontal = 15.dp)
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =  Arrangement.SpaceBetween
            ){
                Text(formatMillis(exoPlayer.currentPosition), Modifier.padding(start = 12.dp))
                Text(formatMillis(exoPlayer.duration), Modifier.padding(end = 12.dp))
            }
        }


        SongViewButtons(exoPlayer, {onPrevSong(song)}, {onNextSong(song)})

    }
}

@Composable
fun SongViewButtons(exoPlayer: ExoPlayer, onPrevSong: () -> Unit, onNextSong: () -> Unit){
    Row(Modifier.padding(top = 15.dp), verticalAlignment = Alignment.CenterVertically){
        Button(
            modifier = Modifier.size(39.dp),
            contentPadding = PaddingValues(5.dp),
            onClick = {
                onPrevSong()
            }) {
            Icon(painterResource(R.drawable.ic_prev), contentDescription = null, Modifier.fillMaxSize())

        }

        Spacer(Modifier.width(24.dp))


        PlayButton(exoPlayer)

        Spacer(Modifier.width(24.dp))

        Button(
            modifier = Modifier.size(39.dp),
            contentPadding = PaddingValues(5.dp),
            onClick = {
                onNextSong()
            }) {
            Icon(painterResource(R.drawable.ic_next), contentDescription = null, Modifier.fillMaxSize())

        }

    }
}


fun formatMillis(ms: Long): String {
    if(ms < 0){
        return "%d:%02d".format(0, 0)
    }
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
