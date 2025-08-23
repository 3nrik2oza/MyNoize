package com.project.mynoize.activities.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.mynoize.activities.main.events.ProfileScreenEvent
import com.project.mynoize.activities.main.viewmodels.ProfileScreenViewModel

@Composable
fun ProfileScreen(
    vm: ProfileScreenViewModel,
){

    Column(modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Spacer(Modifier.size(50.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .padding(bottom = 15.dp)
        ) {

        }

        Text(
            text = vm.username,
            style = MaterialTheme.typography.headlineLarge
        )
        Button(
            onClick = {
                vm.onEvent(ProfileScreenEvent.OnSignOutClick)
            }
        ) {
            Text("Sign out")
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        vm = ProfileScreenViewModel()
    )
}

