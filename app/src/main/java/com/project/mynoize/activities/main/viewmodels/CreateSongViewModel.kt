package com.project.mynoize.activities.main.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.mynoize.activities.main.events.CreateSongEvent
import com.project.mynoize.data.Album
import com.project.mynoize.data.Artist
import com.project.mynoize.data.Song
import com.project.mynoize.util.Constants

class CreateSongViewModel(): ViewModel() {

    var songName by mutableStateOf("")
    var songUri by mutableStateOf("")
    var songTitle by mutableStateOf("Select Song")

    var artistIndex by mutableIntStateOf(0)

    var artistList = mutableStateOf(listOf<Artist>())
    var listOfArtists = mutableStateOf(listOf<String>("Select Artist"))
    var artistSelected = mutableStateOf(false)


    var createAlbum by mutableStateOf(false)

    var albumIndex by mutableIntStateOf(0)
    var albumList = mutableStateOf(listOf<Album>())
    var listOfAlbums = mutableStateOf(listOf<String>("Select Album"))
    var albumSelected = mutableStateOf(false)

    var showAlertDialogCreateAlbum = mutableStateOf(false)
    var messageTextCreateAlbum = mutableStateOf("")
    var loadingCreatingAlbum by mutableStateOf(false)

    var showAlertDialog by mutableStateOf(false)
    var loading by mutableStateOf(false)
    var messageText by mutableStateOf("")


    init{
        val db = FirebaseFirestore.getInstance()

        db.collection(Constants.ARTIST_COLLECTION).get()
            .addOnSuccessListener { result ->

                for(document in result){
                    val artist = document.toObject(Artist::class.java)
                    artist.id = document.id
                    artistList.value += artist
                    listOfArtists.value += artist.name
                }
            }
    }


    fun onEvent(event: CreateSongEvent) {
        when(event){
            is CreateSongEvent.OnSongNameChange -> {
                songName = event.songName
            }
            is CreateSongEvent.OnArtistClick -> {
                selectArtist(event.index)
            }
            is CreateSongEvent.OnAlbumClick -> {
                selectAlbum(event.index)
            }
            is CreateSongEvent.OnAddAlbumClick -> {
                createAlbum = true
                return
            }
            is CreateSongEvent.OnCreateAlbumClick->{
                createAlbum(event.image, event.name)

            }
            is CreateSongEvent.OnSelectSongClick -> {
                loadSongTitle(event.context, event.songUri)
            }
            is CreateSongEvent.OnAddSongClick -> {
                loading = true
                if(!checkInput()){
                    loading = false
                    return
                }
                addSongToStorage()
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
            showAlertDialog = true
            messageText = "An error has occurred. Please try again."
            loading = false
        }

    }

    fun addToFirestore(storageUrl: String, storageRef: StorageReference, file: Uri){
        val db = FirebaseFirestore.getInstance()
        val song = Song(
            title = songName,
            artistId = artistList.value[artistIndex].id,
            songUrl = storageUrl,
            imageUrl = albumList.value[albumIndex].image,
            albumId = albumList.value[albumIndex].id,
            creatorId = FirebaseAuth.getInstance().currentUser!!.uid,
            albumName = albumList.value[albumIndex].name,
            artistName = artistList.value[artistIndex].name
        )
        db.collection(Constants.SONG_COLLECTION)
            .add(song)
            .addOnSuccessListener {
                showAlertDialog = true
                messageText = Constants.SONG_ADDED_SUCCESSFULLY
            }
            .addOnFailureListener {
                showAlertDialog = true
                messageText = "An error has occurred. Please try again."
                loading = false
                storageRef.child("songs/${file.lastPathSegment}").delete()
            }
    }

    fun checkInput() : Boolean{
        if(songName.isEmpty()){
            messageText = "Please enter song name"
            showAlertDialog = true
            return false
        }
        if(!artistSelected.value){
            messageText = "Please select artist"
            showAlertDialog = true
            return false
        }
        if(!albumSelected.value){
            messageText = "Please select album"
            showAlertDialog = true
            return false
        }
        if(songUri.isEmpty()){
            messageText = "Please select song file"
            showAlertDialog = true
            return false
        }
        return true
    }

    fun getAlbums(artistId: String){
        val db = FirebaseFirestore.getInstance()
        albumList.value = listOf()
        listOfAlbums.value = listOf("Select Album")
        db.collection(Constants.ARTIST_COLLECTION)
            .document(artistId).collection(Constants.ALBUM_COLLECTION).get()
            .addOnSuccessListener { result ->
                for(document in result){
                    val album = document.toObject(Album::class.java)
                    albumList.value += album
                    listOfAlbums.value += album.name
                }
            }
    }

    fun selectAlbum(index: Int){
        if(!albumSelected.value){
            if(index == 0){
                return
            }
            listOfAlbums.value = listOfAlbums.value.subList(1, listOfAlbums.value.size)
            albumIndex = index-1
            albumSelected.value = true
            return
        }
        albumIndex = index
        albumSelected.value = true
    }

    fun selectArtist(index: Int){
        if(!artistSelected.value){
            if(index == 0){
                return
            }

            listOfArtists.value = listOfArtists.value.subList(1, listOfArtists.value.size)
            artistIndex = index-1
            getAlbums(artistList.value[artistIndex].id)
            artistSelected.value = true
            return
        }
        artistIndex = index
        artistSelected.value = true
        getAlbums(artistList.value[artistIndex].id)
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
        loadingCreatingAlbum = true

        val storageRef = FirebaseStorage.getInstance().reference
        val file = imageUri.toUri()
        val riversRef = storageRef.child("album_images/${file.lastPathSegment}")
        val uploadTask = riversRef.putFile(file)

        uploadTask.addOnFailureListener {
            showAlertDialogCreateAlbum.value = true
            messageTextCreateAlbum.value = "An error has occurred. Please try again."
            loadingCreatingAlbum = false
        }

        uploadTask.addOnSuccessListener {
            riversRef.downloadUrl.addOnSuccessListener { uri ->
                val db = FirebaseFirestore.getInstance()
                val album = Album(
                    name = albumName,
                    image = uri.toString(),
                    creator = FirebaseAuth.getInstance().currentUser!!.uid,
                    artist = artistList.value[artistIndex].id
                )
                    db.collection(Constants.ARTIST_COLLECTION)
                        .document(artistList.value[artistIndex].id)
                        .collection(Constants.ALBUM_COLLECTION)
                        .add(album)
                        .addOnSuccessListener {
                            albumList.value += album
                            listOfAlbums.value += album.name
                            createAlbum = false
                            loadingCreatingAlbum = false
                        }
                        .addOnFailureListener {
                            showAlertDialogCreateAlbum.value = true
                            messageTextCreateAlbum.value = "An error has occurred. Please try again."
                            loadingCreatingAlbum = false
                            storageRef.child("album_images/${file.lastPathSegment}").delete()
                        }
            }

        }

    }


}