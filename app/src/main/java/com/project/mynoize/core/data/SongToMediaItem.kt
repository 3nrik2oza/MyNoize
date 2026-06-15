package com.project.mynoize.core.data

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.core.net.toUri
import com.project.mynoize.core.domain.entities.Song
import java.io.File


fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(localSongUrl ?: songUrl )
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artistName)
                .setAlbumTitle(albumName)
                .setArtworkUri(
                    localImageUrl?.let { Uri.fromFile(File(it)) } ?: imageUrl.toUri()
                )
                .setDescription(id)
                .build()
        )
        .build()
}
