package com.smproject.cityradio

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class PlayerService : Service() {

    val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(this, "Player")
    }

    val mediaControls: MediaControllerCompat.TransportControls by lazy {
        mediaSession.controller.transportControls
    }

    companion object {
        const val ACTION_PLAY = "com.smproject.cityradio.ACTION_PLAY"
        const val ACTION_PAUSE = "com.smproject.cityradio.ACTION_PAUSE"
        const val ACTION_STOP = "com.smproject.cityradio.ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()

        showNotification()

        mediaSession.apply {
            isActive = true
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//            setMetadata()
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
            ACTION_PLAY -> mediaControls.play()
            ACTION_PAUSE -> mediaControls.pause()
            ACTION_STOP -> mediaControls.stop()
        }

        return START_STICKY

    }

    private fun showNotification() {

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


        val notifucation = NotificationCompat.Builder(this, PRIMARY_CHANNEL)
            .setAutoCancel(false)
            .setContentTitle("")
            .setSmallIcon(R.drawable.ic_music_note)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                R.drawable.ic_music_note, "Play",
                PendingIntent.getService(
                    this, 1,
                    Intent(MyApplication.application, PlayerService::class.java).apply {
                        setAction(PlayerService.ACTION_PLAY)
                        setData(Uri.parse("https://c34.radioboss.fm:18234/stream"))
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                R.drawable.ic_music_note, "Pause",
                PendingIntent.getService(
                    this, 1,
                    Intent(MyApplication.application, PlayerService::class.java).apply {
                        setAction(PlayerService.ACTION_PAUSE)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        startForeground(12345, notifucation)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}