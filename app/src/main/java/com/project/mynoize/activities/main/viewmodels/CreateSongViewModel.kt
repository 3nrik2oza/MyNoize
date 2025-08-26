package com.project.mynoize.activities.main.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.mynoize.activities.main.events.CreateAlbumEvent
import com.project.mynoize.activities.main.events.CreateSongEvent
import com.project.mynoize.activities.main.repository.ArtistRepository
import com.project.mynoize.activities.main.state.AlertDialogState
import com.project.mynoize.activities.main.state.ListSelectionState
import com.project.mynoize.data.Album
import com.project.mynoize.data.Artist
import com.project.mynoize.data.Song
import com.project.mynoize.util.Constants
import kotlinx.coroutines.launch

class CreateSongViewModel(
    artistRepository: ArtistRepository = ArtistRepository()
): ViewModel() {

    var songName by mutableStateOf("")
    var songUri by mutableStateOf("")
    var songTitle by mutableStateOf("Select Song")
    var showCreateAlbum by mutableStateOf(false)

    var artistListState by mutableStateOf(ListSelectionState<Artist>())
        private set


    var albumIndex by mutableIntStateOf(-1)
    var albumList = mutableStateOf(listOf<Album>())

    var albumState by mutableStateOf(ListSelectionState<Album>())

    // implement Stateflow rather then mutableStateOf

    var createAlbumDialogState by mutableStateOf(AlertDialogState())
        private set

    var alertDialogState by mutableStateOf(AlertDialogState())
        private set



    init{
        viewModelScope.launch {
            artistListState = artistListState.copy(list = artistRepository.getArtists())
        }
    }


    fun onEvent(event: CreateSongEvent) {
        when(event){
            is CreateSongEvent.OnSongNameChange -> {
                songName = event.songName
            }
            is CreateSongEvent.OnArtistClick -> {
                artistListState = artistListState.copy(index = event.index)
                getAlbums(artistListState.list[event.index].id)
                albumIndex = -1
            }
            is CreateSongEvent.OnAlbumClick -> {
                albumIndex = event.index
            }
            is CreateSongEvent.OnAddAlbumClick -> {
                showCreateAlbum = true
            }
            is CreateSongEvent.OnCreateAlbumClick->{
                createAlbum(event.image, event.name)
            }
            is CreateSongEvent.OnSelectSongClick -> {
                loadSongTitle(event.context, event.songUri)
            }
            is CreateSongEvent.OnAddSongClick -> {
                alertDialogState = alertDialogState.copy(loading = true)
                if(!checkInput()){
                    alertDialogState = alertDialogState.copy(loading = false)
                    return
                }
                addSongToStorage()
            }
            is CreateSongEvent.OnDismissAlertDialog -> {
                alertDialogState = alertDialogState.copy(show = false)
            }
        }
    }

    fun onCreateAlbumEvent(event: CreateAlbumEvent){
        when(event){
            is CreateAlbumEvent.OnDismissMessageDialog -> {
                createAlbumDialogState = createAlbumDialogState.copy(show = false)
            }
            is CreateAlbumEvent.OnShowAlertDialog -> {
                createAlbumDialogState = createAlbumDialogState.copy(show = true, message = event.message)
            }
            is CreateAlbumEvent.OnDismissCreateAlbumDialog -> {
                showCreateAlbum = false
            }
            is CreateAlbumEvent.OnCreateAlbum -> {
                createAlbum(
                    imageUri = event.imageUri,
                    albumName = event.albumName
                )
            }
        }

    }

    fun addSongToStorage(){
        val storageRef = FirebaseStorage.getInstance().reference
        val file = songUri.toUri()
        val riversRef = storageRef.child("songs/${songName}")
        val uploadTask = riversRef.putFile(file)

        uploadTask
            .addOnSuccessListener {
                riversRef.downloadUrl.addOnSuccessListener { uri ->
                    addToFirestore(storageUrl = uri.toString(), storageRef = storageRef, file = file)
                }
            }.addOnFailureListener {
                alertDialogState = AlertDialogState(
                    show = true,
                    loading = false,
                    message = "An error has occurred. Please try again."
                )
            }
    }

    fun addToFirestore(storageUrl: String, storageRef: StorageReference, file: Uri){
        val db = FirebaseFirestore.getInstance()
        val song = Song(
            title = songName,
            artistId = artistListState.selectedElement().id,
            songUrl = storageUrl,
            imageUrl = albumList.value[albumIndex].image,
            albumId = albumList.value[albumIndex].id,
            creatorId = FirebaseAuth.getInstance().currentUser!!.uid,
            albumName = albumList.value[albumIndex].name,
            artistName = artistListState.selectedElement().name
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

    fun checkInput() : Boolean{
        if(songName.isEmpty()){
            alertDialogState = alertDialogState.copy(message = "Please enter song name", show = true)
            return false
        }
        if(artistListState.index == -1){
            alertDialogState = alertDialogState.copy(message = "Please select artist", show = true)
            return false
        }
        if(albumIndex == -1){
            alertDialogState = alertDialogState.copy(message = "Please select album", show = true)
            return false
        }
        if(songUri.isEmpty()){
            alertDialogState = alertDialogState.copy(message = "Please select song file", show = true)
            return false
        }
        return true
    }

    fun getAlbums(artistId: String){
        try{
            val db = FirebaseFirestore.getInstance()
            albumList.value = listOf()
            db.collection(Constants.ARTIST_COLLECTION)
                .document(artistId).collection(Constants.ALBUM_COLLECTION).get()
                .addOnSuccessListener { result ->
                    for(document in result){
                        val album = document.toObject(Album::class.java)
                        albumList.value += album
                    }
                }
        }catch (e: Exception){
            Log.d("ERROR", e.message.toString())
        }

    }


    fun loadSongTitle(context: Context, uri: String) {
        songUri = uri
        val cursor = context.contentResolver.query(uri.toUri(), null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    songTitle = it.getString(index).removeSuffix(".mp3")
                }
            }
        }
    }

    fun createAlbum(imageUri: String, albumName: String){
        createAlbumDialogState = createAlbumDialogState.copy(loading = true)

        val storageRef = FirebaseStorage.getInstance().reference
        val file = imageUri.toUri()
        val riversRef = storageRef.child("album_images/${file.lastPathSegment}")
        val uploadTask = riversRef.putFile(file)

        uploadTask.addOnFailureListener {
            createAlbumDialogState = AlertDialogState(
                show = true,
                loading = false,
                message = "An error has occurred. Please try again."
            )
        }

        uploadTask.addOnSuccessListener {
            riversRef.downloadUrl.addOnSuccessListener { uri ->
                val db = FirebaseFirestore.getInstance()
                val album = Album(
                    name = albumName,
                    image = uri.toString(),
                    creator = FirebaseAuth.getInstance().currentUser!!.uid,
                    artist = artistListState.selectedElement().id
                )
                    db.collection(Constants.ARTIST_COLLECTION)
                        .document(artistListState.selectedElement().id)
                        .collection(Constants.ALBUM_COLLECTION)
                        .add(album)
                        .addOnSuccessListener {
                            albumList.value += album
                            showCreateAlbum = false
                            createAlbumDialogState = createAlbumDialogState.copy(loading = false)
                        }
                        .addOnFailureListener {
                            createAlbumDialogState = AlertDialogState(
                                show = true,
                                loading = false,
                                message = "An error has occurred. Please try again."
                            )
                            storageRef.child("album_images/${file.lastPathSegment}").delete()
                        }
            }

        }

    }


}