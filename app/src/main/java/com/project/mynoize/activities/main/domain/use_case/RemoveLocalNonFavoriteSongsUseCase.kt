package com.project.mynoize.activities.main.domain.use_case

import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.io.File

class RemoveLocalNonFavoriteSongsUseCase(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(){
        delay(5_000)
        //get all local songs set
        val localSongs = songRepository.getAllLocalSongs()
        val localSongsMap = localSongs.associateBy { it.id }

        // favorite songs
        val favoriteSongs = songRepository.favoriteSongsList.first().map { it.id }.toSet()

        // favorite playlists songs
        val favoritePlaylists = playlistRepository.localFavoritePlaylists.first().filter { it.songsDownloaded }
        val songsFromPlaylist = favoritePlaylists.flatMap { it.songs }.toSet()

        // remove all that are in favorite albums
        val favoriteAlbums = albumRepository.localFavoriteAlbums.first().filter { it.songsDownloaded }.map { it.id }
        val songsFromAlbums = songRepository.getSongsIdsInLocalAlbums(favoriteAlbums).toSet()

        // remaining songs remove from local storage
        val songsToRemove = (localSongs.map { it.id } - favoriteSongs - songsFromPlaylist - songsFromAlbums).toList()
        songsToRemove.forEach { songId ->
            deleteLocalSong(localSongsMap[songId] ?: return@forEach)
        }
    }

    private suspend fun deleteLocalSong(song: Song){
        try {
            val songFile = File(song.localSongUrl)
            songFile.delete()
            songRepository.deleteLocalSong(song.id)
        }catch (e: Exception){
            print(e)
        }
    }
}