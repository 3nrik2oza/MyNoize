package com.project.mynoize.activities.main.presentation.main_screen

import android.content.Context
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.mynoize.activities.main.FavoriteScreen
import com.project.mynoize.activities.main.MainView
import com.project.mynoize.activities.main.MusicScreen
import com.project.mynoize.activities.main.ProfileScreen
import com.project.mynoize.activities.main.presentation.main_screen.components.BottomNavigationBar
import com.project.mynoize.activities.main.presentation.profile_screen.ProfileScreenViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.project.mynoize.R
import com.project.mynoize.activities.main.CreateArtistScreen
import com.project.mynoize.activities.main.CreateSongScreen
import com.project.mynoize.activities.main.ShowMusic
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistEvent
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistScreen
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistViewModel
import com.project.mynoize.activities.main.presentation.create_song.CreateSongEvent
import com.project.mynoize.activities.main.presentation.create_song.CreateSongScreen
import com.project.mynoize.activities.main.presentation.create_song.CreateSongViewModel
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    vmProfileScreen: ProfileScreenViewModel,
    vmMainScreen: MainScreenViewModel,
    context: Context
){

    val navController = rememberNavController()

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
    val selectedNavigationIndexBefore = rememberSaveable { mutableIntStateOf(0) }
    val createActive = rememberSaveable { mutableStateOf(false) }

    fun closeBottomSheet(){
        selectedNavigationIndex.intValue = selectedNavigationIndexBefore.intValue
        createActive.value = false
        isSheetOpen = false
    }


    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
            if (!(currentDestination == CreateArtistScreen::class.qualifiedName || currentDestination == CreateSongScreen::class.qualifiedName)){
                BottomNavigationBar(
                    navController,
                    selectedNavigationIndex,
                    selectedNavigationIndexBefore,
                    createActive,
                    onCreateClick = { isSheetOpen = true }
                )
            }
        }
    ){ innerPadding ->

        NavHost(
            navController = navController,
            startDestination = FavoriteScreen,
            modifier = Modifier.padding(innerPadding)
        ){
            composable<MusicScreen>{
                com.project.mynoize.activities.main.presentation.music_screen.MusicScreen()
            }
            composable<FavoriteScreen>{
                com.project.mynoize.activities.main.presentation.favorite_screen.FavoriteScreen()
            }

            composable<ShowMusic> {
                val currentSong by vmMainScreen.currentSong.collectAsState()
                MainView(
                    songList = vmMainScreen.songList.value,
                    currentSong = currentSong,
                    onSongClick = { song ->
                        vmMainScreen.onSongClick(song)
                    },
                    exoPlayer = vmMainScreen.playerManager.getPlayer(),
                    onNextSong = {
                        vmMainScreen.nextSong()
                    },
                    onPrevSong = {
                        vmMainScreen.prevSong()
                    }
                )
            }

            composable<ProfileScreen> {
                com.project.mynoize.activities.main.presentation.profile_screen.ProfileScreen(
                    vm = vmProfileScreen
                )
            }

            composable<CreateArtistScreen> { backStackEntry ->
                val vm: CreateArtistViewModel = koinViewModel<CreateArtistViewModel>()

                val state by vm.state.collectAsStateWithLifecycle()
                val alertDialogState by vm.alertDialogState.collectAsStateWithLifecycle()
                CreateArtistScreen(
                    state = state,
                    alertDialogState = alertDialogState,
                    onEvent = { event ->
                        when(event){
                            CreateArtistEvent.OnBackClick -> {
                                navController.popBackStack()
                            }else -> Unit
                        }
                        vm.onEvent(event)
                    }
                )
            }

            composable<CreateSongScreen> { backStackEntry ->
                val vm = koinViewModel<CreateSongViewModel>()

                val createSongState by vm.createSongState.collectAsStateWithLifecycle()
                val artistListState by vm.artistListState.collectAsStateWithLifecycle()
                val albumListState by vm.albumListState.collectAsStateWithLifecycle()
                val alertDialogState by vm.alertDialogState.collectAsStateWithLifecycle()
                val createAlbumDialogState by vm.createAlbumDialogState.collectAsStateWithLifecycle()


                CreateSongScreen(
                    context = context,
                    createSongState = createSongState,
                    alertDialogState = alertDialogState,
                    createAlbumDialogState = createAlbumDialogState,
                    albumListState = albumListState,
                    artistListState = artistListState,
                    onEvent = { event ->
                        when(event){
                            CreateSongEvent.OnBackClick -> {
                                navController.popBackStack()
                            }else -> Unit
                        }
                        vm.onEvent(event)
                    },
                    onCreateAlbumEvent = { event ->
                        vm.onCreateAlbumEvent(event)
                    }
                )
            }

        }
    }



    if(isSheetOpen){
        ModalBottomSheet(
            sheetState= sheetState,
            onDismissRequest = {
                closeBottomSheet()
            },
            shape = RectangleShape,
            dragHandle = {},
            containerColor = DarkGray,
            modifier = Modifier.padding(bottom = 70.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 10.dp)
            ){
                CreateViewForBottomSheet(
                    icon = R.drawable.ic_profile,
                    title = "Add Artist",
                    description = "Add your favorite artist to platform so that you can add his songs to your list",
                    onClick = {
                        navController.navigate(CreateArtistScreen)
                        closeBottomSheet()
                    }
                )

                CreateViewForBottomSheet(
                    icon = R.drawable.ic_music_note,
                    title = "Add Song",
                    description = "Add your favorite song to platform so that you can add them to your playlist",
                    onClick = {
                        navController.navigate(CreateSongScreen)
                        closeBottomSheet()
                    }

                )

                CreateViewForBottomSheet(
                    icon = R.drawable.ic_playlist,
                    title = "Create playlist",
                    description = "Create playlists so that you or maybe others can enjoy your songs"
                )
            }

        }
    }
}


@Composable
fun CreateViewForBottomSheet(
    icon: Int,
    title: String = "Title",
    description: String = "Description",
    onClick: () -> Unit = {}
){
    Row(
        Modifier.fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp)
            .clickable{ onClick() },
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(60.dp)
                .background(
                shape = RectangleShape,
                color = Color.White
            )
        ){
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier
                    .size(28.dp)

            )
        }


        Column(
            Modifier.padding(10.dp)
        ) {
            Text(
                text=title.uppercase(),
                color = Red,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NovaSquareFontFamily,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text=description,
                color = Color.White,
                fontFamily = LatoFontFamily,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

        }

    }
}



