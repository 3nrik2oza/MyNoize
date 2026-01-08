package com.project.mynoize.activities.main.presentation.playlist_screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.playlist_screen.components.ImageWithLoading
import com.project.mynoize.activities.main.presentation.playlist_screen.components.PlaylistOptionsBottomSheet
import com.project.mynoize.activities.main.presentation.playlist_screen.components.SongOptionsBottomSheet
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.SongItem
import com.project.mynoize.util.BottomSheetType

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.PlaylistScreen(
    state: PlaylistScreenState,
    alertDialogState: AlertDialogState = AlertDialogState(),
    onEvent: (PlaylistScreenEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
){

    val sheetState = rememberModalBottomSheetState()
    val blockInput = animatedVisibilityScope.transition.isRunning


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

    if(state.deletePlaylistSheetOpen){
        MessageAlertDialog(
            onDismiss = { onEvent(PlaylistScreenEvent.OnToggleDeletePlaylist) },
            message = "Are you sure that you want to delete this playlist?",
            warning = true,
            hasConfirmButton = true,
            onConfirm = {
                onEvent(PlaylistScreenEvent.OnDeletePlaylist)
                onEvent(PlaylistScreenEvent.OnBackClick)
            }
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
                            if(blockInput) return@clickable
                            onEvent(PlaylistScreenEvent.OnBackClick)
                        }
                )

                if(state.playlist.name != "Favorites"){
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        Modifier.size(35.dp)
                            .clickable(onClick = {
                                if(blockInput) return@clickable
                                onEvent(PlaylistScreenEvent.OnMorePlaylistClick) }
                            )
                    )
                }

            }


            Box(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(150.dp)
                    .sharedElement(
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 300)
                        },
                        sharedContentState = rememberSharedContentState(key = "image/${state.playlist.imageLink}")
                    ),
                contentAlignment = Alignment.Center
            ){
                ImageWithLoading(image = state.playlist.imageLink, isFavorite = state.playlist.name == "Favorites")
            }



            Text(
                text = state.playlist.name,
                fontSize = 18.sp,
                fontFamily = NovaSquareFontFamily,
                fontWeight = Bold,
                color = DarkGray,
                modifier = Modifier.padding(top = 15.dp)
            )

            if(state.playlist.name != "Favorites"){
                Text(
                    text = "ADD SONGS",
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 10.dp)
                        .clickable(onClick = {
                            if(blockInput) return@clickable
                            onEvent(PlaylistScreenEvent.OnAddSongClick)
                        })
                        .border(width = 1.dp, shape = RectangleShape, color = Red)
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                    ,
                    fontFamily = LatoFontFamily,
                    color = Red
                )
            }


            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_random),
                    contentDescription = "Play random",
                    Modifier.size(35.dp)
                        .clickable{
                            if(blockInput) return@clickable
                            onEvent(PlaylistScreenEvent.OnPlayRandom)
                        }
                )

                if(state.playlist.name != "Favorites"){
                    Icon(
                        imageVector = Icons.Default.Mode,
                        contentDescription = "Modify",
                        Modifier.size(35.dp)
                            .clickable(onClick = {
                                if(blockInput) return@clickable
                                onEvent(PlaylistScreenEvent.OnPlaylistModifyClicked(playlistId = state.playlist.id)) })
                    )
                }

            }
        }

        itemsIndexed(
            items = state.songs,
            key = {_, song -> song.id}
        ) {index, song ->
            SongItem(
                song = song,
                playlistScreenEvent = { event ->
                    if(blockInput) return@SongItem

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
            when(state.sheetType){
                BottomSheetType.SONG -> SongOptionsBottomSheet(state.selectedSong(), artist = state.artist, event = onEvent)
                BottomSheetType.PLAYLIST -> PlaylistOptionsBottomSheet(playlist = state.playlist, event = onEvent)
            }
        }
    }
}



