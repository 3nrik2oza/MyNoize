package com.project.mynoize.activities.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.project.mynoize.activities.main.presentation.main_screen.MainActivityUiEvent
import com.project.mynoize.activities.main.presentation.main_screen.MainScreen
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenEvent
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenState
import com.project.mynoize.activities.main.ui.PlayButton
import com.project.mynoize.activities.main.ui.theme.MyNoizeTheme
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenViewModel
import com.project.mynoize.activities.main.presentation.profile_screen.ProfileScreenViewModel
import com.project.mynoize.activities.signin.SignInActivity
import com.project.mynoize.core.data.Song
import com.project.mynoize.notification.MusicService
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {

    val vmProfileScreenView: ProfileScreenViewModel by viewModels()

    lateinit var vmMainScreen: MainScreenViewModel

    var intent1: Intent? = null


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            0
            )


        setContent {

            vmMainScreen = koinViewModel<MainScreenViewModel>()

            val state by vmMainScreen.state.collectAsStateWithLifecycle()

            var musicServiceStarted by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                vmProfileScreenView.uiEvent.collect { event ->
                    when(event){
                        is MainActivityUiEvent.NavigateToSignIn -> {
                            val intent = Intent(applicationContext.applicationContext, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> Unit
                    }
                }
            }


            LaunchedEffect(Unit) {
                vmMainScreen.uiEvent.collect { event ->
                    when(event){
                        is MainActivityUiEvent.ShowNotification -> {
                            if(!musicServiceStarted){
                                intent1 = Intent(
                                    applicationContext, MusicService::class.java).also {
                                    startService(it)
                                }
                                musicServiceStarted = true
                            }

                        }
                        else -> Unit
                    }
                }
            }

            MyNoizeTheme {


                MainScreen(
                    vmProfileScreenView,
                    vmMainScreen,
                    state
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vmMainScreen.playerManager.releasePlayer()
        stopService(intent1)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainView(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp),
    ) {

        Column (Modifier.padding(start = 12.dp, top = 12.dp, bottom = 75.dp)){
            Text(text = "My Noize",
                fontSize = 20.sp, fontWeight = Bold)
            Spacer(Modifier.height(24.dp))


            LazyColumn {
                itemsIndexed(state.songList) { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        SongCardView(
                            event = {onEvent(MainScreenEvent.OnSongClick(index))},
                            song = item
                        )
                    }
                    Spacer(Modifier.height(15.dp))

                }
            }

        }


    }
}


@Composable
fun SongCardView(
    event: () -> Unit,
    song: Song
){
   Row(
       verticalAlignment = Alignment.CenterVertically,
       modifier = Modifier
           .clickable(onClick = event)
           .fillMaxWidth()
   ) {
        AsyncImage(
            model = song.imageUrl,
            contentDescription = "translated description of what the image contains",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RectangleShape)

        )

        Column(Modifier.padding(start = 10.dp)) {
            Text(song.title)
            Spacer(Modifier.height(15.dp))
            Text(song.artistName)


        }

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MusicPlayer(
    modifier: Modifier = Modifier,
    onEvent: (MainScreenEvent) -> Unit,
    state: MainScreenState,
    animatedVisibilityScope: AnimatedVisibilityScope
) {


    Row(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 12.dp, end = 24.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ) {

            AsyncImage(
                model = state.currentSong?.artworkUri ?: "",
                contentDescription = "Image",
                Modifier
                    .size(45.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "image/${state.currentSong!!.artworkUri}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = {_,_ ->
                            tween(durationMillis = 500)
                        }
                    )
            )

            Column {
                Text(
                    state.currentSong.title.toString() ,
                    modifier
                        .padding(start = 12.dp, top = 1.dp, bottom = 1.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "text/${state.currentSong.title}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = {_,_ ->
                                tween(durationMillis = 500)
                            }
                        ),
                    fontWeight = Bold, fontSize = 18.sp,
                    maxLines = 1
                )

                Text(
                    state.currentSong.artist.toString(),
                    Modifier
                        .padding(start = 12.dp, top = 1.dp, bottom = 1.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "text/${state.currentSong.artist}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = {_,_ ->
                                tween(durationMillis = 500)
                            }
                        ),
                    fontSize = 12.sp)
            }

        }

        PlayButton(
            onEvent = onEvent,
            state = state
        )

    }
}

