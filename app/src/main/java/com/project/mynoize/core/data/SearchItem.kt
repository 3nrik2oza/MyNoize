package com.project.mynoize.core.data

sealed class SearchItem(
    open val id: String = "",
    open val title: String,
    open val subtitle: String,
    open val imageUrl: String,
    open val favorite: Boolean,
) {
    data class AlbumItem(
        val album: Album
    ): SearchItem(
        id = album.id,
        title = album.name,
        subtitle = "",
        imageUrl = album.image,
        favorite = album.favorite
    )

    data class ArtistItem(
        val artist: Artist
    ): SearchItem(
        id = artist.id,
        title = artist.name,
        subtitle = "",
        imageUrl = artist.imageLink,
        favorite = artist.favorite
    )

    data class SongItem(
        val song: Song
    ): SearchItem(
        id = song.id,
        title = song.title,
        subtitle = song.artistName,
        imageUrl = song.imageUrl,
        favorite = song.favorite
    )

    data class PlaylistItem(
        val playlist: Playlist
    ): SearchItem(
        id = playlist.id,
        title = playlist.name,
        subtitle = "",
        imageUrl = playlist.imageLink,
        favorite = playlist.favorite
    )
}