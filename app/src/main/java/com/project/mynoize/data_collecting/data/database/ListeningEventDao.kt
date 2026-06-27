package com.project.mynoize.data_collecting.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ListeningEventDao {

    @Upsert
    suspend fun upsertListeningEvent(listeningEvent: LocalListeningEvent)

    @Query("UPDATE LocalListeningEvent SET listenedSeconds = :listenedSeconds WHERE id = :id")
    suspend fun updateListenedSeconds(id: String, listenedSeconds: Float)

    @Delete
    suspend fun deleteEvent(listeningEvent: LocalListeningEvent)

    @Query("DELETE FROM LocalListeningEvent WHERE id IN (:ids)")
    suspend fun deleteEventsByIds(ids: List<String>)

    @Query("SELECT * FROM LocalListeningEvent WHERE timestamp < :timestamp")
    fun getOldListeningEvents(timestamp: Long): List<LocalListeningEvent>
}