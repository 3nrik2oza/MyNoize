package com.project.mynoize.activities.main.presentation.artist_screen

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenEvent
import com.project.mynoize.activities.main.presentation.playlist_screen.components.ImageWithLoading
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.core.presentation.components.SongItem

@Composable
fun ArtistScreen(
    state: ArtistScreenState,
    onEvent: (ArtistScreenEvent) -> Unit,
){
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
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    Modifier.size(35.dp)
                        .clickable{
                            onEvent(ArtistScreenEvent.OnBackClick)
                        }
                )
            }

            Box(
                modifier = Modifier
                    .size(150.dp),
                contentAlignment = Alignment.Center
            ){
                ImageWithLoading(state.artist.imageLink)

            }

            Text(
                text = state.artist.name,
                fontSize = 18.sp,
                fontFamily = NovaSquareFontFamily,
                fontWeight = Bold,
                color = DarkGray,
                modifier = Modifier.padding(top = 15.dp)
            )

            Row {
                Icon(
                    imageVector = Icons.Default.Mode,
                    contentDescription = "Modify",
                    Modifier.size(35.dp)
                        .clickable(onClick = {
                            onEvent(ArtistScreenEvent.OnModifyArtist)
                        })
                )

                Icon(
                    painter = if(state.favorite) painterResource(R.drawable.ic_heart) else painterResource(R.drawable.ic_heart_empty),
                    contentDescription = if(state.favorite) "Remove from favorite" else "Add to favorite",
                    Modifier.size(35.dp)
                        .clickable(onClick = {
                            onEvent(ArtistScreenEvent.ArtistFavoriteToggle)
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

                    when(event){
                        is PlaylistScreenEvent.OnSongFavoriteToggle -> onEvent(ArtistScreenEvent.OnSongFavoriteToggle(event.song))
                        is PlaylistScreenEvent.OnSongClicked -> { onEvent(ArtistScreenEvent.OnSongClick(event.index)) }
                        else -> {}
                    }
                    //onEvent(event)
                },
                index = index,
                inPlaylistView = true,
                showMore = false
            )

        }

        item{
            Spacer(Modifier.height(30.dp))
        }

    }
}