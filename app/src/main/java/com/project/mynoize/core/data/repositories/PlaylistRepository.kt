package com.project.mynoize.core.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.Error
import com.project.mynoize.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PlaylistRepository {

    private val db = FirebaseFirestore.getInstance()

    var lastLoadedPlaylists = listOf<Playlist>()

    var list : Flow<List<Playlist>> = db.collection(Constants.PLAYLIST_COLLECTION)
        .snapshots()
        .map { snapshots ->
            snapshots.documents.map { doc ->
                doc.toObject(Playlist::class.java)!!.copy(
                    id = doc.id
                )
            }
        }

    suspend fun updateSongsInPlaylist(songs: List<String>, id: String): Result<Unit, FbError.Firestore> {
        return try {
            val docRef = db.collection(Constants.PLAYLIST_COLLECTION)
                .document(id)
                .update(
                    mapOf(
                        "songs" to songs,
                    )
                )
                .await()
            lastLoadedPlaylists = lastLoadedPlaylists.map { playlist ->
                if(playlist.id == id) playlist.copy(songs = songs) else playlist
            }
            Log.d("PlaylistRepository", "updateSongsInPlaylist: ${lastLoadedPlaylists.find { it.id == id }?.songs }}")
            Log.d("PlaylistRepository", "songs: ${songs }}")
            Result.Success(Unit)
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
        }catch (_: Exception){
            Error(FbError.Firestore.UNKNOWN)
        }
    }

    suspend fun getPlaylist(userId: String): Result<List<Playlist>, FbError.Firestore> {
     return try {
         val snapshot = db.collection(Constants.PLAYLIST_COLLECTION)
             .whereEqualTo("creator", userId)
             .get()
             .await()

         val list = snapshot.documents.map { document ->
             document.toObject(Playlist::class.java)!!.apply {
                 id = document.id
             }
         }

         lastLoadedPlaylists = list
         Result.Success(list)
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
     }catch (_: Exception){
         Error(FbError.Firestore.UNKNOWN)
     }
    }

    suspend fun createPlaylist(playlist: Playlist): Result<Unit, FbError.Firestore> {
        return try {
            val docRef = db.collection(Constants.PLAYLIST_COLLECTION)
                .add(playlist)
                .await()

            Result.Success(Unit)
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
        }catch (_: Exception){
            Error(FbError.Firestore.UNKNOWN)
        }
    }
}