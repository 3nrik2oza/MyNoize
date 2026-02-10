package com.project.mynoize.core.data.repositories

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.database.SongDao
import com.project.mynoize.core.data.mappers.toLocalSongEntity
import com.project.mynoize.core.data.mappers.toSong
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.Error
import com.project.mynoize.core.domain.Result.Success
import com.project.mynoize.util.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class SongRepository(
    private val localSongsDao: SongDao
){
    private val db = FirebaseFirestore.getInstance()


    suspend fun addSongToLocalMemory(song: Song){
        localSongsDao.upsertSong(song.toLocalSongEntity())
    }

    suspend fun getExistingSongs(songs: List<String>): List<String> = localSongsDao.getExistingSongIds(songs)

    suspend fun saveSongLocally(song: Song){
        localSongsDao.upsertSong(song.toLocalSongEntity())
    }

    suspend fun getSongByArtist(artistId: String): Result<List<Song>, FbError.Firestore>{
        return try {
            val snapshot = db.collection(Constants.SONG_COLLECTION)
                .whereEqualTo("artistId", artistId)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Song::class.java)!!.apply {
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

    suspend fun getSongsByIds(ids: List<String>, downloaded: Boolean): Result<List<Song>, FbError.Firestore>{
        if(downloaded){
            val localSongs = getSongsByIdsLocal(ids)
            return Result.Success(localSongs)
        }
        return getSongsByIdsFirebase(ids)
    }

    suspend fun getSongsByIdsLocal(ids: List<String>): List<Song> = localSongsDao.getSongsByIds(ids).first().map { it.toSong() }

    suspend fun getSongsByIdsFirebase(ids: List<String>): Result<List<Song>, FbError.Firestore>{
        return try {
            val snapshot = db.collection(Constants.SONG_COLLECTION)
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Song::class.java)!!.apply {
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

    suspend fun getSongByAuthors(ids: List<String>): Result<List<Song>, FbError.Firestore>{
        return try {
            val snapshot = db.collection(Constants.SONG_COLLECTION)
                .whereIn("artistId", ids)
                .limit(25)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Song::class.java)!!.apply {
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

    suspend fun getSongByAlbumId(albumId: String): Result<List<Song>, FbError.Firestore>{
        return try {
            val snapshot = db.collection(Constants.SONG_COLLECTION)
                .whereEqualTo("albumId", albumId)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Song::class.java)!!.apply {
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

    suspend fun getAllSongs(): Result<List<Song>, FbError.Firestore>{
        return try {
            val snapshot = db.collection(Constants.SONG_COLLECTION)
                .limit(25)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Song::class.java)!!.apply {
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

    suspend fun getAllSongsContaining(q: String): Result<List<Song>, FbError.Firestore>{
        return try {
            val snapshot = db.collection(Constants.SONG_COLLECTION)
                .whereGreaterThanOrEqualTo("titleLower", q)
                .whereLessThan("titleLower", q + "\uf8ff")
                .limit(25)
                .get()
                .await()

            return Success(snapshot.documents.map{ document ->
                document.toObject(Song::class.java)!!.apply {
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

    suspend fun addSongToFirebase(
        song: Song
    ): EmptyResult<DataError.Remote>{
        return  try {
            val docRef = db.collection(Constants.SONG_COLLECTION)
                .add(song.copy(titleLower = song.title.lowercase()))
                .await()

            Success(Unit)
        }catch (_: Exception){
            Error(DataError.Remote.SERVER)
        }

    }
}
