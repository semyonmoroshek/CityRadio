package com.smproject.cityradio

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.EventLogger
import java.io.IOException


object RadioExoPlayer {

    private val _status = MutableLiveData(Status.PAUSE)
    val status: LiveData<Status> = _status

    private val mediaPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(MyApplication.application).build().apply {
            addAnalyticsListener(EventLogger())
        }
    }

    private var mediaItem: MediaItem? = null

    fun play(url: String) {

        try {
//            if (mediaItem == null) {
//                mediaItem = MediaItem.fromUri(url).also {
//                    mediaPlayer.setMediaItem(it)
//                }
//            }

            mediaPlayer.stop()
            mediaPlayer.clearMediaItems()

            // Создайте новый MediaItem и установите его
            val newMediaItem = MediaItem.fromUri(url)
            mediaItem = newMediaItem
            mediaPlayer.setMediaItem(newMediaItem)

            mediaPlayer.prepare()
//            mediaPlayer.play()

                mediaPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (Player.STATE_READY == playbackState) {
                        mediaPlayer.play()
                        _status.value = Status.PLAYING
                    }
                }
            })

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun pausePlayer() {
        try {
            mediaPlayer.pause()
            _status.value = Status.PAUSE
        } catch (e: Exception) {
            Log.d("EXCEPTION", "failed to pause media player")
        }
    }

    enum class Status {
        STOP, PREPARING, PLAYING, PAUSE
    }
}