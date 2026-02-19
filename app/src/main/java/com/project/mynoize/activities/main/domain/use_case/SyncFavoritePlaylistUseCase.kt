package com.project.mynoize.activities.main.domain.use_case

import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncFavoritePlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val downloadMissingSong: DownloadMissingSongUseCase
) {
    suspend operator fun invoke(localUpdate: Long?)= withContext(Dispatchers.IO) {
        playlistRepository.userPlaylists.collect { playlists ->
            if(playlists.isEmpty()){
                return@collect
            }
            //val localUpdate = dataStore.lastModifiedFavPlaylists.first()?.toLong()
            val ops = playlistRepository.updateFavoritePlaylists(localUpdate)
            ops.forEach {
                it()
            }
            delay(1000)
            val localPlaylists = playlistRepository.localFavoritePlaylists.first()

            try {
                localPlaylists
                    .filter { !it.songsDownloaded }
                    .forEach { playlist ->
                        val songList = playlist.songs
                        val missingSongsIds = songList.toSet() - songRepository.getExistingSongs(songList).toSet()
                        var songs = emptyList<Song>()
                        songRepository.getSongsByIdsFirebase(missingSongsIds.toList()).onSuccess { listOfSong ->
                            songs = listOfSong
                        }.onError { return@onError }

                        songs.forEach { downloadMissingSong.invoke(it)  }
                        playlistRepository.setPlaylistAsDownloaded(playlist.id, true)
                    }
            }catch (e: Exception){
                print(e)
            }
        }
    }
}