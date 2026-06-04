package com.project.mynoize.core.data.remote_data_source

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.firestore.entities.AlbumDto
import com.project.mynoize.core.data.firestore.safeFirestoreCall
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.util.Constants
import com.project.mynoize.util.toDtoAlbums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AlbumRemoteDataSource(
    private val firestore: FirebaseFirestore
) {

    fun favoriteAlbums(favoriteIds: List<String>): Flow<List<AlbumDto>> {
        return firestore.collection(Constants.ALBUM_COLLECTION)
            .whereIn(FieldPath.documentId(), favoriteIds)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(AlbumDto::class.java)
            }
    }

    fun getAlbum(artistId: String, albumId: String): Flow<AlbumDto?>{
        return firestore.collection(Constants.ARTIST_COLLECTION)
            .document(artistId)
            .collection(Constants.ALBUM_COLLECTION)
            .document(albumId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(AlbumDto::class.java)
            }

    }

    suspend fun getArtistAlbums(artistId: String) : Result<List<AlbumDto>, FbError.Firestore> {
        return safeFirestoreCall {
            firestore.collection(Constants.ARTIST_COLLECTION)
                .document(artistId)
                .collection(Constants.ALBUM_COLLECTION)
                .get()
                .await()
                .toDtoAlbums()
        }
    }

    suspend fun getAllAlbumsContaining(q: String): Result<List<AlbumDto>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collectionGroup(Constants.ALBUM_COLLECTION)
                .whereGreaterThanOrEqualTo("nameLower", q)
                .whereLessThan("nameLower", q+"\uf8ff")
                .get()
                .await()
                .toDtoAlbums()
        }
    }

    suspend fun createAlbum(album: AlbumDto): Result<String, FbError.Firestore>{
        return safeFirestoreCall {
            val albumRef = firestore.collection(Constants.ARTIST_COLLECTION)
                .document(album.artist)
                .collection(Constants.ALBUM_COLLECTION)
                .document()

            val albumWithId = album.copy(
                id = albumRef.id,
            )

            albumRef.set(albumWithId).await()

            albumRef.id
        }
    }
}