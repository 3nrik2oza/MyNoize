package com.project.mynoize.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.project.mynoize.activities.main.ui.PlayButton
import com.project.mynoize.activities.main.ui.SongView
import com.project.mynoize.activities.main.ui.theme.MyNoizeTheme
import com.project.mynoize.data.Song


class MainActivity : ComponentActivity() {
    val vm: MainScreenViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
            MyNoizeTheme {
                val currentSong by vm.currentSong.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MainView(
                        songList = vm.songList.value,
                        currentSong = currentSong,
                        onSongClick = { song ->
                            vm.onSongClick(song)
                        },
                        exoPlayer = vm.playerManager.getPlayer(),
                        onNextSong = {
                            vm.nextSong()
                        },
                        onPrevSong = {
                            vm.prevSong()
                        }
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
    onNextSong: (Song) -> Unit,
    onPrevSong: (Song) -> Unit,
    exoPlayer: ExoPlayer?
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 25.dp),
    ) {
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
                                        song = it,
                                        onPrevSong = onPrevSong,
                                        onNextSong = onNextSong
                                    )

                                } else {
                                    MusicPlayer(
                                        exoPlayer = player,
                                        song = it
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
    song: Song,
    modifier: Modifier = Modifier
) {


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
                model = song.imageUrl,
                contentDescription = "Image",
                Modifier.size(45.dp).clip(CircleShape)
            )

            Column {
                Text(song.title, modifier.padding(start = 12.dp, top = 1.dp, bottom = 1.dp), fontWeight = Bold, fontSize = 18.sp)

                Text(song.subtitle, Modifier.padding(start = 12.dp, top = 1.dp, bottom = 1.dp), fontSize = 12.sp)
            }

        }

        PlayButton(exoPlayer)



    }
}

