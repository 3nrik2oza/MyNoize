package com.project.mynoize.activities.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.project.mynoize.activities.main.CreateScreen
import com.project.mynoize.activities.main.FavoriteScreen
import com.project.mynoize.activities.main.MusicScreen
import com.project.mynoize.activities.main.ProfileScreen
import com.project.mynoize.data.NavigationItem

@Composable
fun BottomNavigationBar(
    navController: NavController
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
            route = CreateScreen
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Default.Person,
            route = ProfileScreen
        )

    )

    var selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar (
        containerColor = Color.White
    ){
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (index == selectedNavigationIndex.intValue) Color.Black else Color.Gray
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