package com.project.mynoize.core.data.repositories


import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.Error
import com.project.mynoize.core.domain.Result.Success
import com.project.mynoize.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.collections.map

class PlaylistRepository(
    private val auth: AuthRepository,
    private val userRepository: UserRepository
){

    private val db = FirebaseFirestore.getInstance()

    var lastLoadedPlaylists = listOf<Playlist>()


    val userPlaylists: Flow<List<Playlist>> = db.collection(Constants.PLAYLIST_COLLECTION).whereEqualTo("creator", auth.getCurrentUserId())
        .snapshots()
        .map { snapshots ->
            snapshots.documents.map { doc ->
                doc.toObject(Playlist::class.java)!!.copy(
                    id = doc.id
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val favoritePlaylists: Flow<List<Playlist>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoritePlaylists

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            db.collection(Constants.PLAYLIST_COLLECTION)
                .whereIn(FieldPath.documentId(), favoritesIds)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.map { doc ->
                        doc.toObject(Playlist::class.java)!!.copy(
                            id = doc.id
                        )
                    }
                }
        }
    }

    var favoriteSongsPlaylist: Flow<Playlist> = userRepository.user.map{ user ->
        Playlist(id = auth.getCurrentUserId(), name = "Favorites",
            creator = auth.getCurrentUserId(), songs = user.favoriteSongs)
    }

    val playlistsWithFavorites: Flow<List<Playlist>> =
        combine(
            userPlaylists,
            favoriteSongsPlaylist,
            favoritePlaylists
        ){ createdPlaylists, favoriteSongs, favoritePlaylists ->
            listOf(favoriteSongs) + createdPlaylists + favoritePlaylists
        }


    fun getPlaylist(id: String): Flow<Playlist> {
        return db.collection(Constants.PLAYLIST_COLLECTION)
            .document(id)
            .snapshots()
            .map { snapshot ->
                val playlist = snapshot.toObject(Playlist::class.java)
                    ?: throw IllegalStateException("Playlist not found")

                playlist.copy(id = snapshot.id)
            }
    }

    suspend fun getPlaylistsWithSongs(ids: List<String>): Result<List<Playlist>, FbError.Firestore>{
        return try {
            val snapshot = db.collection(Constants.PLAYLIST_COLLECTION)
                .whereArrayContainsAny("songs", ids)
                .limit(5)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Playlist::class.java)!!.apply {
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
        }catch (_: Exception){
            Error(FbError.Firestore.UNKNOWN)
        }
    }



    suspend fun getPlaylistsContaining(q: String): Result<List<Playlist>, FbError.Firestore> {
        return try {
            val snapshot = db.collection(Constants.PLAYLIST_COLLECTION)
                .whereGreaterThanOrEqualTo("nameLower", q)
                .whereLessThan("nameLower", q + "\uf8ff")
                .whereNotEqualTo("creator", auth.getCurrentUserId())
                .limit(25)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Playlist::class.java)!!.apply {
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
        }catch (_: Exception){
            Error(FbError.Firestore.UNKNOWN)
        }
    }

    suspend fun updateSongsInPlaylist(songs: List<String>, id: String): Result<Unit, FbError.Firestore> {
        return try {
            val docRef = db.collection(Constants.PLAYLIST_COLLECTION)
                .document(id)
                .update( mapOf( "songs" to songs) )
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
        }catch (e: Exception){
            Log.d(e.toString(), "")
            Error(FbError.Firestore.UNKNOWN)
        }
    }


    suspend fun createPlaylist(playlist: Playlist): Result<Unit, FbError.Firestore> {
        return try {
            val docRef = db.collection(Constants.PLAYLIST_COLLECTION)
                .add(playlist.copy(nameLower = playlist.name.lowercase()))
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

    suspend fun updatePlaylist(playlist: Playlist): Result<Unit, FbError.Firestore> {
        return try {
            val docRef = db.collection(Constants.PLAYLIST_COLLECTION)
                .document(playlist.id)
                .set(playlist.copy(nameLower = playlist.name.lowercase()))
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




    fun deletePlaylist(playlistId: String){
        db.collection(Constants.PLAYLIST_COLLECTION).document(playlistId).delete()
    }
}

