package com.project.mynoize.activities.main.presentation.select_songs_screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.core.presentation.AlertDialogState
import com.project.mynoize.core.presentation.asString
import com.project.mynoize.core.presentation.components.CustomSearchBar
import com.project.mynoize.core.presentation.components.MessageAlertDialog
import com.project.mynoize.core.presentation.components.SongItem

@Composable
fun SelectSongsScreen(
    state: SelectSongsState = SelectSongsState(),
    alertDialogState: AlertDialogState = AlertDialogState(),
    onEvent: (SelectSongsEvent) -> Unit = {}
){
    if(alertDialogState.show){
        MessageAlertDialog(
            onDismiss = {
                if(alertDialogState.warning){
                    onEvent(SelectSongsEvent.OnDismissAlertDialog)
                }else{
                    onEvent(SelectSongsEvent.OnBackClick)
                }

            },
            message = alertDialogState.message?.asString() ?: "",
            warning = alertDialogState.warning
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f)){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    Modifier.size(35.dp)
                        .clickable{
                            onEvent(SelectSongsEvent.OnBackClick)
                        }
                )

                Spacer(Modifier.width(20.dp))

                Text(
                    text = state.playlist.name,
                    fontFamily = LatoFontFamily,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    fontSize = 24.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Finish",
                Modifier.size(35.dp)
                    .clickable{
                        onEvent(SelectSongsEvent.OnFinishClick)
                    }
            )
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            CustomSearchBar(
                searchQuery = "",
                onSearchQueryChange = {},
                onImeSearch = {}
            )
        }

        Spacer(Modifier.height(40.dp))

        LazyColumn {
            itemsIndexed(items = state.songs){index, song ->
                SongItem(
                    song = song,
                    index = index,
                    selectSongsEvent = onEvent,
                    inPlaylistView = false
                )
            }
        }

    }
}

@Preview
@Composable
fun PrevSelectSongScreen(){
    SelectSongsScreen()

}