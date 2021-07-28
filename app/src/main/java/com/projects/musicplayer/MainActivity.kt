package com.projects.musicplayer

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projects.musicplayer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var list: List<Songs>
    lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        playlistAdapter = PlaylistAdapter()
        activityMainBinding.rvMusicList.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_DENIED
        ) {
            list = getSongsFromPhone()
            playlistAdapter.differ.submitList(list)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                231
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 231) {
            if (grantResults.isNotEmpty())
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onRequestPermissionsResult: Permissions granted")
                    list = getSongsFromPhone()
                    Log.e(TAG, "onCreate: $list ")
                    playlistAdapter.differ.submitList(list)
                }
        }
    }

    /*As the name suggests, this function is used to fetch the songs present in your phones and
   returns the arraylist of the same*/
    private fun getSongsFromPhone(): ArrayList<Songs> {
        val arrayList = ArrayList<Songs>()
/*A content resolver is used to access the data present in your phone
* In this case it is used for obtaining the songs present your phone*/
        val contentResolver = contentResolver
/*Here we are accessing the Media class of Audio class which in turn a class of Media
Store, which contains information about all the media files present
* on our mobile device*/
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
/*Here we make the request of songs to the content resolver to get the music files from
our device*/
        val songCursor = contentResolver.query(songUri, null, null, null, null)
/*In the if condition we check whether the number of music files are null or not. The
moveToFirst() function returns the first row of the results*/
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                } else {
                    0
                }
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
/*moveToNext() returns the next row of the results. It returns null if there is no
row after the current row*/
            while (songCursor.moveToNext()) {
                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)
                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)
/*Adding the fetched songs to the arraylist*/
                arrayList.add(
                    Songs(
                        currentId, currentTitle, currentArtist, currentData,
                        currentDate
                    )
                )
            }
        }
/*Returning the arraylist of songs*/
        songCursor?.close()
        return arrayList
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

data class Songs(
    var songID: Long, var songTitle: String, var artist: String, var songData:
    String, var dateAdded: Long
)




