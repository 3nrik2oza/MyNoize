package com.project.mynoize.core.data.repositories

import android.net.Uri

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import kotlinx.coroutines.tasks.await

class StorageRepository (

){
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.getReference()

    suspend fun addToStorage(file: Uri, path: String): Result<String, FbError.Storage>{
        return try{
            val riversRef = storageRef.child(path)

            riversRef.putFile(file).await()

            val downloadUrl = riversRef.downloadUrl.await()
            Result.Success(downloadUrl.toString())
        }catch (e: StorageException){
            when(e.errorCode){
                StorageException.ERROR_OBJECT_NOT_FOUND -> Result.Error(FbError.Storage.OBJECT_NOT_FOUND)
                StorageException.ERROR_BUCKET_NOT_FOUND -> Result.Error(FbError.Storage.BUCKET_NOT_FOUND)
                StorageException.ERROR_QUOTA_EXCEEDED -> Result.Error(FbError.Storage.QUOTA_EXCEEDED)
                StorageException.ERROR_NOT_AUTHENTICATED -> Result.Error(FbError.Storage.NOT_AUTHENTICATED)
                StorageException.ERROR_NOT_AUTHORIZED -> Result.Error(FbError.Storage.NOT_AUTHORIZED)
                else -> Result.Error(FbError.Storage.UNKNOWN)
            }
            Result.Error(FbError.Storage.UNKNOWN)
        }
    }

    fun removeFromStorage(path: String){
        storageRef.child(path).delete()
    }

}