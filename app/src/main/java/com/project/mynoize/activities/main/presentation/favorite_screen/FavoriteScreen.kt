package com.project.mynoize.activities.main.presentation.favorite_screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.favorite_screen.components.PlaylistScrollElement
import com.project.mynoize.core.data.Artist

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FavoriteScreen(
    state: FavoriteScreenState,
    onEvent: (FavoriteScreenEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
){

    Column(
        Modifier.fillMaxSize()
            .padding(start = 10.dp, top= 20.dp)
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            text= "Your library"
        )

        Spacer(Modifier.size(10.dp))

        Text(
            fontSize = 23.sp,
            text= "Artists"
        )

        LazyRow {

            itemsIndexed(state.artists){ _, artist ->
                ArtistsScrollElement(
                    artist = artist,
                    onClick = {onEvent(FavoriteScreenEvent.OnArtistClicked(artist.id))}
                )
            }

        }

        Spacer(Modifier.size(10.dp))

        Text(
            fontSize = 23.sp,
            text= "Playlists"
        )

        Spacer(Modifier.size(10.dp))

        LazyRow {
            itemsIndexed(state.playlists){ _, playlist ->
                PlaylistScrollElement(
                    modifier = Modifier.size(100.dp),
                    playlist = playlist,
                    onEvent = onEvent,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }

        Text(
            fontSize = 23.sp,
            text= "Albums"
        )

        LazyRow {
            itemsIndexed(state.albums){ _, playlist ->
                PlaylistScrollElement(
                    modifier = Modifier.size(100.dp),
                    playlist = playlist,
                    isPlaylist = false,
                    onEvent = onEvent,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }



    }
}

@Composable
fun AddPlaylistScrollElement(
    text:String = "",
    createNew: () -> Unit
){
    Column (
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
                .border(1.dp, Color.Black)
                .clickable{
                    createNew()
                }
        ){

            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Add",
                modifier = Modifier.size(50.dp)
            )

        }
        Text(
            fontSize = 12.sp,
            text = text
        )
    }
}

@Composable
fun ArtistsScrollElement(
    artist: Artist,
    onClick: () -> Unit
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ){

        AsyncImage(
            model = artist.imageLink,
            contentDescription = "Artist image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Black, CircleShape)
                .clickable{
                    onClick()
                }
        )

        Text(artist.name)
    }

}

@Composable
fun AddArtistsScrollElement(){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ){

        Box(
            Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(50.dp))
        }


        Text("Add artist")
    }

}

/*
@Preview(showBackground = true)
@Composable
fun FavoriteScreenPreview(){
    FavoriteScreen(
        state = FavoriteScreenState(),
        onEvent = {}
    )

}*/