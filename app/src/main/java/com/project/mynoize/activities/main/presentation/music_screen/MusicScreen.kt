package com.project.mynoize.activities.main.presentation.music_screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.playlist_screen.components.ImageWithLoading
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.core.data.Playlist

@Composable
fun MusicScreen(
    state: MusicScreenState,
    onEvent: (MusicScreenEvent) -> Unit,
    onSignOutClicked : () -> Unit
){

    Column(
        Modifier.background(Color.White).fillMaxSize()
            .padding(start = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {

            Text(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 23.sp,
                text = "My Noize",
            )

            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                modifier = Modifier.clickable{ onSignOutClicked() }.padding(end = 10.dp)
            )

        }



        Spacer(Modifier.size(25.dp))

        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text="Playlists made for you")

        LazyRow(
            modifier = Modifier.height(200.dp)
        ) {
            item{
                AddPlaylistScrollElement(
                    text = "Songs for you",
                    createNew = { onEvent(MusicScreenEvent.OnPlaySongsForUser) }
                )
            }
        }

        /* Row with playlists create for user*/

        Spacer(Modifier.size(25.dp))

        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text="Playlists that you might like")

        LazyRow(
            modifier = Modifier.height(200.dp)
        ) {/*
            items(10){
                PlaylistScrollElement(
                    playlist = Playlist(),
                    onEvent = {}
                )
            }*/

            itemsIndexed(state.playlistsForUser){ _,playlist ->
                PlaylistScrollElement(
                    playlist = playlist,
                    onClick = { onEvent(MusicScreenEvent.OnPlaylistClicked(playlist.id, true)) }
                )

            }
        }

        /* Row with playlists that you might like*/

    }

}



@Preview(showBackground = true)
@Composable
fun ShowMusicScreen(){
    MusicScreen(
        state = MusicScreenState(),
        onEvent = {},
        {}
    )
}

@Composable
fun AddPlaylistScrollElement(
    text:String = "",
    createNew: () -> Unit,
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
                painter = painterResource(R.drawable.ic_heart_empty),
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
private fun PlaylistScrollElement(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    onClick: (Playlist) -> Unit
){
    Box(
        modifier = Modifier.clickable{onClick(playlist)}.padding(10.dp)
    ){
        Column{

            Box(
                modifier = Modifier.size(100.dp).border(1.dp, Color.Black),
                contentAlignment = Alignment.Center){
                ImageWithLoading(playlist.imageLink)
            }


            Text(
                fontSize = 12.sp,
                text = playlist.name,
                fontFamily = NovaSquareFontFamily,
                maxLines = 1,
                modifier = modifier.then(Modifier.height(IntrinsicSize.Min))

            )
        }
    }


}
