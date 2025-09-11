package com.project.mynoize.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.project.mynoize.R
import com.project.mynoize.core.data.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicNotificationManager(
    private val context: Context,
    private val serviceScope: CoroutineScope
) {

    private val notificationId = 1
    private val channelId = "running_channel"

    init{
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Music Service",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun buildNotification(
        isPlaying: Boolean,
        bitmap: Bitmap?,
        currentSong: Song?,
        onActionClick: (MusicService.Actions) -> PendingIntent
    ): Notification {


        val layout = createLayout(bitmap, isPlaying, currentSong, onActionClick)

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.color.transparent)
            .setCustomContentView(layout)
            .setOngoing(true)
            .build()
    }

    fun createLayout(bitmap: Bitmap?, isPlaying: Boolean, currentSong: Song?, onActionClick: (MusicService.Actions) -> PendingIntent): RemoteViews{
        val layout = RemoteViews(context.packageName, R.layout.custom_notification_layout)

        bitmap?.let{
            layout.setImageViewBitmap(R.id.notification_album_art, bitmap)
        } ?: layout.setImageViewResource(R.id.notification_album_art, R.drawable.ic_music_note)

        // Play/Pause button
        layout.setImageViewResource(
            R.id.notification_start_stop, if(isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )

        //Song Title and Artist
        currentSong?.let{ song ->
            layout.setTextViewText(R.id.notification_title, song.title)
            layout.setTextViewText(R.id.notification_body, song.artistName)
        }
        layout.setOnClickPendingIntent(R.id.notification_prev, onActionClick(MusicService.Actions.PREV))
        layout.setOnClickPendingIntent(R.id.notification_next, onActionClick(MusicService.Actions.NEXT))
        layout.setOnClickPendingIntent(R.id.notification_start_stop, onActionClick(MusicService.Actions.PLAY))

        return layout
    }

    fun notifyNotification(notification: Notification) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }

    fun loadBitmapAsync(url: String?, onLoaded: (Bitmap?) -> Unit){
        if(url == null || url.isEmpty()){
            onLoaded(null)
            return
        }

        serviceScope.launch(Dispatchers.IO) {
            val bitmap = loadBitmapFromUrl(context, url)
            launch(Dispatchers.Main) {
                onLoaded(bitmap)
            }
        }
    }

    suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? {
        val loader = ImageLoader(context = context)

        val request = ImageRequest.Builder(context = context)
            .data(url)
            .size(512, 512)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as? SuccessResult)
        return (result?.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
    }


}

