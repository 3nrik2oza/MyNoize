package com.project.mynoize.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [LocalSongsEntity::class, LocalAlbumEntity::class, LocalArtistEntity::class, LocalPlaylistEntity::class],
    version = 1
)

@TypeConverters(
    StringListTypeConverter::class
)
abstract class MusicDatabase: RoomDatabase() {

    abstract val songDao: SongDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val playlistDao: PlaylistDao

}