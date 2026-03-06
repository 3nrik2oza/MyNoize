package com.project.mynoize.core.data.firestore

import com.google.firebase.firestore.FirebaseFirestoreException
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import kotlinx.coroutines.CancellationException

suspend fun <T> safeFirestoreCall(
    block: suspend () -> T
): Result<T, FbError.Firestore>{
    return try{
        Result.Success(block())
    }catch (e: FirebaseFirestoreException){
        Result.Error(e.toDomain())
    }catch (e: Exception){
        if (e is CancellationException) throw e
        Result.Error(FbError.Firestore.UNKNOWN)
    }
}