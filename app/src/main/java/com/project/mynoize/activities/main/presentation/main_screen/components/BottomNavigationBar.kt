package com.project.mynoize.activities.main.presentation.main_screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.mynoize.R
import com.project.mynoize.activities.main.CreateArtistScreen
import com.project.mynoize.activities.main.FavoriteScreenRoot
import com.project.mynoize.activities.main.MusicScreen
import com.project.mynoize.activities.main.ProfileScreen
import com.project.mynoize.activities.main.ShowMusic
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LightGray
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
            icon = R.drawable.ic_music,
            route = MusicScreen
        ),
        NavigationItem(
            title = "Favorite",
            icon = R.drawable.ic_heart,
            route = FavoriteScreenRoot
        ),

        NavigationItem(
            title = "Create",
            icon = R.drawable.ic_add,
            route = CreateArtistScreen
        ),
        NavigationItem(
            title = "Show",
            icon = R.drawable.ic_show,
            route = ShowMusic
        ),
        NavigationItem(
            title = "Profile",
            icon = R.drawable.ic_profile,
            route = ProfileScreen
        )

    )




    NavigationBar(
        modifier = Modifier.height(70.dp),
        containerColor = LightGray
    ) {
        navigationItems.forEachIndexed { index, item ->
            val isSelected = selectedNavigationIndex.value == index

            NavigationBarItem(
                selected = isSelected,
                onClick = {},
                icon = {
                    Box(
                        modifier = Modifier
                            .noRippleClickable {
                                if (index == 2) {
                                    createActive.value = !createActive.value
                                    if (createActive.value) {
                                        selectedNavigationIndexBefore.value =
                                            selectedNavigationIndex.value
                                        selectedNavigationIndex.value = index
                                        onCreateClick()
                                    } else {
                                        selectedNavigationIndex.value =
                                            selectedNavigationIndexBefore.value
                                    }
                                } else {
                                    createActive.value = false
                                    navController.navigate(item.route)
                                    selectedNavigationIndex.value = index
                                }
                            }
                            .fillMaxSize()
                            .background(
                                if (isSelected) {
                                    if(index == 2){
                                        DarkGray
                                    }else{
                                        Color.White
                                    }

                                } else Color.Transparent,
                                shape = RectangleShape // full rectangle background
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.rotate(if (createActive.value && index == 2) 45f else 0f),
                            tint =if(isSelected && index == 2)Color.White else Color.Black
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent // remove default small indicator
                )
            )
        }
    }

}

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}