package com.project.mynoize.activities.main.presentation.favorite_screen.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.project.mynoize.R
import com.project.mynoize.activities.main.presentation.favorite_screen.FavoriteScreenEvent
import com.project.mynoize.activities.main.ui.theme.LightGray
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.core.data.Playlist

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistScrollElement(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    onEvent: (FavoriteScreenEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
){
    Box(
        modifier = Modifier.padding(10.dp)
    ){
        Column{

            val painter = rememberAsyncImagePainter(model = playlist.imageLink)

            val painterState = painter.state



            when(painterState){
                is AsyncImagePainter.State.Loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = modifier){
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = modifier
                        )
                        CircularProgressIndicator()
                    }
                }
                is AsyncImagePainter.State.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = modifier
                            .border(1.dp, Color.Black)
                            .clickable(
                                onClick = {onEvent(FavoriteScreenEvent.OnPlaylistClicked(playlist.id))})
                    ){
                        if(playlist.name == "Favorites"){
                            Icon(
                                painter = painterResource(R.drawable.ic_heart),
                                contentDescription = "Playlist icon",
                                modifier = Modifier.size(40.dp)
                            )
                        }else{
                            Icon(
                                imageVector =  Icons.Default.BrokenImage,
                                contentDescription = "Error loading image"
                            )
                        }

                    }
                }
                else -> {
                    Box(
                        contentAlignment = Alignment.BottomStart
                    ){
                        Image(
                            painter = painter,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "Playlist image",
                            modifier = modifier
                                .border(1.dp, Color.Black)
                                .clickable(
                                    onClick = {onEvent(FavoriteScreenEvent.OnPlaylistClicked(playlist.id))})
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "image/${playlist.imageLink}"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = {_,_ ->
                                        tween(durationMillis = 300)
                                    }
                                )

                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_play), contentDescription = "Play button",
                            modifier = Modifier
                                .padding(10.dp)
                                .background(LightGray, CircleShape)
                                .padding(5.dp)
                                .size(10.dp),
                            tint = Color.Black
                        )

                    }
                }
            }


            Text(
                fontSize = 12.sp,
                text = playlist.name,
                fontFamily = NovaSquareFontFamily,
                maxLines = 1,
                modifier = modifier.then(Modifier.height(IntrinsicSize.Min))

            )
        }
    }


}
