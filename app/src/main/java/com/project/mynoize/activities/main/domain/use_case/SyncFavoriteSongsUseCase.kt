package com.project.mynoize.activities.main.domain.use_case


import com.project.mynoize.core.data.repositories.SongRepository

class SyncFavoriteSongsUseCase(
    private val songRepository: SongRepository,
    private val downloadMissingSongUseCase: DownloadMissingSongUseCase
) {
    suspend operator fun invoke() {
        songRepository.favoriteSongsList.collect { songs ->
            val songsIds = songs.map { it.id }
            val localSongs = songRepository.getExistingSongs(songsIds)
            val missingSongsIds = songsIds.toSet() - localSongs.toSet()

            songs.filter { it.id in missingSongsIds }.forEach { downloadMissingSongUseCase.invoke(it)}
        }
    }
}