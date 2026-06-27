package com.project.mynoize.data_collecting.data.model

import com.google.firebase.Timestamp
import java.util.UUID

data class ListeningEvent(
    val id: UUID,
    val songId: String,
    val artistId: String,
    val userId: String,
    val albumId: String,
    val sessionId: UUID,
    val sourceType: SourceType,
    val sourceId: String,
    val timestamp: Timestamp = Timestamp.now(),
    val listenedSeconds: Float,
    val duration: Int,
)
