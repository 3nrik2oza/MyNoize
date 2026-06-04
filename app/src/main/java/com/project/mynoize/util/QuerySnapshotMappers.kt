package com.project.mynoize.util

import com.google.firebase.firestore.QuerySnapshot
import com.project.mynoize.core.data.firestore.entities.AlbumDto
import com.project.mynoize.core.data.firestore.entities.ArtistDto
import com.project.mynoize.core.data.firestore.entities.PlaylistDto
import com.project.mynoize.core.data.firestore.entities.SongDto

fun QuerySnapshot.toDtoPlaylists(): List<PlaylistDto>{
    return this.documents.map { document ->
        document.toObject(PlaylistDto::class.java)!!.apply {
            id = document.id
        }
    }
}

fun QuerySnapshot.toDtoSongs(): List<SongDto>{
    return this.documents.map { document ->
        document.toObject(SongDto::class.java)!!.apply {
            id = document.id
        }
    }
}

fun QuerySnapshot.toDtoAlbums(): List<AlbumDto>{
    return this.documents.map { document ->
        document.toObject(AlbumDto::class.java)!!.apply {
            id = document.id
        }
    }
}

fun QuerySnapshot.toDtoArtists(): List<ArtistDto>{
    return this.documents.map { document ->
        document.toObject(ArtistDto::class.java)!!.apply {
            id = document.id
        }
    }
}