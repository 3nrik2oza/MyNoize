package com.project.mynoize.activities.main.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.mynoize.activities.main.events.CreateArtistEvent
import com.project.mynoize.data.Artist
import com.project.mynoize.util.Constants

class CreateArtistViewModel: ViewModel() {

    var loading by mutableStateOf(false)

    var showAlertDialog by mutableStateOf(false)
    var messageText by mutableStateOf("")

    var artistName by mutableStateOf("")

    var artistImage by mutableStateOf("")


    fun onEvent(event: CreateArtistEvent){
        when(event){
            is CreateArtistEvent.OnArtistNameChange -> {
                if(!loading){
                    artistName = event.artistName
                }
            }
            is CreateArtistEvent.OnAddArtistClick -> {
                loading = true
                if(!checkInput()){
                    loading = false
                    return
                }

                val storageRef = FirebaseStorage.getInstance().reference

                val file = artistImage.toUri()
                val riversRef = storageRef.child("artist_images/${file.lastPathSegment}")
                val uploadTask = riversRef.putFile(file)

                uploadTask.addOnFailureListener {
                    showAlertDialog = true
                    messageText = "An error has occurred. Please try again. "
                    loading = false
                }

                uploadTask.addOnSuccessListener {
                    addToFirestore(riversRef, storageRef, file)
                }

            }
            is CreateArtistEvent.OnImageChange -> {
                if(!loading) {
                    artistImage = event.artistImage
                }

            }
            is CreateArtistEvent.OnDismissAlertDialog -> {
                showAlertDialog = false
            }
        }
    }

    fun addToFirestore(riversRef: StorageReference, storageRef: StorageReference, file: Uri){
        riversRef.downloadUrl.addOnSuccessListener { uri ->
            val db = FirebaseFirestore.getInstance()
            val artist = Artist(
                name = artistName.trim(),
                image = uri.toString(),
                creator = FirebaseAuth.getInstance().currentUser!!.uid
            )
            db.collection(Constants.ARTIST_COLLECTION)
                .add(artist)
                .addOnSuccessListener {
                    showAlertDialog = true
                    messageText = "Artist added successfully"
                }
                .addOnFailureListener {
                    showAlertDialog = true
                    messageText = "Error adding artist"
                    loading = false
                    storageRef.child("artist_images/${file.lastPathSegment}").delete()
                }
        }
    }

    fun checkInput(): Boolean {
        if(artistName.isEmpty()){
            showAlertDialog = true
            messageText = "Please enter artist name"
            return false
        }
        if(artistImage.isEmpty()){
            showAlertDialog = true
            messageText = "Please select artist image"
            return false
        }
        return true
    }

}