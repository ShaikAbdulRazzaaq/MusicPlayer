package com.projects.musicplayer.player

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(private val musicDatabase: MusicDatabase) {

    private var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = State.STATE_INITIALIZING
        val allSongs = musicDatabase.getAllSongs()
        songs = allSongs.map { song ->
            Builder().apply {
                putString(METADATA_KEY_ARTIST, song.subtitle)
                putString(METADATA_KEY_DISPLAY_TITLE, song.title)
                putString(METADATA_KEY_MEDIA_ID, song.mediaId)
                putString(METADATA_KEY_TITLE, song.title)
                putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
                putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                putString(METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
                putString(METADATA_KEY_DISPLAY_SUBTITLE, song.subtitle)
                putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.subtitle)
            }.build()

        }
        state = State.STATE_INITIALIZING
    }

    fun asMediaSource(defaultDataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach {
            val mediaSource =
                ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(
                    MediaItem.fromUri(
                        it.getString(
                            METADATA_KEY_MEDIA_URI
                        )
                    )
                )
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map {
        val desc = MediaDescriptionCompat.Builder().apply {
            setMediaUri(it.getString(METADATA_KEY_MEDIA_URI).toUri())
            setTitle(it.description.title)
            setSubtitle(it.description.subtitle)
            setMediaId(it.description.mediaId)
            setIconUri(it.description.iconUri)
        }.build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()
    private var state = State.STATE_CREATED
        set(value) {
            if (value == State.STATE_INITIALIZED || value == State.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it(state == State.STATE_INITIALIZED)
                    }
                }
            } else field = value
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == State.STATE_INITIALIZING || state == State.STATE_CREATED) {
            onReadyListeners += action
            false
        } else {
            action(state == State.STATE_INITIALIZED)
            true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}