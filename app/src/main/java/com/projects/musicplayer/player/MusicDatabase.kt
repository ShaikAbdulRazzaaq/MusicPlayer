package com.projects.musicplayer.player

import com.google.firebase.firestore.FirebaseFirestore
import com.projects.musicplayer.utils.Constants
import com.projects.musicplayer.utils.Song
import kotlinx.coroutines.tasks.await

class MusicDatabase {
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val songCollection = firebaseFirestore.collection(Constants.SONG_COLLECTION)
    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}