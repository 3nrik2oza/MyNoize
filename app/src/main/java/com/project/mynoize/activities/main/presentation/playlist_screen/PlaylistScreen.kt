package com.project.mynoize.activities.main.presentation.playlist_screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.project.mynoize.R
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.Song
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.SongItem

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.PlaylistScreen(
    state: PlaylistScreenState,
    alertDialogState: AlertDialogState = AlertDialogState(),
    onEvent: (PlaylistScreenEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
){

    val sheetState = rememberModalBottomSheetState()


    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                if(alertDialogState.warning){
                    onEvent(PlaylistScreenEvent.OnDismissAlertDialog)
                }else{
                    onEvent(PlaylistScreenEvent.OnBackClick)
                }

            },
            message = alertDialogState.message?.asString() ?: "",
            warning = alertDialogState.warning
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item{
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    Modifier.size(35.dp)
                        .clickable{
                            onEvent(PlaylistScreenEvent.OnBackClick)
                        }
                )

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    Modifier.size(35.dp)
                        .clickable(onClick = {

                        }
                        )
                )
            }

            val painter = rememberAsyncImagePainter(model = state.playlist.image)

            val painterState = painter.state

            Box(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(150.dp)
                    .sharedElement(
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        },
                        sharedContentState = rememberSharedContentState(key = "image/${state.playlist.image}")
                    ),
                contentAlignment = Alignment.Center
            ){
                when(painterState){
                    is AsyncImagePainter.State.Loading ->{
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                        CircularProgressIndicator()
                    }
                    is AsyncImagePainter.State.Error ->{
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Error loading image"
                        )
                    }
                    else -> {
                        Image(
                            painter = painter,
                            contentDescription = "Image",
                            modifier = Modifier.fillMaxSize()

                        )
                    }
                }
            }



            Text(
                text = state.playlist.name,
                fontSize = 18.sp,
                fontFamily = NovaSquareFontFamily,
                fontWeight = Bold,
                color = DarkGray,
                modifier = Modifier.padding(top = 15.dp)
            )

            Text(
                text = "ADD SONGS",
                fontSize = 25.sp,
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 10.dp)
                    .clickable(onClick = {
                        onEvent(PlaylistScreenEvent.OnAddSongClick)
                    })
                    .border(width = 1.dp, shape = RectangleShape, color = Red)
                    .padding(horizontal = 30.dp, vertical = 8.dp)
                ,
                fontFamily = LatoFontFamily,
                color = Red
            )

            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_random),
                    contentDescription = "Play",
                    Modifier.size(35.dp)
                        .clickable{

                        }
                )

                Icon(
                    imageVector = Icons.Default.Mode,
                    contentDescription = "Options",
                    Modifier.size(35.dp)
                        .clickable(onClick = {

                        })
                )
            }
        }

        itemsIndexed(
            items = state.songs,
            key = {_, song -> song.id}
        ) {index, song ->
            SongItem(
                song = song,
                playlistScreenEvent = { event ->
                    onEvent(event)
                },
                index = index,
                inPlaylistView = true
            )

        }

        item{
            Spacer(Modifier.height(30.dp))
        }

    }

    if(state.isSheetOpen){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { onEvent(PlaylistScreenEvent.OnDismissAlertDialog) },
            containerColor = Color.White,
            shape = RectangleShape,
            dragHandle = {}
        ) {
            BottomSheetView(state.selectedSong(), artist = state.artist, event = onEvent)
        }
    }
}

@Composable
private fun BottomSheetView(song: Song, artist: Artist, event: (PlaylistScreenEvent) -> Unit){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Spacer(Modifier.height(15.dp))

        Text(
            text = song.title,
            fontFamily = LatoFontFamily,
            fontWeight = Bold,
            color = Color.Black,
            fontSize = 24.sp,
            maxLines = 1
        )

        Text(
            text= song.albumName,
            fontFamily = NovaSquareFontFamily,
            color = Red,
            fontSize = 15.sp,
            maxLines = 1
        )

        Spacer(Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .size(80.dp),
            contentAlignment = Alignment.Center
        ){
            ImageWithLoading(artist.image)

        }

        Spacer(Modifier.height(5.dp))

        Text(
            text = song.artistName
        )

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(
            color = DarkGray,
            thickness = 1.dp
        )


        Text(
            text = "Remove from playlist",
            modifier = Modifier
                .clickable(onClick = {event(PlaylistScreenEvent.OnRemoveSongClick)})
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp)
                .padding(horizontal = 20.dp),
            textAlign = TextAlign.Start
        )

        Text(
            text = "Add/Remove from favorites",
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).padding(horizontal = 20.dp),
            textAlign = TextAlign.Start
        )

        Text(
            text = "Add to Playlist",
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).padding(horizontal = 20.dp),
            textAlign = TextAlign.Start
        )

    }
}

@Composable
fun ImageWithLoading(image: String){

    val painter = rememberAsyncImagePainter(model = image)

    val painterState = painter.state
    when(painterState){
        is AsyncImagePainter.State.Loading ->{
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            CircularProgressIndicator()
        }
        is AsyncImagePainter.State.Error ->{
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "Error loading image"
            )
        }
        else -> {
            Image(
                painter = painter,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

}