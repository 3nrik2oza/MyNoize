package com.project.mynoize.activities.main.presentation.search_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenEvent
import com.project.mynoize.activities.main.presentation.playlist_screen.components.SongOptionsBottomSheet
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.SearchItem
import com.project.mynoize.core.presentation.components.CustomSearchBar
import com.project.mynoize.core.presentation.components.SelectPlaylistDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchScreenState = SearchScreenState(),
    onEvent: (SearchScreenEvent) -> Unit = {}
){

    val sheetState = rememberModalBottomSheetState()

    if(state.selectPlaylistSheet){
        SelectPlaylistDialog(
            playlists = state.userPlaylists.filter { !it.songs.contains(state.selectedSong!!.id) },
            onPlaylistClick = { onEvent(SearchScreenEvent.OnPlaylistSelected(it)) },
            onDismiss = { onEvent(SearchScreenEvent.OnToggleSelectPlaylistSheet) }
        )
    }

    Column(
        Modifier.background(Color.White).fillMaxSize()
            .padding(start = 10.dp, end = 10.dp, top= 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CustomSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = { onEvent(SearchScreenEvent.OnSearchQueryChange(it)) },
            onImeSearch = {},
            modifier = Modifier.width(295.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row{
            FloatingText("Songs", selected = state.selectedSearchType == SearchTypes.SONGS, onClick = { onEvent(SearchScreenEvent.OnSearchTypeChange(SearchTypes.SONGS))})
            Spacer(modifier = Modifier.width(10.dp))
            FloatingText("Artists", selected = state.selectedSearchType == SearchTypes.ARTISTS, onClick = { onEvent(SearchScreenEvent.OnSearchTypeChange(SearchTypes.ARTISTS))})
            Spacer(modifier = Modifier.width(10.dp))
            FloatingText("Albums", selected = state.selectedSearchType == SearchTypes.ALBUMS, onClick = { onEvent(SearchScreenEvent.OnSearchTypeChange(SearchTypes.ALBUMS))})
            Spacer(modifier = Modifier.width(10.dp))
            FloatingText("Playlists", selected = state.selectedSearchType == SearchTypes.PLAYLISTS, onClick = { onEvent(SearchScreenEvent.OnSearchTypeChange(SearchTypes.PLAYLISTS))})
        }

        Spacer(modifier = Modifier.height(10.dp))

        if(state.isLoading){
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }else {
            LazyColumn{
                itemsIndexed(state.searchItems){ _, item ->
                    SearchElement(item = item, onEvent = onEvent)
                }
            }
        }
    }

    if(state.isSheetOpen){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { onEvent(SearchScreenEvent.OnToggleMoreOptionsSheet) },
            containerColor = Color.White,
            shape = RectangleShape,
            dragHandle = {}
        ) {
            SongOptionsBottomSheet(
                song = state.selectedSong!!.song,
                artist = state.selectedArtist ?: Artist(),
                removeFromPlaylistButton = false,
                event = { event ->
                    when(event){
                        is PlaylistScreenEvent.OnArtistClick -> {
                            onEvent(SearchScreenEvent.OnToggleMoreOptionsSheet)
                            onEvent(SearchScreenEvent.OnSearchItemClicked(SearchItem.ArtistItem(state.selectedArtist!!)))
                        }
                        is PlaylistScreenEvent.OnSongFavoriteToggle -> onEvent(SearchScreenEvent.OnSearchItemFavoriteClicked(SearchItem.SongItem(state.selectedSong.song)))
                        is PlaylistScreenEvent.OnToggleSelectPlaylistSheet -> { onEvent(SearchScreenEvent.OnToggleSelectPlaylistSheet) }
                        is PlaylistScreenEvent.OnDismissAlertDialog -> { onEvent(SearchScreenEvent.OnToggleMoreOptionsSheet) }
                        else -> {}
                    }
                },
                isCreator = false
            )
        }
    }

}

@Preview
@Composable
fun PrevSearchScreen(){
    SearchScreen()
}

@Composable
fun SearchElement(
    item: SearchItem,
    onEvent: (SearchScreenEvent) -> Unit = {}
){
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.clickable(onClick = {
                onEvent(SearchScreenEvent.OnSearchItemClicked(item))
            }).fillMaxWidth().height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val painter = rememberAsyncImagePainter(model = item.imageUrl)

            val painterState = painter.state

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ){
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black, RectangleShape)
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ){
                    when(painterState){
                        is AsyncImagePainter.State.Loading ->{
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop,
                            )
                            CircularProgressIndicator()
                        }
                        is AsyncImagePainter.State.Error ->{
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "Error loading image"
                            )
                        }
                        else -> {
                            Image(
                                painter = painter,
                                contentDescription = "Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(Modifier.width(20.dp))

                Column{
                    Text(
                        text = item.title,
                        maxLines = 2,
                        fontFamily = NovaSquareFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = item.subtitle,
                        maxLines = 1,
                        fontFamily = NovaSquareFontFamily,
                        color = DarkGray.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            Row {

                Icon(
                    painter = painterResource(id = if(item.favorite) R.drawable.ic_heart else R.drawable.ic_heart_empty),
                    contentDescription = if(item.favorite) "Remove song from favorite" else "Add song to favorite",
                    modifier = Modifier.clickable{ onEvent(SearchScreenEvent.OnSearchItemFavoriteClicked(item)) }
                )

                if(item is SearchItem.SongItem){
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Add song to the playlist",
                        modifier = Modifier.clickable(onClick = {
                            onEvent(SearchScreenEvent.OnMoreOptionsSongClick(item))
                        })
                    )
                }


            }





        }
        Spacer(Modifier.height(35.dp))
    }
}

@Composable
fun FloatingText(
    text: String = "",
    selected: Boolean,
    onClick: () -> Unit,
){
    Box(
        Modifier
            .clickable{ onClick() }
            .background(color = if(selected) Color.White else Color.Transparent)
            .clip(RectangleShape)
            .border(1.dp, color = if(selected) Red else Color.Black, RectangleShape)
            .padding(vertical = 3.dp, horizontal = 5.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}