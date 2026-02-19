package com.project.mynoize.activities.main.domain.use_case

import com.project.mynoize.activities.main.domain.SyncError
import com.project.mynoize.core.data.Album
import com.project.mynoize.core.data.Song
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onError
import com.project.mynoize.core.domain.onSuccess
import kotlinx.coroutines.flow.first

class DownloadMissingSongUseCase(
    private val storageRepository: StorageRepository,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository
) {

    suspend operator fun invoke(song: Song, albumImageUrl: String? = null): EmptyResult<SyncError>{
        var localSongUrl = ""
        storageRepository.downloadToLocalMemory(song.songUrl, "songs").onSuccess { localSongUrl = it }
        if(localSongUrl.isEmpty()) return Result.Error(SyncError.FirebaseStorageError)

        var imageUrl = albumImageUrl
        if(imageUrl == null){
            getImageUrl(albumId = song.albumId, artistId = song.artistId).onSuccess {
                imageUrl = it
            }.onError { return Result.Error(it) }
        }

        songRepository.saveSongLocally(
            song.copy(
                localSongUrl = localSongUrl,
                localImageUrl = imageUrl!!
            )
        )

        return Result.Success(Unit)
    }

    private suspend fun getImageUrl(albumId: String, artistId: String) : Result<String, SyncError>{
        val localAlbum: MutableList<Album> = albumRepository.doesAlbumExist(albumId).toMutableList()
        if(localAlbum.isEmpty()){
            var album = albumRepository.getAlbum(artistId+"/"+albumId).first()
            storageRepository.downloadToLocalMemory(album.image, "album_images").onSuccess {
                album = album.copy(localImageUrl = it)
            }
            albumRepository.saveAlbumLocally(album)
            localAlbum.add(album)
        }

        return Result.Success(localAlbum.first().localImageUrl)
    }

}