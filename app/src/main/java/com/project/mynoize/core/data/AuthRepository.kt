package com.project.mynoize.core.data

import android.util.Log
import androidx.credentials.provider.AuthenticationError
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import com.project.mynoize.core.domain.EmptyResult
import com.project.mynoize.core.domain.FbError
import com.project.mynoize.core.domain.Result
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class AuthRepository {
    val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String{
        return auth.currentUser!!.uid
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): EmptyResult<FbError.Auth>{
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        }catch (e: FirebaseAuthException){
            when(e.errorCode){
                "ERROR_INVALID_EMAIL" -> Result.Error(FbError.Auth.INVALID_EMAIL)
                "ERROR_WRONG_PASSWORD" -> Result.Error(FbError.Auth.INVALID_PASSWORD)
                "ERROR_INVALID_CREDENTIAL" -> Result.Error(FbError.Auth.USER_NOT_FOUND)
                "ERROR_USER_DISABLED" -> Result.Error(FbError.Auth.USER_DISABLED)
                "ERROR_TOO_MANY_REQUESTS" -> Result.Error(FbError.Auth.ERROR_TOO_MANY_REQUESTS)
                "ERROR_NETWORK_REQUEST_FAILED" -> Result.Error(FbError.Auth.ERROR_NETWORK_REQUEST_FAILED)
                else -> Result.Error(FbError.Auth.UNKNOWN)
            }
        }catch (e: Exception){
            if(e.message == "A network error (such as timeout, interrupted connection or unreachable host) has occurred."){
                Result.Error(FbError.Auth.ERROR_NETWORK_REQUEST_FAILED)
            }else{
                Result.Error(FbError.Auth.UNKNOWN)
            }


        }

    }

}