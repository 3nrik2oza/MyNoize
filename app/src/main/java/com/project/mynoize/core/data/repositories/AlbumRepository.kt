package com.project.mynoize.core.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.Result
import com.project.mynoize.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AlbumRepository(
    private val userRepository: UserRepository
) {
    val db = FirebaseFirestore.getInstance()

    @OptIn(ExperimentalCoroutinesApi::class)
    var favoriteAlbums: Flow<List<Album>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoriteAlbums

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            db.collectionGroup(Constants.ALBUM_COLLECTION)
                .whereIn("id", favoritesIds)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Album::class.java)
                }

        }
    }


    suspend fun getAlbums(artistId: String) : Result<List<Album>, DataError.Remote>{
        return try {
            val snapshot = db.collection(Constants.ARTIST_COLLECTION)
                .document(artistId).collection(Constants.ALBUM_COLLECTION)
                .get()
                .await()

            val list = snapshot.documents.map{ document ->
                document.toObject(Album::class.java)!!.apply{
                    id = document.id
                }
            }

            Result.Success(list)

        }catch (_: Exception){
            Result.Error(DataError.Remote.SERVER)
        }
    }

    suspend fun getAlbum(albumPath: String) : Flow<Album>{
        val path = albumPath.split("/")
        return db.collection(Constants.ARTIST_COLLECTION)
                .document(path[0])
                .collection(Constants.ALBUM_COLLECTION)
                .document(path[1])
                .snapshots()
                .map { snapshot ->
                    snapshot.toObject<Album>()?.copy(id = snapshot.id) ?: Album()
                }

    }

    suspend fun getAllAlbumsContaining(q: String): Result<List<Album>, DataError.Remote> {
        return try {
            val snapshot = db
                .collectionGroup(Constants.ALBUM_COLLECTION)
                .whereGreaterThanOrEqualTo("nameLower", q)
                .whereLessThan("nameLower", q + "\uf8ff")
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { document ->
                document.toObject(Album::class.java)?.apply {
                    id = document.id
                }
            }

            Result.Success(list)

        } catch (_: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }

    suspend fun createAlbum(album: Album): EmptyResult<DataError.Remote>{
        return try {
            val albumRef = db.collection(Constants.ARTIST_COLLECTION)
                .document(album.artist)
                .collection(Constants.ALBUM_COLLECTION)
                .document()

            val albumWithId = album.copy(
                id = albumRef.id,
                nameLower = album.name.lowercase()
            )

            albumRef.set(albumWithId).await()

            Result.Success(Unit)
        }catch (_: Exception){
            Result.Error(DataError.Remote.SERVER)
        }
    }



}