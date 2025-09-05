package com.project.mynoize.activities.main.presentation.main_screen.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.project.mynoize.activities.main.CreateArtistScreen
import com.project.mynoize.activities.main.FavoriteScreen
import com.project.mynoize.activities.main.MusicScreen
import com.project.mynoize.activities.main.ProfileScreen
import com.project.mynoize.activities.main.ShowMusic
import com.project.mynoize.core.data.NavigationItem

@SuppressLint("UnrememberedMutableState")
@Composable
fun BottomNavigationBar(
    navController: NavController,
    selectedNavigationIndex: MutableState<Int>,
    selectedNavigationIndexBefore: MutableState<Int>,
    createActive: MutableState<Boolean>,
    onCreateClick: () -> Unit = {}
){
    val navigationItems = listOf(
        NavigationItem(
            title = "Music",
            icon = Icons.Default.MusicNote,
            route = MusicScreen
        ),
        NavigationItem(
            title = "Favorite",
            icon = Icons.Default.Favorite,
            route = FavoriteScreen
        ),

        NavigationItem(
            title = "Create",
            icon = Icons.Default.Add,
            route = CreateArtistScreen
        ),
        NavigationItem(
            title = "Show",
            icon = Icons.Default.Preview,
            route = ShowMusic
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Default.Person,
            route = ProfileScreen
        )

    )




    NavigationBar (
        containerColor = Color.White
    ){
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.value == index,
                onClick = {

                    Log.d("NAVIGATION", index.toString())
                    if(index == 2){
                        createActive.value = !createActive.value
                        if(createActive.value){
                            selectedNavigationIndexBefore.value = selectedNavigationIndex.value
                            selectedNavigationIndex.value = index
                            onCreateClick()
                        }else{
                            selectedNavigationIndex.value = selectedNavigationIndexBefore.value
                        }
                    }else{
                        createActive.value = false
                        navController.navigate(item.route)
                        selectedNavigationIndex.value = index
                    }



                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.rotate(if(createActive.value && index == 2) 45f else 0f)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (index == selectedNavigationIndex.value) Color.Black else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}