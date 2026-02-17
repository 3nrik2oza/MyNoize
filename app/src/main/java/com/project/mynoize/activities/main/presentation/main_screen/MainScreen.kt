@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.project.mynoize.activities.main.presentation.main_screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.mynoize.activities.main.FavoriteScreen
import com.project.mynoize.activities.main.MusicScreen
import com.project.mynoize.activities.main.presentation.main_screen.components.BottomNavigationBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.project.mynoize.R
import com.project.mynoize.activities.main.ArtistView
import com.project.mynoize.activities.main.CreateArtistScreen
import com.project.mynoize.activities.main.CreatePlaylistScreen
import com.project.mynoize.activities.main.CreateSongScreen
import com.project.mynoize.activities.main.FavoriteScreenRoot
import com.project.mynoize.activities.main.MusicPlayer
import com.project.mynoize.activities.main.PlaylistView
import com.project.mynoize.activities.main.SearchScreen
import com.project.mynoize.activities.main.SelectSongsScreen
import com.project.mynoize.activities.main.presentation.artist_screen.ArtistScreen
import com.project.mynoize.activities.main.presentation.artist_screen.ArtistScreenEvent
import com.project.mynoize.activities.main.presentation.artist_screen.ArtistScreenViewModel
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistEvent
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistScreen
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistViewModel
import com.project.mynoize.activities.main.presentation.create_playlist.CreatePlaylistEvent
import com.project.mynoize.activities.main.presentation.create_playlist.CreatePlaylistScreen
import com.project.mynoize.activities.main.presentation.create_playlist.CreatePlaylistViewModel
import com.project.mynoize.activities.main.presentation.create_song.CreateSongEvent
import com.project.mynoize.activities.main.presentation.create_song.CreateSongScreen
import com.project.mynoize.activities.main.presentation.create_song.CreateSongViewModel
import com.project.mynoize.activities.main.presentation.favorite_screen.FavoriteScreen
import com.project.mynoize.activities.main.presentation.favorite_screen.FavoriteScreenEvent
import com.project.mynoize.activities.main.presentation.favorite_screen.FavoriteScreenViewModel
import com.project.mynoize.activities.main.presentation.main_screen.components.SongView
import com.project.mynoize.activities.main.presentation.music_screen.MusicScreen
import com.project.mynoize.activities.main.presentation.music_screen.MusicScreenEvent
import com.project.mynoize.activities.main.presentation.music_screen.MusicScreenViewModel
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreen
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenEvent
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenViewModel
import com.project.mynoize.activities.main.presentation.search_screen.SearchScreen
import com.project.mynoize.activities.main.presentation.search_screen.SearchScreenEvent
import com.project.mynoize.activities.main.presentation.search_screen.SearchScreenViewModel
import com.project.mynoize.activities.main.presentation.select_songs_screen.SelectSongsEvent
import com.project.mynoize.activities.main.presentation.select_songs_screen.SelectSongsScreen
import com.project.mynoize.activities.main.presentation.select_songs_screen.SelectSongsViewModel
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red
import com.project.mynoize.core.data.SearchItem
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@SuppressLint("UnusedSharedTransitionModifierParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    vmMainScreen: MainScreenViewModel,
    mainState: MainScreenState,
){

    val navController = rememberNavController()

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
    val selectedNavigationIndexBefore = rememberSaveable { mutableIntStateOf(0) }
    val createActive = rememberSaveable { mutableStateOf(false) }

    val showBottomBar = when (navController.currentBackStackEntryAsState().value?.destination?.route) {
        CreateArtistScreen::class.qualifiedName -> false
        CreateSongScreen::class.qualifiedName -> false
        else -> true
    }

    val isConnected = vmMainScreen.isConnected.collectAsStateWithLifecycle()

    fun closeBottomSheet(){
        selectedNavigationIndex.intValue = selectedNavigationIndexBefore.intValue
        createActive.value = false
        isSheetOpen = false
    }

    LaunchedEffect(isConnected.value) {
        if (!isConnected.value) {
            selectedNavigationIndex.intValue = 1
            navController.navigate(FavoriteScreenRoot) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            if (
                showBottomBar && isConnected.value
            ){
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
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = MusicScreen,
                    modifier = Modifier.padding(innerPadding)
                ){
                    composable<MusicScreen>{
                        val vm: MusicScreenViewModel = koinViewModel<MusicScreenViewModel>(scope = getKoin().getScope("USER_SESSION"))

                        val state by vm.state.collectAsStateWithLifecycle()


                        MusicScreen(
                            state= state,
                            onEvent = { event ->
                                when(event){
                                    is MusicScreenEvent.OnPlaylistClicked -> {
                                        navController.navigate(PlaylistView(playlistId = event.id, playList = event.isPlaylist))
                                    }
                                }
                                vm.onEvent(event)
                            },
                            onSignOutClicked = { vmMainScreen.onEvent(MainScreenEvent.OnLogoutClick) }
                        )
                    }

                    navigation<FavoriteScreenRoot>(
                        startDestination = FavoriteScreen
                    ){

                        composable<FavoriteScreen>{
                            val vm: FavoriteScreenViewModel = koinViewModel<FavoriteScreenViewModel>(scope = getKoin().getScope("USER_SESSION"))

                            val state by vm.state.collectAsStateWithLifecycle()
                            FavoriteScreen(
                                state = state,
                                onEvent = { event ->
                                    when(event){
                                        is FavoriteScreenEvent.OnArtistClicked -> {
                                            navController.navigate(ArtistView(artistId = event.artistId))
                                        }
                                        is FavoriteScreenEvent.OnPlaylistClicked -> {
                                            navController.navigate(PlaylistView(playlistId = event.playlistId, playList = event.isPlaylist))
                                        }
                                        is FavoriteScreenEvent.OnCreatePlaylist -> {
                                            navController.navigate(CreatePlaylistScreen(playlistId = ""))
                                        }
                                        //else -> Unit
                                    }
                                    vm.onEvent(event)
                                },
                                animatedVisibilityScope = this
                            )
                        }

                        composable<ArtistView> {
                            val arg = it.toRoute<ArtistView>()

                            val vm: ArtistScreenViewModel = koinViewModel<ArtistScreenViewModel>(scope = getKoin().getScope("USER_SESSION"))

                            LaunchedEffect(true) {
                                vm.onEvent(ArtistScreenEvent.SetArtistId(arg.artistId, isConnected.value))
                            }
                            val state by vm.state.collectAsStateWithLifecycle()

                            ArtistScreen(
                                state = state,
                                onEvent = { event ->
                                    when(event){
                                        is ArtistScreenEvent.OnBackClick -> {
                                            navController.popBackStack()
                                        }
                                        is ArtistScreenEvent.OnModifyArtist -> {
                                            navController.navigate(CreateArtistScreen(artistId = arg.artistId))
                                        }
                                        else -> Unit
                                    }
                                    vm.onEvent(event)
                                },
                                isConnected = isConnected.value
                            )
                        }

                        composable<PlaylistView> {
                            val arg = it.toRoute<PlaylistView>()

                            val vm: PlaylistScreenViewModel = koinViewModel<PlaylistScreenViewModel>(scope = getKoin().getScope("USER_SESSION"))

                            LaunchedEffect(true) {
                                vm.onEvent(PlaylistScreenEvent.SetPlaylistId(arg.playlistId, arg.playList))
                            }

                            val state by vm.state.collectAsStateWithLifecycle()
                            val alertDialogState by vm.alertDialogState.collectAsStateWithLifecycle()

                            PlaylistScreen(
                                state = state,
                                alertDialogState = alertDialogState,
                                onEvent = { event ->
                                    when(event){
                                        is PlaylistScreenEvent.OnBackClick -> {
                                            navController.popBackStack()
                                        }
                                        is PlaylistScreenEvent.OnAddSongClick -> {
                                            navController.navigate(SelectSongsScreen(playlistId = arg.playlistId))
                                        }
                                        is PlaylistScreenEvent.OnPlaylistModifyClicked -> {
                                            navController.navigate(CreatePlaylistScreen(playlistId = arg.playlistId))
                                        }
                                        is PlaylistScreenEvent.OnArtistClick -> {
                                            navController.navigate(ArtistView(artistId = event.artistId))
                                        }
                                        else -> Unit
                                    }
                                    vm.onEvent(event)
                                },
                                animatedVisibilityScope = this
                            )

                        }

                        composable<SelectSongsScreen>{
                            val args = it.toRoute<SelectSongsScreen>()

                            val vm: SelectSongsViewModel = koinViewModel<SelectSongsViewModel>(scope = getKoin().getScope("USER_SESSION"))
                            vm.onEvent(SelectSongsEvent.SetPlaylist(playlistId = args.playlistId))

                            val state by vm.state.collectAsStateWithLifecycle()
                            val alertDialogState by vm.alertDialogState.collectAsStateWithLifecycle()
                            SelectSongsScreen(
                                state = state,
                                alertDialogState = alertDialogState,
                                onEvent = { event ->
                                    when(event){
                                        is SelectSongsEvent.OnBackClick -> {
                                            navController.popBackStack()
                                        }
                                        else -> Unit
                                    }
                                    vm.onEvent(event)
                                }
                            )
                        }

                    }

                    composable<SearchScreen> {
                        val vm: SearchScreenViewModel = koinViewModel<SearchScreenViewModel>(scope = getKoin().getScope("USER_SESSION"))

                        val lifecycleOwner = LocalLifecycleOwner.current

                        DisposableEffect(lifecycleOwner) {
                            val observer = LifecycleEventObserver { _, event ->
                                if(event == Lifecycle.Event.ON_RESUME){
                                    vm.onEvent(SearchScreenEvent.OnUpdateSearchItems)
                                }
                            }
                            lifecycleOwner.lifecycle.addObserver(observer)
                            onDispose {
                                lifecycleOwner.lifecycle.removeObserver(observer)
                            }
                        }

                        val state by vm.state.collectAsStateWithLifecycle()

                        SearchScreen(
                            state = state,
                            onEvent = { event ->
                                when(event){
                                    is SearchScreenEvent.OnSearchItemClicked -> {
                                        when(event.item){
                                            is SearchItem.PlaylistItem -> {
                                                navController.navigate(PlaylistView(playlistId = event.item.playlist.id, playList = true))
                                            }
                                            is SearchItem.ArtistItem -> {
                                                navController.navigate(ArtistView(artistId = event.item.artist.id))
                                            }
                                            is SearchItem.AlbumItem -> {
                                                val album = event.item.album
                                                navController.navigate(PlaylistView(playlistId = album.artist+"/"+album.id, playList = false))
                                            }
                                            else -> {}
                                        }
                                    }
                                    else -> {}
                                }
                                vm.onEvent(event)
                            }
                        )
                    }
/*
                    composable<ProfileScreen> {
                        ProfileScreen(
                            vm = vmProfileScreen
                        )
                    }*/

                    composable<CreateArtistScreen> {
                        val arg = it.toRoute<CreateArtistScreen>()
                        val vm: CreateArtistViewModel = koinViewModel<CreateArtistViewModel>(scope = getKoin().getScope("USER_SESSION"))

                        LaunchedEffect(true) {
                            if(arg.artistId != "") {
                                vm.onEvent(CreateArtistEvent.OnModifyArtist(artistId = arg.artistId))
                            }
                        }

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

                    composable<CreateSongScreen> {
                        val vm = koinViewModel<CreateSongViewModel>(scope = getKoin().getScope("USER_SESSION"))

                        val createSongState by vm.createSongState.collectAsStateWithLifecycle()
                        val artistListState by vm.artistListState.collectAsStateWithLifecycle()
                        val albumListState by vm.albumListState.collectAsStateWithLifecycle()
                        val alertDialogState by vm.alertDialogState.collectAsStateWithLifecycle()
                        val createAlbumDialogState by vm.createAlbumDialogState.collectAsStateWithLifecycle()


                        CreateSongScreen(
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

                    composable<CreatePlaylistScreen> {
                        val arg = it.toRoute<CreatePlaylistScreen>()
                        val vm: CreatePlaylistViewModel = koinViewModel<CreatePlaylistViewModel>(scope = getKoin().getScope("USER_SESSION"))

                        LaunchedEffect(true) {
                            if(arg.playlistId != ""){
                                vm.onEvent(CreatePlaylistEvent.OnModifyPlaylist(playlistId = arg.playlistId))
                            }
                        }

                        val state by vm.state.collectAsStateWithLifecycle()
                        val alertDialogState by vm.alertDialogState.collectAsStateWithLifecycle()

                        CreatePlaylistScreen(
                            state = state,
                            alertDialogState = alertDialogState,
                            onEvent = { event ->
                                when(event){
                                    CreatePlaylistEvent.OnBackClick -> {
                                        navController.popBackStack()
                                    }else -> Unit
                                }
                                vm.onEvent(event)
                            }

                        )
                    }

                }

                val scaffoldState = rememberBottomSheetScaffoldState()
                val padding by animateDpAsState(
                    targetValue = if(isConnected.value) 0.dp else 60.dp
                )

                mainState.currentSong?.let {
                    BottomSheetScaffold(
                        scaffoldState = scaffoldState,
                        sheetPeekHeight = 140.dp,
                        sheetDragHandle = {},
                        modifier = Modifier.align(Alignment.BottomCenter),
                        sheetShape = RectangleShape,
                        sheetContent = {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    shape = RectangleShape,
                                    color = Color.White
                                )
                                .padding(top = padding)) {
                                val showMusicPlayer = scaffoldState.bottomSheetState.currentValue.name == "PartiallyExpanded"
                                AnimatedContent(
                                    targetState = showMusicPlayer,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween()).togetherWith(fadeOut(animationSpec = tween(1)))
                                    },
                                    label = "SongViewToMusicPlayerTransition"
                                ) { isMusicPlayer ->
                                    if (!isMusicPlayer) {
                                        SongView(
                                            onEvent = { event ->
                                                vmMainScreen.onEvent(event)},
                                            state = mainState,
                                            animatedVisibilityScope = this
                                        )

                                    } else {
                                        MusicPlayer(
                                            onEvent = { event ->
                                                vmMainScreen.onEvent(event)},
                                            state = mainState,
                                            animatedVisibilityScope = this,
                                        )
                                    }
                                }
                            }
                        }
                    ) { }

                }

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
                        navController.navigate(CreateArtistScreen(artistId = ""))
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
                    description = "Create playlists so that you or maybe others can enjoy your songs",
                    onClick = {
                        navController.navigate(CreatePlaylistScreen(""))
                        closeBottomSheet()
                    }
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



