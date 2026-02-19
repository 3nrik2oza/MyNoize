package com.project.mynoize.core.data.repositories

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
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
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.util.Constants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SongRepository(
    private val userRepository: UserRepository,
    private val localSongsDao: SongDao
){
    private val db = FirebaseFirestore.getInstance()


    @OptIn(ExperimentalCoroutinesApi::class)
    val favoriteSongsList: Flow<List<Song>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoriteSongs

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            db.collection(Constants.SONG_COLLECTION)
                .whereIn(FieldPath.documentId(), favoritesIds)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.map { doc ->
                        doc.toObject(Song::class.java)!!.copy(
                            id = doc.id
                        )
                    }
                }
        }
    }

    suspend fun deleteLocalSong(id: String) = localSongsDao.deleteSong(id)

    suspend fun getAllLocalSongs(): List<Song> = localSongsDao.getAllSong().map { it.toSong() }

    suspend fun getSongsIdsInLocalAlbums(albumIds: List<String>): List<String> = localSongsDao.getSongsFromAlbumLists(albumIds)

    suspend fun getExistingSongs(songs: List<String>): List<String> = localSongsDao.getExistingSongIds(songs)

    suspend fun saveSongLocally(song: Song){
        localSongsDao.upsertSong(song.toLocalSongEntity())
    }

    suspend fun getSongByArtist(artistId: String, connected: Boolean): Result<List<Song>, FbError.Firestore>{
        if(!connected){
            return Success(getLocalSongsFromArtist(artistId))
        }
        return getSongByArtistFirebase(artistId)
    }

    suspend fun getSongByArtistFirebase(artistId: String): Result<List<Song>, FbError.Firestore>{
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
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(FbError.Firestore.UNKNOWN)
        }
    }

    suspend fun getLocalSongsFromArtist(artistId: String): List<Song> = localSongsDao.getSongsByArtistId(artistId).first().map { it.toSong() }

    suspend fun getLocalSongsAsPrimary(ids: List<String>): Result<List<Song>, FbError.Firestore> {
        val localSongs = getSongsByIdsLocal(ids)
        val missing = ids.toSet() - localSongs.map { it.id }.toSet()
        if(missing.isEmpty()){
            return Success(localSongs)
        }

        var remoteSongs = emptyList<Song>()
        getSongsByIdsFirebase(missing.toList()).onSuccess {
            remoteSongs = it
        }
        if(remoteSongs.isNotEmpty()){
            return Success((localSongs + remoteSongs).sortedBy { ids.indexOf(it.id) })
        }

        return Error(FbError.Firestore.UNKNOWN)


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
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
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
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(FbError.Firestore.UNKNOWN)
        }
    }

    suspend fun getSongByAlbumId(albumId: String, downloaded: Boolean): Result<List<Song>, FbError.Firestore>{
        val localSongs = localSongsDao.getSongsByAlbumId(albumId).first().map { it.toSong() }

        if(downloaded){
            return Success(localSongs)
        }

        return try {
            val snapshot = db.collection(Constants.SONG_COLLECTION)
                .whereEqualTo("albumId", albumId)
                .get()
                .await()

            val albumSongsRemote = snapshot.documents.map{ document ->
                document.toObject(Song::class.java)!!.apply {
                    id = document.id
                }
            }

            val merged = albumSongsRemote.map { remote ->
                localSongs.find { it.id == remote.id } ?: remote
            }


            return Success(merged)
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
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
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
        }catch (e: Exception){
            if(e is CancellationException){
                throw e
            }
            Error(DataError.Remote.SERVER)
        }

    }
}
