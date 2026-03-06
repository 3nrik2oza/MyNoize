package com.project.mynoize.core.data.repositories


import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.database.AlbumDao
import com.project.mynoize.core.data.mappers.toAlbum
import com.project.mynoize.core.data.mappers.toLocalAlbumEntity
import com.project.mynoize.core.data.remote_data_source.AlbumRemoteDataSource
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.File

class AlbumRepository(
    private val userRepository: UserRepository,
    private val albumDao: AlbumDao,
    private val storageRepository: StorageRepository,
    private val remoteSource: AlbumRemoteDataSource
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    var remoteFavoriteAlbums: Flow<List<Album>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoriteAlbums

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            remoteSource.favoriteAlbums(favoritesIds)
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

    suspend fun updateLocalAlbums(local: List<Album>, remote: List<Album>): List<suspend () -> Unit> {
        val localMap =local.associateBy { it.id }
        val remoteMap = remote.associateBy { it.id }

        remoteMap.forEach { (id, remoteAlbum) ->
            val localItem = localMap[id]

            when{
                localItem == null -> {
                    var path = ""
                    storageRepository.downloadToLocalMemory(remoteAlbum.image, "album_image").onSuccess {
                        path = it
                    }
                    if(path != ""){
                        albumDao.upsertAlbum(remoteAlbum.copy(localImageUrl = path).toLocalAlbumEntity())
                    }// finish this function
                }
                localItem.lastModified < remoteAlbum.lastModified -> {
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
                        if(e is CancellationException){
                            throw e
                        }
                        e.printStackTrace()
                    }
                }
            }
        }


        return emptyList()
    }


    suspend fun updateAlbumDownloadedField(id: String, songsDownloaded: Boolean){
        albumDao.updateDownloadField(id, songsDownloaded)
    }

    suspend fun getAlbums(artistId: String) : Result<List<Album>, FbError.Firestore>{
        return remoteSource.getArtistAlbums(artistId)
    }

    fun getAlbum(artistId: String, albumId: String) : Flow<Album>{
        return remoteSource.getAlbum(artistId, albumId)

    }

    suspend fun getAllAlbumsContaining(q: String): Result<List<Album>, FbError.Firestore> {
        return remoteSource.getAllAlbumsContaining(q)
    }

    suspend fun createAlbum(album: Album): Result<String, FbError.Firestore> {
        return remoteSource.createAlbum(album)
    }



}