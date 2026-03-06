package com.project.mynoize.core.data.repositories

import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.database.SongDao
import com.project.mynoize.core.data.mappers.toLocalSongEntity
import com.project.mynoize.core.data.mappers.toSong
import com.project.mynoize.core.data.remote_data_source.SongRemoteDataSource
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.Error
import com.project.mynoize.core.domain.Result.Success
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class SongRepository(
    private val userRepository: UserRepository,
    private val localSongsDao: SongDao,
    private val remoteSource: SongRemoteDataSource
){


    @OptIn(ExperimentalCoroutinesApi::class)
    val favoriteSongsList: Flow<List<Song>> = userRepository.user
        .distinctUntilChanged()
        .flatMapLatest { user ->
        val favoritesIds = user.favoriteSongs

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            remoteSource.favoriteSongs(favoritesIds)
        }
    }

    suspend fun deleteLocalSong(id: String) = localSongsDao.deleteSong(id)

    suspend fun getAllLocalSongs(): List<Song> = localSongsDao.getAllSong().map { it.toSong() }

    suspend fun getSongsIdsInLocalAlbums(albumIds: List<String>): List<String> =
        localSongsDao.getSongsFromAlbumLists(albumIds)

    suspend fun getExistingSongs(songs: List<String>): List<String> = localSongsDao.getExistingSongIds(songs)

    suspend fun getMissingSongs(songsIds: List<String>): Result<List<Song>, FbError.Firestore>{
        val missingSongs = songsIds.toSet() - getExistingSongs(songsIds).toSet()
        return remoteSource.getSongsByIds(missingSongs.toList())
    }

    suspend fun saveSongLocally(song: Song){
        localSongsDao.upsertSong(song.toLocalSongEntity())
    }

    suspend fun getSongByArtist(artistId: String, connected: Boolean): Result<List<Song>, FbError.Firestore>{
        if(!connected){
            return Success(getLocalSongsFromArtist(artistId))
        }
        return remoteSource.getSongsByArtist(artistId)
    }

    suspend fun getLocalSongsFromArtist(artistId: String): List<Song> =
        localSongsDao.getSongsByArtistId(artistId).map { it.toSong() }

    suspend fun getLocalSongsAsPrimary(ids: List<String>): Result<List<Song>, FbError.Firestore> {
        val localSongs = getSongsByIdsLocal(ids)
        val orderMap = ids.withIndex().associate { it.value to it.index }
        val missing = ids.toSet() - localSongs.map { it.id }.toSet()
        if(missing.isEmpty()){
            return Success(localSongs.sortedBy { orderMap[it.id] })
        }

        var remoteSongs = emptyList<Song>()

        remoteSource.getSongsByIds(missing.toList()).onSuccess {
            remoteSongs = it
        }
        if(remoteSongs.isNotEmpty()){
            return Success((localSongs + remoteSongs).sortedBy { orderMap[it.id] })
        }

        return Error(FbError.Firestore.UNKNOWN)


    }

    suspend fun getSongsByIdsLocal(ids: List<String>): List<Song> =
        localSongsDao.getSongsByIds(ids).map { it.toSong() }


    suspend fun getSongByAuthors(ids: List<String>): Result<List<Song>, FbError.Firestore>{
        return remoteSource.getSongsByArtists(ids)
    }

    suspend fun getSongByAlbumId(albumId: String, downloaded: Boolean): Result<List<Song>, FbError.Firestore>{
        val localSongs = localSongsDao.getSongsByAlbumId(albumId).map { it.toSong() }

        if(downloaded){
            return Success(localSongs)
        }

        var merged = emptyList<Song>()
        val remoteSongsResult = remoteSource.getSongsByAlbumId(albumId).onSuccess {
            merged = it.map { song ->
                localSongs.find { local -> local.id == song.id } ?: song
            }
        }
        if(merged.isNotEmpty()){
            return Success(merged)
        }

        return remoteSongsResult
    }

    suspend fun getAllSongsContaining(q: String): Result<List<Song>, FbError.Firestore>{
        return remoteSource.getSongsContaining(q)
    }

    suspend fun addSongToFirebase(
        song: Song
    ): EmptyResult<FbError.Firestore>{
        return remoteSource.addSong(song)
    }
}
