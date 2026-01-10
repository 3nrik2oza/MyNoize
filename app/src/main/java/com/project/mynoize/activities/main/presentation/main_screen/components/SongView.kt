package com.project.mynoize.activities.main.presentation.main_screen.components


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenEvent
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenState
import com.project.mynoize.activities.main.ui.PlayButton
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.LightGray
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SongView(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope
){
    var sliderPosition by remember { mutableFloatStateOf(state.currentPosition / state.duration.toFloat() ) }

    val infiniteTransition = rememberInfiniteTransition(label = "disk-rotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable( animation = tween (durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart ),
        label = "rotation" )

    Column(modifier.fillMaxWidth()
        .padding(top = 20.dp, start = 12.dp, end = 12.dp),
        Arrangement.Top, Alignment.CenterHorizontally) {

        Column(
            modifier = Modifier.weight(1f).padding(bottom = 1.dp).fillMaxSize().padding(top = 10.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "NOW PLAYING \"${state.currentSong?.title}\"".uppercase(),
                fontFamily = LatoFontFamily,
                fontWeight = Bold,
                color = Color.Black,
                fontSize = 27.sp,
                maxLines = 3
            )

            Text(
                text= "FROM ALBUM \"${state.currentSong?.albumTitle}\"".uppercase(),
                fontFamily = NovaSquareFontFamily,
                color = Red,
                fontSize = 15.sp,
                maxLines = 2
            )
        }

        var imageLoadResult by remember(state.currentSong?.artworkUri) { mutableStateOf<Result<Painter>?>(null) }


        val painter = rememberAsyncImagePainter(
            model = state.currentSong?.artworkUri,
            onSuccess = {
                imageLoadResult = Result.success(it.painter)
            },
            onError = {
                imageLoadResult = Result.failure(it.result.throwable)
            }
        )


        Box(Modifier.sharedElement(
            sharedContentState = rememberSharedContentState(key = "image1/${state.currentSong!!.artworkUri}"),
            animatedVisibilityScope = animatedVisibilityScope,
            boundsTransform = {_,_ ->
                tween(durationMillis = 500)
            }
        )
        ){
            when(val result = imageLoadResult){
                null-> Row(
                    modifier= Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Box(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        Image(painter=painter, contentDescription = null, alpha = 0.001f)
                    }
                }
                else ->{
                    Row(
                        modifier= Modifier.width(310.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .padding(top = 40.dp)
                                .size(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.music_disk),
                                contentDescription = "Music Disk",
                                modifier = Modifier
                                    .size(200.dp)
                                    .offset(x = 100.dp)
                                    .graphicsLayer{
                                        rotationZ = rotation
                                    }
                                    .zIndex(0f)
                            )
                            Image(
                                painter = if(result.isSuccess) painter else painterResource(R.drawable.music_disk),
                                contentDescription = "Song Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(210.dp)
                                    .zIndex(1f)
                            )
                        }
                    }
                }
            }
        }




        Column(
            modifier= Modifier.weight(2f).fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                state.currentSong.title.toString(),
                fontWeight = Bold, fontSize = 18.sp,
                fontFamily = NovaSquareFontFamily,
                color = Color.Black,
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "text/${state.currentSong.title}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = {_,_ ->
                        tween(durationMillis = 500)
                    }
                )
            )

            Text(
                state.currentSong.artist.toString(),
                fontSize = 15.sp,
                color = DarkGray,
                fontFamily = NovaSquareFontFamily,
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "text/${state.currentSong.artist}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = {_,_ ->
                        tween(durationMillis = 500)
                    }
                )
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Slider(
                    value = state.currentPosition.toFloat()/state.duration,
                    onValueChange = { sliderPosition = it},
                    enabled = state.duration > 0,
                    onValueChangeFinished = {
                        onEvent(MainScreenEvent.SeekTo((sliderPosition * state.duration).toLong()))
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.height(15.dp).padding(top =  15.dp),
                    track = { sliderState ->

                        Box(Modifier.fillMaxWidth().background(LightGray)) {
                            Box(
                                Modifier
                                    .fillMaxWidth(sliderState.value )
                                    .align(Alignment.CenterStart)
                                    .height(2.dp)
                                    .background(DarkGray, CircleShape)
                            )
                        }
                    },
                    thumb = {
                    }
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement =  Arrangement.SpaceBetween
                ){
                    Text(
                        text = formatMillis(state.currentPosition),
                        modifier = Modifier.padding(start = 12.dp),
                        fontFamily = NovaSquareFontFamily,
                        fontWeight = Bold,
                        color = DarkGray,
                        fontSize = 12.sp
                    )

                    Text(
                        formatMillis(state.duration),
                        modifier = Modifier.padding(start = 12.dp),
                        fontFamily = NovaSquareFontFamily,
                        fontWeight = Bold,
                        color = DarkGray,
                        fontSize = 12.sp
                    )
                }

                SongViewButtons(onEvent, state = state)
            }
        }






    }

}

@Composable
fun SongViewButtons(
    onEvent: (MainScreenEvent) -> Unit,
    state: MainScreenState){
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.offset(y = (-15).dp)){
        Button(
            modifier = Modifier.size(39.dp),
            contentPadding = PaddingValues(5.dp),
            colors = ButtonColors(contentColor = DarkGray, containerColor = Color.Transparent, disabledContainerColor = Color.Transparent, disabledContentColor = Color.Transparent),
            onClick = {
                onEvent(MainScreenEvent.OnPrevSongClick)
            }) {
            Icon(painterResource(R.drawable.ic_prev), contentDescription = null, Modifier.fillMaxSize())

        }

        Spacer(Modifier.width(24.dp))


        PlayButton(
            state = state,
            onEvent = {onEvent(it)}
        )

        Spacer(Modifier.width(24.dp))

        Button(
            modifier = Modifier.size(39.dp),
            contentPadding = PaddingValues(5.dp),
            colors = ButtonColors(contentColor = DarkGray, containerColor = Color.Transparent, disabledContainerColor = Color.Transparent, disabledContentColor = Color.Transparent),
            onClick = {
                onEvent(MainScreenEvent.OnNextSongClick)
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
