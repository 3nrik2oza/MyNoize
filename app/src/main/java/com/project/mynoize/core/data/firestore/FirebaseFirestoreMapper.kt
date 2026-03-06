package com.project.mynoize.core.data.firestore

import com.google.firebase.firestore.FirebaseFirestoreException
import com.project.mynoize.core.domain.FbError

fun FirebaseFirestoreException.toDomain(): FbError.Firestore{
    return when(code){
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> FbError.Firestore.PERMISSION_DENIED
        FirebaseFirestoreException.Code.UNAVAILABLE -> FbError.Firestore.UNAVAILABLE
        FirebaseFirestoreException.Code.ABORTED -> FbError.Firestore.ABORTED
        FirebaseFirestoreException.Code.NOT_FOUND -> FbError.Firestore.NOT_FOUND
        FirebaseFirestoreException.Code.ALREADY_EXISTS -> FbError.Firestore.ALREADY_EXISTS
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> FbError.Firestore.DEADLINE_EXCEEDED
        FirebaseFirestoreException.Code.CANCELLED -> FbError.Firestore.CANCELLED
        else -> FbError.Firestore.UNKNOWN
    }
}