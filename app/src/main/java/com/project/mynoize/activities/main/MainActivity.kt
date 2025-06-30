package com.project.mynoize.activities.main

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.mynoize.activities.main.ui.theme.MyNoizeTheme
import com.project.mynoize.data.Song
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var list = mutableStateOf(listOf<Song>())

        val db = Firebase.firestore
        db.collection("songs").get()
            .addOnSuccessListener { result ->
                val updatedList = list.value.toMutableList()
                for (document in result) {
                    val song = document.toObject(Song::class.java)
                    updatedList.add(song)
                }
                list.value = updatedList
            }
            .addOnFailureListener { exception ->

            }

        setContent {
            MyNoizeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        list.value
                    )
                }
            }
        }
    }
}

@Composable
fun MainView(list: List<Song>) {

    Column (
        modifier = Modifier.fillMaxSize().padding(start = 5.dp, top = 25.dp),
        verticalArrangement = Arrangement.Top
    ){

        Text(
            text = "Your music",
            fontSize = 20.sp,
            fontWeight = Bold
        )

        Spacer(Modifier.height(24.dp))

        var currentSong by remember { mutableStateOf<Song?>(null)}

        LazyColumn(
        ){
            itemsIndexed(list) { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically){
                    Button(
                        onClick = {currentSong = item},
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        colors = ButtonColors(Color.Cyan, Color.Cyan, Color.Cyan, Color.Cyan)
                    ){

                    }

                    Column(
                        Modifier.padding(start = 10.dp)
                    ){
                        Text(item.title)
                        Spacer(Modifier.height(15.dp))
                        Text(item.subtitle)
                    }
                }
                Spacer(Modifier.height(15.dp))

            }
        }

        currentSong?.let{
            MusicPlayer(url = it.songUrl)
        }

    }
}

@Composable
fun MusicPlayer(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember {mutableStateOf(0L)}

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column (Modifier.padding(16.dp)){
        Button(onClick = {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            }else{
                exoPlayer.play()
            }
            isPlaying = !isPlaying
        }){
            Text(if (isPlaying) "Pause" else "Play")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Progress Slider
        Slider(
            value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
            onValueChange = { value ->
                val seekPos = (value * duration).toLong()
                exoPlayer.seekTo(seekPos)
                currentPosition = seekPos
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Time display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatMillis(currentPosition))
            Text(formatMillis(duration))
        }
    }

}

fun formatMillis(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyNoizeTheme {
    }
}