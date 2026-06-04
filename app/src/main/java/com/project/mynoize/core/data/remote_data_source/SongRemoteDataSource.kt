package com.project.mynoize.core.data.remote_data_source

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.firestore.entities.SongDto
import com.project.mynoize.core.data.firestore.safeFirestoreCall
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.util.Constants
import com.project.mynoize.util.toDtoSongs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SongRemoteDataSource(
    private val firestore: FirebaseFirestore
) {

    private val songCollection = Constants.SONG_COLLECTION

    fun favoriteSongs(favoriteIds: List<String>):Flow<List<SongDto>>{
        return firestore.collection(songCollection)
            .whereIn(FieldPath.documentId(), favoriteIds)
            .snapshots()
            .map { snapshot ->
                snapshot.toDtoSongs()
            }

    }

    suspend fun getSongsByArtist(artistId: String): Result<List<SongDto>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereEqualTo("artistId", artistId)
                .get()
                .await()
                .toDtoSongs()
        }
    }

    suspend fun getSongsByIds(ids: List<String>): Result<List<SongDto>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()
                .toDtoSongs()
        }
    }

    suspend fun getSongsByArtists(ids: List<String>): Result<List<SongDto>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereIn("artistId", ids)
                .limit(25)
                .get()
                .await()
                .toDtoSongs()
        }
    }

    suspend fun getSongsByAlbumId(albumId: String): Result<List<SongDto>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereEqualTo("albumId", albumId)
                .get()
                .await()
                .toDtoSongs()
        }
    }

    suspend fun getSongsContaining(q: String): Result<List<SongDto>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .whereGreaterThanOrEqualTo("titleLower", q)
                .whereLessThan("titleLower", q + "\uf8ff")
                .limit(25)
                .get()
                .await()
                .toDtoSongs()
        }
    }

    suspend fun addSong(remoteSong: SongDto): EmptyResult<FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(songCollection)
                .add(remoteSong.copy(titleLower = remoteSong.title.lowercase()))
                .await()
        }

    }


}