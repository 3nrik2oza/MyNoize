package com.project.mynoize.activities.main.repository

import android.net.Uri

import com.google.firebase.storage.FirebaseStorage
import com.project.mynoize.core.domain.DataError
import com.project.mynoize.core.domain.Result
import kotlinx.coroutines.tasks.await

class StorageRepository (

){
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.getReference()

    suspend fun addToStorage(file: Uri, path: String): Result<String, DataError.Remote>{
        return try{
            val riversRef = storageRef.child(path)

            riversRef.putFile(file).await()

            val downloadUrl = riversRef.downloadUrl.await()
            Result.Success(downloadUrl.toString())
        }catch (e: Exception){
            Result.Error(DataError.Remote.SERVER)
        }
    }

    fun removeFromStorage(path: String){
        storageRef.child(path).delete()
    }

}