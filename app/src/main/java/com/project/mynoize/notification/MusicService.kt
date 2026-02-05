package com.project.mynoize.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.project.mynoize.R
import com.project.mynoize.managers.ExoPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin

class MusicService(
): MediaLibraryService() {

    private lateinit var exoPlayerManager: ExoPlayerManager

    private lateinit var mediaLibrarySession: MediaLibrarySession

    private var bitmap: Bitmap? = null
    private var currentImageUrl: Uri? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())



    override fun onCreate() {
        super.onCreate()

        val userScope = getKoin()
            .getScopeOrNull("USER_SESSION")
            ?: return  // user logged out, service should not run

        exoPlayerManager = userScope.get()

        val callback =object : MediaLibrarySession.Callback {}
        exoPlayerManager.getPlayer()?.let { player ->
            mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback).build()
        }

        exoPlayerManager.getPlayer()?.addListener(object : Player.Listener{
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateNotification(mediaItem)
            }
        })

        val metadata = exoPlayerManager.getPlayer()?.mediaMetadata


        if(currentImageUrl != metadata?.artworkUri){
            loadBitmapAsync(metadata?.artworkUri?.toString()){
                bitmap = it
                showNotification(metadata, bitmap)
            }
        }

        if(this::mediaLibrarySession.isInitialized){
            startForeground(NOTIFICATION_ID, buildNotification(metadata, bitmap))
        }


    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    private fun updateNotification(mediaItem: MediaItem?) {
        val metadata = mediaItem?.mediaMetadata
        val artworkUri = metadata?.artworkUri

        if(currentImageUrl != artworkUri){
            currentImageUrl = artworkUri
            loadBitmapAsync(artworkUri.toString()){
                bitmap = it
                showNotification(metadata, bitmap)
            }
        }

        showNotification(metadata, bitmap)

    }

    @OptIn(UnstableApi::class)
    private fun showNotification(metadata: MediaMetadata?, artwork: Bitmap?) {
        val notification = buildNotification(metadata, artwork)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    @OptIn(UnstableApi::class)
    private fun buildNotification(metadata: MediaMetadata?, artwork: Bitmap?): Notification {
        createNotificationChannel()


        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(metadata?.title ?: "Unknown Title")
            .setContentText(metadata?.artist ?: "Unknown Artist")
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(artwork)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(MediaSessionCompat.Token.fromToken(mediaLibrarySession.platformToken))
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::mediaLibrarySession.isInitialized){
            mediaLibrarySession.release()
        }
        serviceScope.cancel()
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Music Playback",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "music_channel"
        private const val NOTIFICATION_ID = 1
    }

    fun loadBitmapAsync(url: String?, onLoaded: (Bitmap?) -> Unit){
        if(url == null || url.isEmpty()){
            onLoaded(null)
            return
        }

        serviceScope.launch(Dispatchers.IO) {
            val bitmap = loadBitmapFromUrl(applicationContext, url)
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