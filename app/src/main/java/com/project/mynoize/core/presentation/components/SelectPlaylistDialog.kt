package com.project.mynoize.core.presentation.components


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.activities.main.presentation.playlist_screen.components.ImageWithLoading
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.core.data.Playlist


@Composable
fun SelectPlaylistDialog(
    onDismiss: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit = {},
    playlists: List<Playlist> = listOf(Playlist(name = "Test", imageLink = ""))
){
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        modifier = Modifier,
        containerColor = Color.White,
        shape = RectangleShape,
        title = {
            Text(
                text = "Select Playlist",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = LatoFontFamily
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 210.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(playlists){_, playlist ->
                    Column {
                        Row(
                            modifier = Modifier.clickable(onClick = {
                                onPlaylistClick(playlist)
                            }).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(50.dp).border(1.dp, Color.Black)){
                                ImageWithLoading(image = playlist.imageLink, boxSize = 50.dp)
                            }

                            Spacer(Modifier.width(20.dp))

                            Text(
                                text = playlist.name,
                                maxLines = 2,
                                fontFamily = NovaSquareFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                        }
                        Spacer(Modifier.height(30.dp))
                    }
                }



            }
        }
    )
}

@Preview
@Composable
fun SelectPlaylistDialogPrev(){
    SelectPlaylistDialog(onDismiss = {})

}