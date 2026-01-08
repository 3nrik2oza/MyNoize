package com.project.mynoize.core.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.Result
import com.project.mynoize.util.Constants
import kotlinx.coroutines.tasks.await

class AlbumRepository(

) {
    val db = FirebaseFirestore.getInstance()

    suspend fun getAlbum(artistId: String) : Result<List<Album>, DataError.Remote>{
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

        }catch (e: Exception){
            Result.Error(DataError.Remote.SERVER)
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

        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }

    suspend fun createAlbum(album: Album): EmptyResult<DataError.Remote>{
        return try {
            val snapshot = db.collection(Constants.ARTIST_COLLECTION)
                .document(album.artist)
                .collection(Constants.ALBUM_COLLECTION)
                .add(album.copy(nameLower = album.name.lowercase()))
                .await()

            Result.Success(Unit)
        }catch (e: Exception){
            Result.Error(DataError.Remote.SERVER)
        }
    }



}