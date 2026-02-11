package com.project.mynoize.core.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.database.AlbumDao
import com.project.mynoize.core.data.mappers.toAlbum
import com.project.mynoize.core.data.mappers.toLocalAlbumEntity
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.File

class AlbumRepository(
    private val userRepository: UserRepository,
    private val albumDao: AlbumDao,
    private val storageRepository: StorageRepository
) {
    val db = FirebaseFirestore.getInstance()

    @OptIn(ExperimentalCoroutinesApi::class)
    var remoteFavoriteAlbums: Flow<List<Album>> = userRepository.user.flatMapLatest { user ->
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val localFavoriteAlbums: Flow<List<Album>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoriteAlbums



        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            albumDao.getAlbumsFlowFromIds(favoritesIds).map { entities ->
                entities.map { it.toAlbum() }
            }
        }
    }

    fun getLocalAlbums(ids: List<String>): List<Album> = albumDao.getAlbumsFromIds(ids).map { it.toAlbum() }

    suspend fun doesAlbumExist(id: String): List<Album> = albumDao.getAlbumFromId(id).map { it.toAlbum() }

    suspend fun saveAlbumLocally(album: Album){
        albumDao.upsertAlbum(album.toLocalAlbumEntity())
    }

    suspend fun updateFavoriteAlbums(lastUpdateLocally: Long?): List<suspend () -> Unit> {
        val lastUpdatedRemote = userRepository.user.first().lastModifiedFavoriteAlbums.seconds
        val local = localFavoriteAlbums.first()
        val remote = remoteFavoriteAlbums.first()

        if(lastUpdateLocally == null || lastUpdateLocally < lastUpdatedRemote){
            return updateLocalAlbums(local, remote)
        }

        return emptyList()
    }

    fun updateLocalAlbums(local: List<Album>, remote: List<Album>): List<suspend () -> Unit> {
        val ops = mutableListOf<suspend () -> Unit>()
        val localMap =local.associateBy { it.id }
        val remoteMap = remote.associateBy { it.id }

        remoteMap.forEach { (id, remoteAlbum) ->
            val localItem = localMap[id]

            when{
                localItem == null -> {
                    ops.add {
                        var path = ""
                        storageRepository.downloadToLocalMemory(remoteAlbum.image, "album_image").onSuccess {
                            path = it
                        }
                        if(path != ""){
                            albumDao.upsertAlbum(remoteAlbum.copy(localImageUrl = path).toLocalAlbumEntity())
                        }// finish this function
                    }
                }
                localItem.lastModified < remoteAlbum.lastModified -> {
                    ops.add{
                        try {
                            var path = ""
                            File(localItem.localImageUrl).delete()
                            storageRepository.downloadToLocalMemory(remoteAlbum.image, "album_image").onSuccess {
                                path = it
                            }
                            if(path != ""){
                                albumDao.upsertAlbum(remoteAlbum.copy(localImageUrl = path).toLocalAlbumEntity())
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }


        return ops
    }


    suspend fun updateAlbumDownloadedField(id: String, songsDownloaded: Boolean){
        albumDao.updateDownloadField(id, songsDownloaded)
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

    fun getAlbum(albumPath: String) : Flow<Album>{
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

    suspend fun createAlbum(album: Album): Result<String,DataError.Remote> {
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

            Result.Success(albumRef.id)
        } catch (_: Exception) {
            Result.Error(DataError.Remote.SERVER)
        }
    }



}