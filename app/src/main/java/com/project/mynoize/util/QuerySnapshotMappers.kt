package com.project.mynoize.util

import com.google.firebase.firestore.QuerySnapshot
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.firestore.entities.RemoteSong

fun QuerySnapshot.toPlaylists(): List<Playlist>{
    return this.documents.map { document ->
        document.toObject(Playlist::class.java)!!.apply {
            id = document.id
        }
    }
}

fun QuerySnapshot.toSongs(): List<RemoteSong>{
    return this.documents.map { document ->
        document.toObject(RemoteSong::class.java)!!.apply {
            id = document.id
        }
    }
}

fun QuerySnapshot.toAlbums(): List<Album>{
    return this.documents.map { document ->
        document.toObject(Album::class.java)!!.apply {
            id = document.id
        }
    }
}

fun QuerySnapshot.toArtists(): List<Artist>{
    return this.documents.map { document ->
        document.toObject(Artist::class.java)!!.apply {
            id = document.id
        }
    }
}