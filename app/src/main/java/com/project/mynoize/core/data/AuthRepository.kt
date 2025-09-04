package com.project.mynoize.core.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User

class AuthRepository {
    val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String{
        return auth.currentUser!!.uid
    }

}