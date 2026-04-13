package com.project.mynoize.core.data.remote_data_source

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.firestore.safeFirestoreCall
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.Success
import com.project.mynoize.util.Constants
import com.project.mynoize.util.toArtists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ArtistRemoteDataSource(
    private val firestore: FirebaseFirestore
) {

    fun favoriteArtists(favoriteIds: List<String>): Flow<List<Artist>>{
        return firestore.collection(Constants.ARTIST_COLLECTION)
            .whereIn(FieldPath.documentId(), favoriteIds)
            .snapshots()
            .map { it.toArtists() }
    }

    suspend fun getArtists(): Result<List<Artist>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.ARTIST_COLLECTION)
                //.limit(50)
                .get()
                .await()
                .toArtists()
        }
    }

    suspend fun getArtist(artistId: String): Result<Artist, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.ARTIST_COLLECTION)
                .document(artistId)
                .get()
                .await()
                .toObject(Artist::class.java)!!.apply {
                    id = artistId
                }

        }
    }

    suspend fun getArtistContaining(q: String): Result<List<Artist>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.ARTIST_COLLECTION)
                .whereGreaterThanOrEqualTo("nameLower", q)
                .whereLessThan("nameLower", q + "\uf8ff")
                .limit(25)
                .get()
                .await()
                .toArtists()
        }
    }

    suspend fun createArtist(artist: Artist): EmptyResult<FbError.Firestore>{
        return safeFirestoreCall {
            val docRef = firestore.collection(Constants.ARTIST_COLLECTION).document()
            docRef.set(artist.copy(id=docRef.id, nameLower = artist.name.lowercase())).await()

            Result.Success(Unit)
        }
    }

    suspend fun updateArtist(artist: Artist): EmptyResult<FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.ARTIST_COLLECTION)
                .document(artist.id)
                .set(artist.copy(nameLower = artist.name.lowercase()))
                .await()

            Success(Unit)
        }
    }


}