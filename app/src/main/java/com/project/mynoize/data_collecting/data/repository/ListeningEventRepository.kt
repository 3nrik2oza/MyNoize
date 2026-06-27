package com.project.mynoize.data_collecting.data.repository

import com.google.firebase.Timestamp
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.Result
import com.project.mynoize.core.domain.onSuccess
import com.project.mynoize.data_collecting.data.database.ListeningEventDao
import com.project.mynoize.data_collecting.data.firebase.ListeningEventRemoteDataSource
import com.project.mynoize.data_collecting.data.mappers.toDto
import com.project.mynoize.data_collecting.data.mappers.toEntity
import com.project.mynoize.data_collecting.data.mappers.toListeningEvent
import com.project.mynoize.data_collecting.data.model.ListeningEvent

class ListeningEventRepository(
    private val eventDao: ListeningEventDao,
    private val remoteSource: ListeningEventRemoteDataSource,
    private val authRepository: AuthRepository,
) {

    suspend fun uploadLocalListeningEvents(){
        val localEvents = eventDao.getOldListeningEvents(Timestamp.now().seconds).map { it.toListeningEvent() }

        eventDao.deleteEventsByIds(localEvents.filter { it.listenedSeconds < 3 }.map { it.toDto() }.map { it.id })

        remoteSource.uploadListeningEvents(localEvents.filter { it.listenedSeconds > 3 }.map { it.toDto() })
            .onSuccess {
                eventDao.deleteEventsByIds(ids = localEvents.map { it.id.toString() })
            }
    }

    suspend fun updateListeningTime(listeningEvent: ListeningEvent){
        eventDao.updateListenedSeconds(
            id = listeningEvent.id.toString(),
            listenedSeconds = listeningEvent.listenedSeconds.coerceIn(
                minimumValue = null,
                maximumValue = listeningEvent.duration.toFloat()
            )
        )
    }

    suspend fun saveToLocalDatabase(listeningEvent: ListeningEvent): EmptyResult<DataError.Local>{
        return try {
            eventDao.upsertListeningEvent(listeningEvent.copy(userId = authRepository.getCurrentUserId()).toEntity())
            Result.Success(Unit)
        }catch (_: Exception){
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}