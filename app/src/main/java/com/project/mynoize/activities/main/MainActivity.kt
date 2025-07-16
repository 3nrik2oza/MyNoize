package com.project.mynoize.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.Disposable
import com.project.mynoize.R
import com.project.mynoize.activities.main.ui.SongView
import com.project.mynoize.activities.main.ui.theme.MyNoizeTheme
import com.project.mynoize.data.Song
import org.jetbrains.annotations.Async
import kotlin.math.round


class MainActivity : ComponentActivity() {
    val vm = MainScreenViewModel(this)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
            MyNoizeTheme {
                var currentSong by remember { mutableStateOf<Song?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainView(
                        songList = vm.songList.value,
                        currentSong = currentSong,
                        onSongClick = { song ->
                            currentSong = song
                            vm.playerManager.initializePlayer(song.songUrl)
                        },
                        exoPlayer = vm.playerManager.getPlayer()
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.playerManager.releasePlayer()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainView(
    songList: List<Song>,
    currentSong: Song?,
    onSongClick: (Song) -> Unit,
    exoPlayer: ExoPlayer?
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 25.dp),
    ) {
        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        val scaffoldState = rememberBottomSheetScaffoldState()
        Column (Modifier.padding(start = 12.dp, top = 12.dp, bottom = 75.dp)){
            Text(text = "My Noize",
                fontSize = 20.sp, fontWeight = Bold)
            Spacer(Modifier.height(24.dp))


            LazyColumn {
                itemsIndexed(songList) { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        SongCardView(event = {onSongClick(item) },
                            title = item.title, artist = item.subtitle, imageUrl = item.imageUrl)


                    }
                    Spacer(Modifier.height(15.dp))

                }
            }

        }

        currentSong?.let {
            exoPlayer?.let { player ->
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 75.dp,
                    sheetDragHandle = {},
                    modifier = Modifier.align(Alignment.BottomCenter),
                    sheetContent = {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
                            val showMusicPlayer = scaffoldState.bottomSheetState.currentValue.name == "PartiallyExpanded"
                            AnimatedContent(
                                targetState = showMusicPlayer,
                                transitionSpec = {
                                    // Customize animations if desired
                                    fadeIn(animationSpec = tween(15)) with fadeOut(
                                        animationSpec = tween(
                                            15
                                        )
                                    )
                                },
                                label = "SongViewToMusicPlayerTransition"
                            ) { targetState ->
                                if (scaffoldState.bottomSheetState.currentValue.name != "PartiallyExpanded") {
                                    SongView(
                                        exoPlayer = player,
                                        songName = it.title,
                                        songArtist = it.subtitle,
                                        imageUrl = it.imageUrl
                                    )

                                } else {
                                    MusicPlayer(
                                        exoPlayer = player,
                                        songName = it.title,
                                        songArtist = it.subtitle,
                                        imageUrl = it.imageUrl
                                    )
                                }
                            }
                        }
                    }
                ) { }

            }
        }
    }
}


@Composable
fun SongCardView(
    event: () -> Unit,
    title: String = "",
    artist: String = "",
    imageUrl: String = "",
){
   Row(
       verticalAlignment = Alignment.CenterVertically,
       modifier = Modifier.clickable(onClick = event ).fillMaxWidth()
   ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "translated description of what the image contains",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        )

        Column(Modifier.padding(start = 10.dp)) {
            Text(title)
            Spacer(Modifier.height(15.dp))
            Text(artist)
        }

    }
}

@Composable
fun MusicPlayer(
    exoPlayer: ExoPlayer,
    songName: String = "",
    songArtist: String = "",
    imageUrl: String = "",
    modifier: Modifier = Modifier
)
{
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

    Row(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.DarkGray)
            .padding(start = 12.dp, end = 24.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Row {

            AsyncImage(
                model = imageUrl,
                contentDescription = "Image",
                Modifier.size(45.dp).clip(CircleShape)
            )

            Column {
                Text(songName, modifier.padding(start = 12.dp, top = 1.dp, bottom = 1.dp), fontWeight = Bold, fontSize = 18.sp)

                Text(songArtist, Modifier.padding(start = 12.dp, top = 1.dp, bottom = 1.dp), fontSize = 12.sp)
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