package com.smproject.cityradio

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat

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

        mediaSession.apply {
            isActive  = true
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//            setMetadata()
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    RadioPlayer.play("https://c34.radioboss.fm:18234/stream")
                }

                override fun onPause() {
                    super.onPause()
                    RadioPlayer.pausePlayer()
                }

                override fun onStop() {
                    super.onStop()
                    RadioPlayer.pausePlayer()
                }
            })
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.action

        if (action.isNullOrBlank()) {
            return START_NOT_STICKY
        }

        when(action) {
            ACTION_PLAY -> mediaControls.play()
            ACTION_PAUSE -> mediaControls.pause()
            ACTION_STOP -> mediaControls.stop()
        }

        return START_STICKY

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}