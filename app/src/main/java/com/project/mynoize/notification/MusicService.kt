package com.project.mynoize.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import com.project.mynoize.core.data.Song
import com.project.mynoize.managers.ExoPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MusicService(
): Service() {

    private val exoPlayerManager: ExoPlayerManager by inject()

    private lateinit var notificationManager: MusicNotificationManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var currentBitmap: Bitmap? = null
    private var currentImageUrl: String? = null


    override fun onCreate() {
        super.onCreate()

        notificationManager = MusicNotificationManager(this, serviceScope)

        serviceScope.launch {
            exoPlayerManager.isPlaying.collect { isPlaying ->

                    updateNotification(isPlaying, exoPlayerManager.currentSong.value)
                }
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        action?.let {
            performAction(Actions.valueOf(it))
        } ?: performAction(Actions.START)

        return START_STICKY
    }

    private fun performAction(action: Actions) {
        when (action) {
            Actions.START -> {}
            Actions.STOP -> stopSelf()
            Actions.PLAY -> exoPlayerManager.playPauseToggle()
            Actions.NEXT -> exoPlayerManager.nextSong()
            Actions.PREV -> exoPlayerManager.prevSong()
        }
    }


    private fun updateNotification(isPlaying: Boolean, song: Song?) {
        val imageUrl = song?.imageUrl

        if (imageUrl != currentImageUrl) {
            currentImageUrl = imageUrl
            notificationManager.loadBitmapAsync(imageUrl) { bitmap ->
                currentBitmap = bitmap
                val notification = notificationManager.buildNotification(
                    isPlaying,
                    bitmap,
                    song
                ) { action -> createActionIntent(action) }
                notificationManager.notifyNotification(notification)
            }
        } else {
            // Use cached bitmap immediately
            val notification = notificationManager.buildNotification(
                isPlaying,
                currentBitmap,
                song
            ) { action -> createActionIntent(action) }
            notificationManager.notifyNotification(notification)
        }
    }

    private fun createActionIntent(action: Actions): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply { this.action = action.name }
        return PendingIntent.getService(
            this,
            action.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    enum class Actions {
        START,
        STOP,
        PLAY,
        NEXT,
        PREV
    }
}