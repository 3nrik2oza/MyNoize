package com.project.mynoize.data_collecting.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalListeningEvent(
    @PrimaryKey(autoGenerate = false) val id: String,
    val songId: String,
    val artistId: String,
    val userId: String,
    val albumId: String,
    val sessionId: String,
    val sourceType: String,
    val sourceId: String,
    val timestamp: Long,
    val listenedSeconds: Float,
    val duration: Int,
)