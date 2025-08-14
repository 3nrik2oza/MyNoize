package com.project.mynoize.data

import androidx.compose.ui.graphics.vector.ImageVector
import com.project.mynoize.activities.main.AppScreen

data class NavigationItem (
    val title: String,
    val icon: ImageVector,
    val route: AppScreen
)