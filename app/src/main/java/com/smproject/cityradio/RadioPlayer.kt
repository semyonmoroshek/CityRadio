package com.smproject.cityradio

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.io.IOException

object RadioPlayer {

    private val _status = MutableLiveData(Status.PAUSE)
    val status: MutableLiveData<Status> = _status

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer().also {

            it.setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
            )
        }
    }


    fun play(url: String) {
        Log.d("TTTT", "fun play")
        mediaPlayer.stop()

        try {
            mediaPlayer.apply {
                setDataSource(url)
                setOnPreparedListener { playPlayer() }
                setOnCompletionListener { _status.value = Status.PLAYING }
                prepareAsync()
                _status.value = Status.PREPARING

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun playPlayer() {
        Log.d("TTTT", "fun playPlayer")
        try {
            mediaPlayer.start()
            _status.value = Status.PLAYING
        } catch (e: java.lang.Exception) {
            Log.d("EXCEPTION", "failed to start media player")
        }
    }

    fun pausePlayer() {
        Log.d("TTTT", "fun pausePlayer")
        try {
            mediaPlayer.pause()
            _status.value = Status.PAUSE
        } catch (e: Exception) {
            Log.d("EXCEPTION", "failed to pause media player")
        }
    }

    enum class Status() {

        STOP, PREPARING, PLAYING, PAUSE
    }
}