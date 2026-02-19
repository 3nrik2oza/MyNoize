package com.project.mynoize.core.data.repositories

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.*
import com.project.mynoize.util.Constants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ArtistRepository(
    private val userRepository: UserRepository
) {
    private val db = FirebaseFirestore.getInstance()


    /*
    var artists: Flow<List<Artist>> = db.collection(Constants.ARTIST_COLLECTION)
        .snapshots()
        .map { snapshots ->
            snapshots.documents.map { doc ->
                doc.toObject(Artist::class.java)!!.copy(
                    id = doc.id
                )
            }
        }*/ // remove this and use loaded artists

    var loadedArtists: MutableSet<Artist> = mutableSetOf()

    @OptIn(ExperimentalCoroutinesApi::class)
    var favoriteArtists: Flow<List<Artist>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoriteArtists

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            db.collection(Constants.ARTIST_COLLECTION)
                .whereIn(FieldPath.documentId(), favoritesIds)
                .snapshots()
                .map { snapshot ->
                snapshot.toObjects(Artist::class.java)
                }
        }
    }


    suspend fun getArtists(): Result<List<Artist>, FbError.Firestore> {
        return try {
            val snapshot = db.collection(Constants.ARTIST_COLLECTION)
                .limit(25)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Artist::class.java)!!.apply {
                    id = document.id
                }
            })
        }catch (e: FirebaseFirestoreException){
            when(e.code){
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> Error(FbError.Firestore.PERMISSION_DENIED)
                FirebaseFirestoreException.Code.UNAVAILABLE -> Error(FbError.Firestore.UNAVAILABLE)
                FirebaseFirestoreException.Code.ABORTED -> Error(FbError.Firestore.ABORTED)
                FirebaseFirestoreException.Code.NOT_FOUND -> Error(FbError.Firestore.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Error(FbError.Firestore.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Error(FbError.Firestore.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.CANCELLED -> Error(FbError.Firestore.CANCELLED)
                else -> Error(FbError.Firestore.UNKNOWN)
            }
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(FbError.Firestore.UNKNOWN)
        }

    }

    suspend fun getArtist(artistId: String): Result<Artist, FbError.Firestore> {
        val loadedArtist = loadedArtists.find { it.id == artistId }

        if(loadedArtist != null) return Success(loadedArtist)

        return try {
            val snapshot = db.collection(Constants.ARTIST_COLLECTION).document(artistId)
                .get()
                .await()

            val artist = snapshot.toObject(Artist::class.java)!!.copy(id = snapshot.id)

            loadedArtists.add(artist)

            return Success(artist)
        }catch (e: FirebaseFirestoreException){
            when(e.code){
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> Error(FbError.Firestore.PERMISSION_DENIED)
                FirebaseFirestoreException.Code.UNAVAILABLE -> Error(FbError.Firestore.UNAVAILABLE)
                FirebaseFirestoreException.Code.ABORTED -> Error(FbError.Firestore.ABORTED)
                FirebaseFirestoreException.Code.NOT_FOUND -> Error(FbError.Firestore.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Error(FbError.Firestore.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Error(FbError.Firestore.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.CANCELLED -> Error(FbError.Firestore.CANCELLED)
                else -> Error(FbError.Firestore.UNKNOWN)
            }
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(FbError.Firestore.UNKNOWN)
        }

    }

    suspend fun getArtistsContaining(q: String): Result<List<Artist>, FbError.Firestore> {
        return try {
            val snapshot = db.collection(Constants.ARTIST_COLLECTION)
                .whereGreaterThanOrEqualTo("nameLower", q)
                .whereLessThan("nameLower", q + "\uf8ff")
                .limit(25)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Artist::class.java)!!.apply {
                    id = document.id
                }
            })
        }catch (e: FirebaseFirestoreException){
            when(e.code){
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> Error(FbError.Firestore.PERMISSION_DENIED)
                FirebaseFirestoreException.Code.UNAVAILABLE -> Error(FbError.Firestore.UNAVAILABLE)
                FirebaseFirestoreException.Code.ABORTED -> Error(FbError.Firestore.ABORTED)
                FirebaseFirestoreException.Code.NOT_FOUND -> Error(FbError.Firestore.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Error(FbError.Firestore.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Error(FbError.Firestore.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.CANCELLED -> Error(FbError.Firestore.CANCELLED)
                else -> Error(FbError.Firestore.UNKNOWN)
            }
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(FbError.Firestore.UNKNOWN)
        }

    }

    suspend fun createArtist(artist: Artist): EmptyResult<FbError.Firestore>{
        return try{
            val docRef = db.collection(Constants.ARTIST_COLLECTION).document()

            docRef.set(artist.copy(id = docRef.id,nameLower = artist.name.lowercase())).await()

            Success(Unit)
        }catch (e: FirebaseFirestoreException){
            when(e.code){
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> Error(FbError.Firestore.PERMISSION_DENIED)
                FirebaseFirestoreException.Code.UNAVAILABLE -> Error(FbError.Firestore.UNAVAILABLE)
                FirebaseFirestoreException.Code.ABORTED -> Error(FbError.Firestore.ABORTED)
                FirebaseFirestoreException.Code.NOT_FOUND -> Error(FbError.Firestore.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Error(FbError.Firestore.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Error(FbError.Firestore.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.CANCELLED -> Error(FbError.Firestore.CANCELLED)
                else -> Error(FbError.Firestore.UNKNOWN)
            }
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(FbError.Firestore.UNKNOWN)
        }

    }

    suspend fun updateArtist(artist: Artist): EmptyResult<FbError.Firestore>{
        return try{
            val docRef = db.collection(Constants.ARTIST_COLLECTION)
                .document(artist.id)
                .set(artist.copy(nameLower = artist.name.lowercase()))
                .await()

            loadedArtists.removeIf { it.id == artist.id }
            loadedArtists.add(artist)
            Success(Unit)
        }catch (e: FirebaseFirestoreException){
            when(e.code){
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> Error(FbError.Firestore.PERMISSION_DENIED)
                FirebaseFirestoreException.Code.UNAVAILABLE -> Error(FbError.Firestore.UNAVAILABLE)
                FirebaseFirestoreException.Code.ABORTED -> Error(FbError.Firestore.ABORTED)
                FirebaseFirestoreException.Code.NOT_FOUND -> Error(FbError.Firestore.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Error(FbError.Firestore.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Error(FbError.Firestore.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.CANCELLED -> Error(FbError.Firestore.CANCELLED)
                else -> Error(FbError.Firestore.UNKNOWN)
            }
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(FbError.Firestore.UNKNOWN)
        }

    }

}
