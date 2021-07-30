package com.projects.musicplayer.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.projects.musicplayer.R
import com.projects.musicplayer.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.projects.musicplayer.utils.Constants.NOTIFICATION_ID

class MusicNotificationManager(
    context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallbacks: () -> Unit
) {
    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID,
            DescriptionAdaptor(mediaController)
        ).apply {
            setNotificationListener(notificationListener)
            setChannelNameResourceId(R.string.notification_channel_name)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
        }.build().apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.pexels_logo)
        }
    }

    inner class DescriptionAdaptor(private val mediaController: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            TODO("Not yet implemented")
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            TODO("Not yet implemented")
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            TODO("Not yet implemented")
        }
    }
}