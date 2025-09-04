package com.project.mynoize.activities.main.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.project.mynoize.core.data.Artist
import com.project.mynoize.util.Constants
import kotlinx.coroutines.tasks.await

class ArtistRepository(

) {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getArtists(): List<Artist> {
        val snapshot = db.collection(Constants.ARTIST_COLLECTION)
            .get()
            .await()

        return snapshot.documents.map{ document ->
            document.toObject(Artist::class.java)!!.apply {
                id = document.id
            }
        }
    }
}