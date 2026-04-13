package com.project.mynoize.activities.main.domain.use_case

import com.project.mynoize.activities.main.domain.RestoreSessionError
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.entities.Song
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.flow.firstOrNull

class RestoreLastSessionUseCase(
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository
) {

    suspend operator fun invoke(playlistId: String): Result<List<Song>, RestoreSessionError> {
        try {
            val playlist = playlistRepository.getPlaylist(playlistId)
                .firstOrNull() ?: return Result.Error(RestoreSessionError.PlaylistNotFound)

            var remoteSongs: List<Song>? = null
            songRepository.getLocalSongsAsPrimary(ids = playlist.songs)
                .onSuccess { remoteSongs = it }

            if(remoteSongs == null){
                return Result.Error(RestoreSessionError.SongsNotFound)
            }
            return Result.Success(remoteSongs)
        }catch (_: Exception){
            return Result.Error(RestoreSessionError.Unknown)
        }
    }
}