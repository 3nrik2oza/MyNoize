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


/*
*
*     fun addToFirestore(storageUrl: String, storageRef: StorageReference, file: Uri){
        val db = FirebaseFirestore.getInstance()
        val song = Song(
            title = createSongState.songName,
            artistId = artistListState.selectedElement().id,
            artistName = artistListState.selectedElement().name,
            songUrl = storageUrl,
            imageUrl = albumState.selectedElement().image,
            albumId = albumState.selectedElement().id,
            albumName = albumState.selectedElement().name,
            creatorId = FirebaseAuth.getInstance().currentUser!!.uid
        )
        db.collection(Constants.SONG_COLLECTION)
            .add(song)
            .addOnSuccessListener {
                alertDialogState = alertDialogState.copy(
                    show = true,
                    message = Constants.SONG_ADDED_SUCCESSFULLY
                )
            }
            .addOnFailureListener {
                alertDialogState = AlertDialogState(
                    show = true,
                    loading = false,
                    message = "An error has occurred. Please try again."
                )
                storageRef.child("songs/${file.lastPathSegment}").delete()
            }
    }
* */