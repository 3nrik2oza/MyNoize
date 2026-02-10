package com.project.mynoize.core.data.repositories

import android.content.Context
import android.net.Uri

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import kotlinx.coroutines.tasks.await
import java.io.File
import androidx.core.net.toUri
import com.project.mynoize.core.domain.UploadToStorageResult

class StorageRepository(
    private val context: Context,
){
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.getReference()

    suspend fun addToStorage(file: Uri, path: String): Result<UploadToStorageResult, FbError.Storage>{
        return try{
            val extension = getFileExtension(file) ?: return Result.Error(FbError.Storage.UNKNOWN)
            val finalPath = "$path.$extension"

            val riversRef = storageRef.child(finalPath)
            riversRef.putFile(file).await()

            val downloadUrl = riversRef.downloadUrl.await()
            val uploadToStorageResult = UploadToStorageResult(path = finalPath, downloadLink = downloadUrl.toString())
            Result.Success(uploadToStorageResult)
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

    suspend fun downloadToLocalMemory(downloadUrl: String, folder: String): Result<String, FbError.Storage>{
        return try{
            val fileRef = storage.getReferenceFromUrl(downloadUrl)

            val baseDir = context.getExternalFilesDir(null) ?: return Result.Error(FbError.Storage.UNKNOWN)
            val targetDir = File(baseDir, folder)
            if(!targetDir.exists()){
                targetDir.mkdirs()
            }

            val fileName = downloadUrl.toUri().lastPathSegment?.split("/")[1] ?: return Result.Error(FbError.Storage.UNKNOWN)

            val localFile = File(targetDir, fileName)

            fileRef.getFile(localFile).await()

            Result.Success(localFile.absolutePath)
        }catch (_:Exception){
            Result.Error(FbError.Storage.UNKNOWN)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: return null
        return android.webkit.MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
    }

}