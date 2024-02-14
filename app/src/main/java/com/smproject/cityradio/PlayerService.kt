package com.smproject.cityradio

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
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

        startForeground(12345, getNotification())

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

    private fun getNotification(notDissmisible: Boolean = false): Notification {

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
            .setContentTitle("Music title")
            .setSmallIcon(R.drawable.logo_transp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(notDissmisible)
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
            .addAction(
                R.drawable.ic_play, "Play",
                PendingIntent.getService(
                    this, 1,
                    Intent(MyApplication.application, PlayerService::class.java).apply {
                        action = ACTION_PLAY
                        data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                R.drawable.ic_pause,
                "Pause",
                PendingIntent.getService(
                    this, 1,
                    Intent(MyApplication.application, PlayerService::class.java).apply {
                        action = ACTION_PAUSE
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )


        return notifucation.build()
//        startForeground(12345, notifucation.build())

    }

    private fun getPlayerStatus(): String {
        val mediaController = mediaSession.controller
        val playbackState = mediaController.playbackState
        if (playbackState != null) {
            return when (playbackState.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    "Playing"
                }

                PlaybackStateCompat.STATE_PAUSED -> {
                    "Paused"
                }

                else -> {
                    "Paused"
                }
            }
        }
        return "Paused"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}