package com.smproject.cityradio

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _viewState: MutableLiveData<MainViewState> =
        MutableLiveData(MainViewState(PlayerStatus.PAUSE, null))

    val viewState: LiveData<MainViewState> = _viewState

    private val playerObserver = Observer<RadioExoPlayer.Status> {
        Log.d("TTTT", "RadioExoPlayer status: $it" )
        when (it) {
            RadioExoPlayer.Status.STOP, RadioExoPlayer.Status.PREPARING, RadioExoPlayer.Status.PAUSE, null -> {
                Log.d("TTTT", "Status: btnStatus = PlayerStatus.PAUSE" )
                _viewState.value = _viewState.value?.copy(btnStatus = PlayerStatus.PAUSE)
            }
            RadioExoPlayer.Status.PLAYING -> {
                Log.d("TTTT", "Status: btnStatus = PlayerStatus.PLAYING" )
                _viewState.value = _viewState.value?.copy(btnStatus = PlayerStatus.PLAYING)
            }
        }
    }

    init {
        RadioExoPlayer.status.observeForever(playerObserver)
    }

    override fun onCleared() {
        RadioExoPlayer.status.removeObserver(playerObserver)

    }

    fun clickBtnPlayer() {
        Log.d("TTTT", "PlayerStatus: ${viewState.value?.btnStatus}")
        when (viewState.value?.btnStatus) {
            PlayerStatus.PLAYING -> {
                Log.d("TTTT", "PlayerStatus: ${viewState.value?.btnStatus} - PlayerStatus.PLAYING")
                MyApplication.application.startService(Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = PlayerService.ACTION_PAUSE
                    data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                })
            }

            PlayerStatus.PAUSE -> {
                Log.d("TTTT", "PlayerStatus: ${viewState.value?.btnStatus} - PlayerStatus.PAUSE")
                MyApplication.application.startService(Intent(MyApplication.application, PlayerService::class.java).apply {
                    action = PlayerService.ACTION_PLAY
                    data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                })
            }
            null -> {
                //Do nothing
            }
        }
    }


    private val _songTitle: MutableLiveData<Song> = MutableLiveData()

    val songTitle: LiveData<Song> = _songTitle

    fun setSongTitle() {

    }

}