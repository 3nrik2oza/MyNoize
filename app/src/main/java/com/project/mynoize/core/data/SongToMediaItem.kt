package com.project.mynoize.core.data

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.core.net.toUri


fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(localSongUrl.ifEmpty { "songUrl" })
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artistName)
                .setAlbumTitle(albumName)
                .setArtworkUri(imageUrl.toUri())
                .setDescription(id)
                .build()
        )
        .build()
}
