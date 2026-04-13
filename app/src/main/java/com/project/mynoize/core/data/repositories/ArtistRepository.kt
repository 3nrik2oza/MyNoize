package com.project.mynoize.core.data.repositories

import com.project.mynoize.core.data.Artist
import com.project.mynoize.core.data.remote_data_source.ArtistRemoteDataSource
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.Result.*
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ArtistRepository(
    private val userRepository: UserRepository,
    private val remoteSource: ArtistRemoteDataSource
) {

    var loadedArtists: MutableSet<Artist> = mutableSetOf()

    @OptIn(ExperimentalCoroutinesApi::class)
    var favoriteArtists: Flow<List<Artist>> = userRepository.user.flatMapLatest { user ->
        val favoritesIds = user.favoriteArtists

        if(favoritesIds.isEmpty()){
            flowOf(emptyList())
        }else{
            remoteSource.favoriteArtists(favoritesIds)
        }
    }


    suspend fun getArtists(): Result<List<Artist>, FbError.Firestore> {
        return remoteSource.getArtists()
    }

    suspend fun getArtist(artistId: String): Result<Artist, FbError.Firestore> {
        val loadedArtist = loadedArtists.find { it.id == artistId }

        if(loadedArtist != null) return Success(loadedArtist)

        val getRemoteArtist = remoteSource.getArtist(artistId)
        getRemoteArtist.onSuccess {
            loadedArtists.add(it)
        }
        return getRemoteArtist

    }

    suspend fun getArtistsContaining(q: String): Result<List<Artist>, FbError.Firestore> {
        return remoteSource.getArtistContaining(q)

    }

    suspend fun createArtist(artist: Artist): EmptyResult<FbError.Firestore>{

        return remoteSource.createArtist(artist)

    }

    suspend fun updateArtist(artist: Artist): EmptyResult<FbError.Firestore>{
        val result = remoteSource.updateArtist(artist)
        result.onSuccess {
            loadedArtists.removeIf { it.id == artist.id }
            loadedArtists.add(artist)
        }
        return result


    }

}
