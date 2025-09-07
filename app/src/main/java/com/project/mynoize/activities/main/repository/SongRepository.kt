package com.project.mynoize.activities.main.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.project.mynoize.core.data.Song
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.Result
import com.project.mynoize.util.Constants
import kotlinx.coroutines.tasks.await

class SongRepository{
    private val db = FirebaseFirestore.getInstance()

    suspend fun addSongToFirebase(
        song: Song
    ): EmptyResult<DataError.Remote>{
        return  try {
            val docRef = db.collection(Constants.SONG_COLLECTION)
                .add(song)
                .await()

            Result.Success(Unit)
        }catch (e: Exception){
            Result.Error(DataError.Remote.SERVER)
        }

    }
}
