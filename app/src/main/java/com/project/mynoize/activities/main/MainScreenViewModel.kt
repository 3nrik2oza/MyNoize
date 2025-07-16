package com.project.mynoize.activities.main

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.mynoize.data.Song
import com.project.mynoize.managers.ExoPlayerManager

class MainScreenViewModel (context: Context) : ViewModel(){

    var playerManager: ExoPlayerManager = ExoPlayerManager(context)

    var songList = mutableStateOf(listOf<Song>())


    init{
        Firebase.firestore.collection("songs").get()
            .addOnSuccessListener { result ->
                val updatedList = songList.value.toMutableList()
                for(document in result){
                    val song = document.toObject(Song::class.java)
                    updatedList.add(song)
                }
                songList.value = updatedList
        }
    }



}