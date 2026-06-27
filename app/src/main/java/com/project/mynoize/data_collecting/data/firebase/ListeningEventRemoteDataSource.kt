package com.project.mynoize.data_collecting.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.project.mynoize.core.data.firestore.safeFirestoreCall
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import com.project.mynoize.data_collecting.data.firebase.entities.ListeningEventDto
import com.project.mynoize.util.Constants

class ListeningEventRemoteDataSource(
    private val firestore: FirebaseFirestore
) {

    suspend fun uploadListeningEvents(events: List<ListeningEventDto>): EmptyResult<FbError.Firestore>{
        safeFirestoreCall {
            events.chunked(300).forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { event ->
                    val docRef = firestore.collection(Constants.LISTENING_EVENT_COLLECTION).document()
                    batch.set(docRef, event.copy(id = docRef.id))
                }
                batch.commit()
            }
        }
        return Result.Success(Unit)
    }
}