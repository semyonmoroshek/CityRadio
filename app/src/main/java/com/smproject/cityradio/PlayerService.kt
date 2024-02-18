package com.smproject.cityradio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Observer
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PlayerService : Service() {

    @Inject
    lateinit var repository: Repository

    val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(this, "Player")
    }

    val mediaControls: MediaControllerCompat.TransportControls by lazy {
        mediaSession.controller.transportControls
    }

    private var songTitleObserver: Observer<String>? = null

    companion object {
        const val ACTION_PLAY = "com.smproject.cityradio.ACTION_PLAY"
        const val ACTION_PAUSE = "com.smproject.cityradio.ACTION_PAUSE"
        const val ACTION_STOP = "com.smproject.cityradio.ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()

        startForeground(12345, getNotification())

        mediaSession.apply {
            isActive = true
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    RadioExoPlayer.play("https://c34.radioboss.fm:18234/stream")
                }

                override fun onPause() {
                    super.onPause()
                    RadioExoPlayer.pausePlayer()
                }

                override fun onStop() {
                    super.onStop()
                    RadioExoPlayer.pausePlayer()
                }
            })
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.action

        if (action.isNullOrBlank()) {
            return START_NOT_STICKY
        }

        when (action) {
            ACTION_PLAY -> {
                mediaControls.play()
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(12345, getNotification(true))

            }

            ACTION_PAUSE -> {
                mediaControls.pause()
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(12345, getNotification())
            }

            ACTION_STOP -> {
                mediaControls.stop()
                stopSelf()
                return START_NOT_STICKY
            }
        }

        return START_STICKY

    }

    private fun getNotification(isPlaying: Boolean = false): Notification {

        var songTitle = repository.songTitle.value

        val PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID"
        val PRIMARY_CHANNEL_NAME = "PRIMARY"

        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PRIMARY_CHANNEL,
                PRIMARY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
        }

        if (!isPlaying) {
            songTitle = "Paused"
        }

        val notificationLayout = RemoteViews(packageName, R.layout.status_bar)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.status_bar_expanded)

        if (isPlaying) {
            notificationLayout.setViewVisibility(R.id.btnPlay, View.GONE)
            notificationLayout.setViewVisibility(R.id.btnPause, View.VISIBLE)

            notificationLayoutExpanded.setViewVisibility(R.id.btnPlay, View.GONE)
            notificationLayoutExpanded.setViewVisibility(R.id.btnPause, View.VISIBLE)


        } else {
            notificationLayout.setViewVisibility(R.id.btnPlay, View.VISIBLE)
            notificationLayout.setViewVisibility(R.id.btnPause, View.GONE)

            notificationLayoutExpanded.setViewVisibility(R.id.btnPlay, View.VISIBLE)
            notificationLayoutExpanded.setViewVisibility(R.id.btnPause, View.GONE)
        }

        notificationLayout.setTextViewText(R.id.notification_title, songTitle)
        notificationLayoutExpanded.setTextViewText(R.id.notification_title, songTitle)

        setNotificationLayout(notificationLayout)
        setNotificationLayoutExpanded(notificationLayoutExpanded)

        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_transp)
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, true)

        val mediaSessionToken = mediaSession.sessionToken

        val actionIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val actionText = if (isPlaying) "Pause" else "Play"

        val actionIntent = PendingIntent.getService(
            this, 0,
            Intent(MyApplication.application, PlayerService::class.java).apply {
                action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notifucation = NotificationCompat.Builder(this, PRIMARY_CHANNEL)
            .setAutoCancel(false)
            .setContentTitle(songTitle)
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(originalBitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setDeleteIntent(
                PendingIntent.getService(
                    this, 1,
                    Intent(MyApplication.application, PlayerService::class.java).apply {
                        action = ACTION_STOP
                        data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(actionIcon, actionText, actionIntent)
//            .addAction(
//                R.drawable.ic_play, "Play",
//                PendingIntent.getService(
//                    this, 1,
//                    Intent(MyApplication.application, PlayerService::class.java).apply {
//                        action = ACTION_PLAY
//                        data = Uri.parse("https://c34.radioboss.fm:18234/stream")
//                    },
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            )
//            .addAction(
//                R.drawable.ic_pause,
//                "Pause",
//                PendingIntent.getService(
//                    this, 1,
//                    Intent(MyApplication.application, PlayerService::class.java).apply {
//                        action = ACTION_PAUSE
//                    },
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            )

            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionToken)
                    .setShowActionsInCompactView(0)
                    .setShowCancelButton(true)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

//            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayoutExpanded)


        return notifucation.build()

    }

    private fun setNotificationLayoutExpanded(notificationLayout: RemoteViews) {
        notificationLayout.setOnClickPendingIntent(
            R.id.btnPlay, PendingIntent.getService(
                this, 1,
                Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = ACTION_PLAY
                    data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        notificationLayout.setOnClickPendingIntent(
            R.id.btnPause, PendingIntent.getService(
                this, 1,
                Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = ACTION_PAUSE
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        notificationLayout.setOnClickPendingIntent(
            R.id.btnClose, PendingIntent.getService(
                this, 1,
                Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = ACTION_STOP
                    data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun setNotificationLayout(notificationLayout: RemoteViews) {
        notificationLayout.setOnClickPendingIntent(
            R.id.btnPlay, PendingIntent.getService(
                this, 1,
                Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = ACTION_PLAY
                    data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        notificationLayout.setOnClickPendingIntent(
            R.id.btnPause, PendingIntent.getService(
                this, 1,
                Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = ACTION_PAUSE
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        notificationLayout.setOnClickPendingIntent(
            R.id.btnClose, PendingIntent.getService(
                this, 1,
                Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = ACTION_STOP
                    data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun createCustomIcon(bitmap: Bitmap): IconCompat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IconCompat.createWithAdaptiveBitmap(bitmap)
        } else {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false)
            IconCompat.createWithBitmap(resizedBitmap)
        }
    }

    private fun vectorToBitmap(context: Context, drawableId: Int): Bitmap? {
        val vectorDrawable =
            VectorDrawableCompat.create(context.resources, drawableId, null) ?: return null

        val width = vectorDrawable.intrinsicWidth
        val height = vectorDrawable.intrinsicHeight

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return bitmap
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        songTitleObserver?.let { repository.songTitle.removeObserver(it) }
    }
}