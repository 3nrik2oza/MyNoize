package com.project.mynoize.activities.main.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenEvent
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenState
import com.project.mynoize.activities.main.ui.theme.DarkGray

@Composable
fun PlayButton(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
){




    Button(
        modifier = Modifier.size(51.dp),
        contentPadding = PaddingValues(5.dp),
        colors = ButtonColors(contentColor = DarkGray, containerColor = Color.Transparent, disabledContainerColor = Color.Transparent, disabledContentColor = Color.Transparent),
        onClick = {
            onEvent(MainScreenEvent.OnPlayPauseToggleClick)
        }) {
        if(state.isPlaying ){
            Icon(painterResource(R.drawable.ic_pause), contentDescription = "Pause", Modifier.fillMaxSize())
        }else{
            Icon(painterResource(R.drawable.ic_play), contentDescription = "Play", Modifier.fillMaxSize())
        }
    }
}