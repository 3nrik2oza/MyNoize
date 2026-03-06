package com.project.mynoize.core.data.repositories


import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.database.PlaylistDao
import com.project.mynoize.core.data.mappers.toLocalPlaylistEntity
import com.project.mynoize.core.data.mappers.toPlaylist
import com.project.mynoize.core.data.remote_data_source.PlaylistRemoteDataSource
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.File
import kotlin.collections.map

class PlaylistRepository(
    private val auth: AuthRepository,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository,
    private val playlistDao: PlaylistDao,
    private val remoteSource: PlaylistRemoteDataSource
){

    val localFavoritePlaylists: Flow<List<Playlist>> = playlistDao.getPlaylists().map { entities ->
        entities.map { it.toPlaylist() }
    }

    suspend fun setPlaylistAsDownloaded(id: String, songsDownloaded: Boolean) {
        playlistDao.updateDownloadField(id, songsDownloaded)
    }


    val userPlaylists = remoteSource.userPlaylists(auth.getCurrentUserId())

    suspend fun updateFavoritePlaylists(lastUpdateLocally: Long?): List<suspend () -> Unit> {
        val lastUpdatedRemote = userRepository.user.first().lastModifiedFavoritePlaylists.seconds
        val local = localFavoritePlaylists.first()
        val remote = userPlaylists.first() + favoritePlaylists.first()

        if(lastUpdateLocally == null || lastUpdateLocally < lastUpdatedRemote){
            return updateLocalPlaylists(local, remote)
        }

        return emptyList()
    }

    private suspend fun updateLocalPlaylists(local: List<Playlist>, remote: List<Playlist>): List<suspend () -> Unit> {
        val ops = mutableListOf<suspend () -> Unit >()
        val localMap = local.associateBy { it.id }
        val remoteMap = remote.associateBy { it.id }

        remoteMap.forEach { (id, remotePlaylist) ->
            val localItem = localMap[id]

            when{
                localItem == null -> {
                    var path = ""
                    storageRepository.downloadToLocalMemory(remotePlaylist.imageLink, "playlist_image").onSuccess {
                        path = it
                    }
                    if(path != ""){
                        playlistDao.upsertPlaylist(remotePlaylist.toLocalPlaylistEntity(path))
                    }
                }
                localItem.lastModified.seconds < remotePlaylist.lastModified.seconds -> {
                    try {
                        var path = ""
                        File(localItem.localImagePath).delete()
                        storageRepository.downloadToLocalMemory(remotePlaylist.imageLink, "playlist_image").onSuccess {
                            path = it
                        }
                        if(path != ""){
                            playlistDao.upsertPlaylist(remotePlaylist.toLocalPlaylistEntity(path))
                        }
                    }catch (e: Exception){
                        if(e is CancellationException){
                            throw e
                        }
                    }
                }

            }
        }

        localMap.forEach { (id, localPlaylist) ->
            val remoteItem = remoteMap[id]

            when{
                remoteItem == null -> {
                    playlistDao.deletePlaylist(localPlaylist.toLocalPlaylistEntity())
                }
            }
        }
        return ops
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val favoritePlaylists: Flow<List<Playlist>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoritePlaylists

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            remoteSource.favoritePlaylists(favoritesIds)
        }
    }

    val favoriteSongsPlaylist: Flow<Playlist> = userRepository.user.map{ user ->
        Playlist(id = auth.getCurrentUserId(), name = "Favorites",
            creator = auth.getCurrentUserId(), songs = user.favoriteSongs)
    }

    val playlistsWithFavorites: Flow<List<Playlist>> =
        combine(
            localFavoritePlaylists,
            favoriteSongsPlaylist,
        ){ favoritePlaylists, favoriteSongs ->
            listOf(favoriteSongs) + favoritePlaylists
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPlaylist(id: String): Flow<Playlist> {
        return playlistDao.getPlaylist(id)
            .flatMapLatest { local ->
                when {
                    local != null -> flowOf(local.toPlaylist())
                    id == auth.getCurrentUserId() -> flowOf(favoriteSongsPlaylist.first())
                    else -> remoteSource.getPlaylist(id)
                }
            }
    }

    suspend fun getPlaylistsWithSongs(ids: List<String>): Result<List<Playlist>, FbError.Firestore>{
        return remoteSource.getPlaylistsWithSongs(ids)
    }



    suspend fun getRemotePlaylistsContaining(q: String): Result<List<Playlist>, FbError.Firestore> {
        return remoteSource.getPlaylistsContaining(q, auth.getCurrentUserId())
    }

    suspend fun updateSongsInRemotePlaylist(songs: List<String>, id: String): Result<Unit, FbError.Firestore> {
        return remoteSource.updateSongsInPlaylist(songs, id)
    }


    suspend fun createPlaylist(playlist: Playlist): Result<Unit, FbError.Firestore> {
        return remoteSource.createPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist): Result<Unit, FbError.Firestore> {
        return remoteSource.updatePlaylist(playlist)
    }

    suspend fun deletePlaylist(playlistId: String){
        remoteSource.deletePlaylist(playlistId)
    }
}

