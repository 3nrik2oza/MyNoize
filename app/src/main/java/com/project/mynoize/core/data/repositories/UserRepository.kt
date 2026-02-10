package com.project.mynoize.core.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.User
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.Error
import com.project.mynoize.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: AuthRepository,
) {

    private val db = FirebaseFirestore.getInstance()

    var user : Flow<User> = db.collection(Constants.USER_COLLECTION).document(auth.getCurrentUserId())
        .snapshots().map { snapshot ->
        snapshot.toObject(User::class.java) ?: User()
    }

    suspend fun updateFavoriteArtists(artistId: String, favorite: Boolean): EmptyResult<FbError.Firestore>{
        val user = user.first()
        val updatedUser = user.copy(favoriteArtists = if(!favorite) user.favoriteArtists + artistId else user.favoriteArtists - artistId)
        return updateUser(updatedUser)
    }

    suspend fun updateFavoriteSongs(songId: String, favorite: Boolean): EmptyResult<FbError.Firestore>{
        val user = user.first()
        val updatedUser = user.copy(favoriteSongs = if(!favorite) user.favoriteSongs + songId else user.favoriteSongs - songId)
        return updateUser(updatedUser)
    }

    suspend fun updateFavoritePlaylist(playlistId: String, favorite: Boolean, time: Timestamp): EmptyResult<FbError.Firestore>{
        val user = user.first()
        val updatedUser = user.copy(
            favoritePlaylists = if(!favorite) user.favoritePlaylists + playlistId else user.favoritePlaylists - playlistId,
            lastModifiedFavoritePlaylists = time
        )
        return updateUser(updatedUser)
    }

    suspend fun updateFavoriteAlbums(albumId: String, favorite: Boolean): EmptyResult<FbError.Firestore> {
        val user = user.first()
        val updatedUser = user.copy(favoriteAlbums = if(!favorite) user.favoriteAlbums + albumId else user.favoriteAlbums - albumId)
        return updateUser(updatedUser)
    }

    suspend fun updateUser(user: User): EmptyResult<FbError.Firestore> {
        return try {
            val docRef = db.collection(Constants.USER_COLLECTION)
                .document(auth.getCurrentUserId())
                .set(user)
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
        }catch (_ : Exception){
            Error(FbError.Firestore.UNKNOWN)
        }

    }


}