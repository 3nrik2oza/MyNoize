package com.project.mynoize.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenEvent
import com.project.mynoize.activities.main.presentation.select_songs_screen.SelectSongsEvent
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.core.data.Song

@Composable
fun SongItem(
    song: Song,
    index: Int = 0,
    selectSongsEvent: (SelectSongsEvent) -> Unit = {},
    playlistScreenEvent: (PlaylistScreenEvent) -> Unit = {},
    inPlaylistView: Boolean,
    showMore: Boolean = true,
) {

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.clickable(onClick = {
                playlistScreenEvent(PlaylistScreenEvent.OnSongClicked(index))
            }).fillMaxWidth().height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val painter = rememberAsyncImagePainter(model = song.imageUrl)

            val painterState = painter.state

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ){
                Box(
                    modifier = Modifier
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ){
                    when(painterState){
                        is AsyncImagePainter.State.Loading ->{
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.matchParentSize(),
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

                Spacer(Modifier.width(20.dp))

                Column{
                    Text(
                        text = song.title,
                        maxLines = 2,
                        fontFamily = NovaSquareFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = song.artistName,
                        maxLines = 1,
                        fontFamily = NovaSquareFontFamily,
                        color = DarkGray.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            if(inPlaylistView){

                Row {

                    Icon(
                        painter = painterResource(id = if(song.favorite) R.drawable.ic_heart else R.drawable.ic_heart_empty),
                        contentDescription = if(song.favorite) "Remove song from favorite" else "Add song to favorite",
                        modifier = Modifier.clickable{ playlistScreenEvent(PlaylistScreenEvent.OnSongFavoriteToggle(song)) }
                    )

                    if(showMore){
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Add song to the playlist",
                            modifier = Modifier.clickable(onClick = {
                                playlistScreenEvent(
                                    PlaylistScreenEvent.OnMoreSongClick(
                                        song = song
                                    )
                                )
                            })
                        )
                    }

                }
            }else{
                Icon(
                    imageVector = if(song.favorite) Icons.Default.Check else Icons.Default.LibraryMusic,
                    contentDescription = "Add song to the playlist",
                    modifier = Modifier.clickable(onClick = {
                        selectSongsEvent(
                            SelectSongsEvent.OnSongClicked(
                                index = index,
                                add = !song.favorite
                            )
                        )
                    })
                )
            }





        }
        Spacer(Modifier.height(35.dp))
    }

}


@Preview
@Composable
fun PrevSongItem(){
    SongItem(
        song = Song(),
        selectSongsEvent = {},
        inPlaylistView = true
    )
}
