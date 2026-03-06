package com.project.mynoize.core.data.remote_data_source

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.firestore.safeFirestoreCall
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.util.Constants
import com.project.mynoize.util.toPlaylists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.collections.map

class PlaylistRemoteDataSource(
    private val firestore: FirebaseFirestore
) {

    fun userPlaylists(userId: String): Flow<List<Playlist>> {
        return firestore.collection(Constants.PLAYLIST_COLLECTION)
            .whereEqualTo("creator", userId)
            .snapshots()
            .map { snapshots ->
                snapshots.toPlaylists()
            }
    }

    fun favoritePlaylists(favoriteIds: List<String>): Flow<List<Playlist>>{
        return firestore.collection(Constants.PLAYLIST_COLLECTION)
            .whereIn(FieldPath.documentId(), favoriteIds)
            .snapshots()
            .map { it.toPlaylists() }
    }

    fun getPlaylist(playlistId: String): Flow<Playlist>{
       return firestore.collection(Constants.PLAYLIST_COLLECTION)
           .document(playlistId)
           .snapshots()
           .map { snapshot ->
               val playlist = snapshot.toObject(Playlist::class.java) ?: throw IllegalStateException("Playlist not found")

               playlist.copy(id = snapshot.id)
           }

    }

    suspend fun getPlaylistsWithSongs(ids: List<String>): Result<List<Playlist>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.PLAYLIST_COLLECTION)
            .whereArrayContainsAny("songs", ids)
            .limit(5)
            .get()
            .await()
                .documents.map {
                    it.toObject(Playlist::class.java)!!.apply {
                        id = it.id
                    }
                }
        }
    }

    suspend fun getPlaylistsContaining(q: String, userId: String) : Result<List<Playlist>, FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.PLAYLIST_COLLECTION)
                .whereGreaterThanOrEqualTo("nameLower", q)
                .whereLessThan("nameLower", q + "\uf8ff")
                .whereNotEqualTo("creator", userId)
                .limit(25)
                .get()
                .await()
                .toPlaylists()
        }
    }

    suspend fun updateSongsInPlaylist(songs: List<String>, id: String): EmptyResult<FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.PLAYLIST_COLLECTION)
                .document(id)
                .update( mapOf( "songs" to songs, "lastModified" to Timestamp.now()) )
                .await()

            Result.Success(Unit)
        }
    }

    suspend fun createPlaylist(playlist: Playlist): EmptyResult<FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.PLAYLIST_COLLECTION)
                .add(playlist.copy(nameLower = playlist.name.lowercase()))
                .await()

            Result.Success(Unit)
        }
    }

    suspend fun updatePlaylist(playlist: Playlist): EmptyResult<FbError.Firestore>{
        return safeFirestoreCall {
            firestore.collection(Constants.PLAYLIST_COLLECTION)
                .document(playlist.id)
                .set(playlist.copy(nameLower = playlist.nameLower.lowercase()))
                .await()

            Result.Success(Unit)
        }
    }

    suspend fun deletePlaylist(playlistId: String): EmptyResult<FbError.Firestore>{
        safeFirestoreCall {
            firestore.collection(Constants.PLAYLIST_COLLECTION)
                .document(playlistId)
                .delete()
                .await()
        }
        return Result.Success(Unit)
    }

}