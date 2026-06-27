package com.project.mynoize.data_collecting.data.firebase.entities

import com.google.firebase.Timestamp

data class ListeningEventDto(
    val id: String,
    val songId: String,
    val artistId: String,
    val userId: String,
    val albumId: String,
    val sessionId: String,
    val sourceType: String,
    val sourceId: String,
    val timestamp: Timestamp = Timestamp.now(),
    val listenedSeconds: Float,
    val duration: Int,
)
