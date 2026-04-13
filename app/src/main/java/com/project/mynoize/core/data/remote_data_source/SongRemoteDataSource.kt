package com.project.mynoize.core.data.remote_data_source

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.firestore.entities.RemoteSong
import com.project.mynoize.core.data.firestore.safeFirestoreCall
import com.project.mynoize.core.data.mappers.toSong
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.entities.Song
import com.project.mynoize.util.Constants
import com.project.mynoize.util.toSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SongRemoteDataSource(
    private val firestore: FirebaseFirestore
) {

    private val songCollection = Constants.SONG_COLLECTION

    fun favoriteSongs(favoriteIds: List<String>):Flow<List<Song>>{
        return firestore.collection(songCollection)
            .whereIn(FieldPath.documentId(), favoriteIds)
            .snapshots()
            .map { snapshot ->
                snapshot.toSongs()
                    .map { it.toSong() }
            }

    }

    suspend fun getSongsByArtist(artistId: String): Result<List<Song>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereEqualTo("artistId", artistId)
                .get()
                .await()
                .toSongs()
                .map { it.toSong() }
        }
    }

    suspend fun getSongsByIds(ids: List<String>): Result<List<Song>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()
                .toSongs()
                .map { it.toSong() }
        }
    }

    suspend fun getSongsByArtists(ids: List<String>): Result<List<Song>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereIn("artistId", ids)
                .limit(25)
                .get()
                .await()
                .toSongs()
                .map { it.toSong() }
        }
    }

    suspend fun getSongsByAlbumId(albumId: String): Result<List<Song>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereEqualTo("albumId", albumId)
                .get()
                .await()
                .toSongs()
                .map { it.toSong() }
        }
    }

    suspend fun getSongsContaining(q: String): Result<List<Song>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereGreaterThanOrEqualTo("titleLower", q)
                .whereLessThan("titleLower", q + "\uf8ff")
                .limit(25)
                .get()
                .await()
                .toSongs()
                .map { it.toSong() }
        }
    }

    suspend fun addSong(remoteSong: RemoteSong): EmptyResult<FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .add(remoteSong.copy(titleLower = remoteSong.title.lowercase()))
                .await()
        }

    }


}