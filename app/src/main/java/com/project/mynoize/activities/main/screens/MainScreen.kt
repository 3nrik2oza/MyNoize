package com.project.mynoize.activities.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.integrity.v
import com.project.mynoize.activities.main.CreateScreen
import com.project.mynoize.activities.main.FavoriteScreen
import com.project.mynoize.activities.main.MusicScreen
import com.project.mynoize.activities.main.ProfileScreen
import com.project.mynoize.activities.main.ui.BottomNavigationBar
import com.project.mynoize.activities.main.viewmodels.ProfileScreenViewModel

@Composable
fun MainScreen(
    vmProfileScreen: ProfileScreenViewModel
){

    val navController = rememberNavController()


    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {BottomNavigationBar(navController)}
    ){ innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MusicScreen,
            modifier = Modifier.padding(innerPadding)
        ){
            composable<MusicScreen>{
                HomeScreen()
            }
            composable<FavoriteScreen>{
                CartScreen()
            }

            composable<CreateScreen> {
                SettingScreen()
            }

            composable<ProfileScreen> {
                ProfileScreen(
                    vm = vmProfileScreen
                )
            }

        }

    }

}




//HomeScreen.kt
@Composable
fun HomeScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Home Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

//CartScreen.kt
@Composable
fun CartScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Cart Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

//SettingScreen
@Composable
fun SettingScreen(){
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Setting Screen",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val vmProfileScreen = ProfileScreenViewModel()
    MainScreen(
        vmProfileScreen
    )
}

