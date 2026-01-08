package com.project.mynoize.activities.main.presentation.playlist_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenEvent
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.core.data.Playlist


@Composable
fun PlaylistOptionsBottomSheet(playlist: Playlist, event: (PlaylistScreenEvent) -> Unit){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Spacer(Modifier.height(15.dp))

        Text(
            text = playlist.name,
            fontFamily = LatoFontFamily,
            fontWeight = Bold,
            color = Color.Black,
            fontSize = 24.sp,
            maxLines = 1
        )

        Spacer(Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .size(80.dp),
            contentAlignment = Alignment.Center
        ){
            ImageWithLoading(playlist.imageLink, isFavorite = playlist.name == "Favorites")
        }

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(
            color = DarkGray,
            thickness = 1.dp
        )


        Text(
            text = "Download playlist",
            modifier = Modifier
                .clickable(onClick = {event(PlaylistScreenEvent.OnRemoveSongClick)})
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp)
                .padding(horizontal = 20.dp),
            textAlign = TextAlign.Start
        )

        Text(
            text = "Delete playlist",
            modifier = Modifier
                .clickable(onClick = { event(PlaylistScreenEvent.OnToggleDeletePlaylist) })
                .fillMaxWidth().padding(bottom = 10.dp).padding(horizontal = 20.dp),
            textAlign = TextAlign.Start
        )

    }
}