package com.project.mynoize.data_collecting.data.mappers

import com.google.firebase.Timestamp
import com.project.mynoize.data_collecting.data.database.LocalListeningEvent
import com.project.mynoize.data_collecting.data.firebase.entities.ListeningEventDto
import com.project.mynoize.data_collecting.data.model.ListeningEvent
import com.project.mynoize.data_collecting.data.model.SourceType
import java.util.UUID

fun ListeningEvent.toDto(): ListeningEventDto = ListeningEventDto(
    id = id.toString(),
    songId = songId,
    artistId = artistId,
    userId = userId,
    albumId = albumId,
    sessionId = sessionId.toString(),
    sourceType = sourceType.name,
    sourceId = sourceId,
    timestamp = timestamp,
    listenedSeconds = listenedSeconds,
    duration = duration,
)

fun ListeningEventDto.toDomain(): ListeningEvent = ListeningEvent(
    id = UUID.fromString(id),
    songId = songId,
    artistId = artistId,
    userId = userId,
    albumId = albumId,
    sessionId = UUID.fromString(sessionId),
    sourceType = SourceType.valueOf(sourceType),
    sourceId = sourceId,
    timestamp = timestamp,
    listenedSeconds = listenedSeconds,
    duration = duration,
)



fun ListeningEvent.toEntity(): LocalListeningEvent = LocalListeningEvent(
    id = id.toString(),
    songId = songId,
    artistId = artistId,
    userId = userId,
    albumId = albumId,
    sessionId = sessionId.toString(),
    sourceType = sourceType.name,
    sourceId = sourceId,
    timestamp = timestamp.seconds,
    listenedSeconds = listenedSeconds,
    duration = duration,
)

fun LocalListeningEvent.toListeningEvent(): ListeningEvent = ListeningEvent(
    id = UUID.fromString(id),
    songId = songId,
    artistId = artistId,
    userId = userId,
    albumId = albumId,
    sessionId = UUID.fromString(sessionId),
    sourceType = SourceType.valueOf(sourceType),
    sourceId = sourceId,
    timestamp = Timestamp(seconds = timestamp, nanoseconds = 0),
    listenedSeconds = listenedSeconds,
    duration = duration,
)