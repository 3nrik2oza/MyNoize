package com.project.mynoize.activities.main.domain.use_case

import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncFavoriteAlbumsUseCase(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
    private val downloadMissingSong: DownloadMissingSongUseCase
) {
    suspend operator fun invoke(localUpdate: Long?) = withContext(Dispatchers.IO){
        try {
            albumRepository.remoteFavoriteAlbums.collect { albums ->
                if(albums.isEmpty()){
                    return@collect
                }
                val ops = albumRepository.updateFavoriteAlbums(localUpdate)
                ops.forEach { it() }

                delay(2000)
                val localAlbums = albumRepository.localFavoriteAlbums.first()

                localAlbums.filter { !it.songsDownloaded }
                    .forEach { album ->
                    var albumSongs = emptyList<Song>()
                    songRepository.getSongByAlbumId(album.id, album.songsDownloaded).onSuccess {
                        albumSongs = it
                    }
                    val songsInAlbumIds = albumSongs.map { it.id }

                    val missingSongsIds = songsInAlbumIds.toSet() - songRepository.getExistingSongs(songsInAlbumIds).toSet()

                    albumSongs.filter { it.id in missingSongsIds }.forEach {
                        downloadMissingSong(it, album.localImageUrl).onError {
                            return@collect
                        }
                    }

                    albumRepository.updateAlbumDownloadedField(album.id, true)
                }
            }
        }catch (e: Exception){
            print(e)
        }
    }
}