package com.project.mynoize.activities.main.presentation.favorite_screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.favorite_screen.components.PlaylistScrollElement
import com.project.mynoize.core.data.Playlist

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

            item{
                AddArtistsScrollElement()
            }

            items(10){
                ArtistsScrollElement()
            }
        }

        Spacer(Modifier.size(10.dp))

        Text(
            fontSize = 23.sp,
            text= "Playlists"
        )

        Spacer(Modifier.size(10.dp))

        LazyRow {

            item{
                AddPlaylistScrollElement(
                    text = "Add playlist"
                )
            }

            itemsIndexed(state.playlists){ index, playlist ->
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

            item{
                AddPlaylistScrollElement(
                    text = "Add album"
                )
            }

            items(10){
                PlaylistScrollElement(
                    modifier = Modifier.size(100.dp),
                    playlist = Playlist(),
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
){
    Column (
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
                .border(1.dp, Color.Black)
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
fun ArtistsScrollElement(){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ){

        AsyncImage(
            model = "",
            contentDescription = "Artist image",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)
                .clickable{

                }
        )

        Text("Artis name")
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
                .background(Color.DarkGray)
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