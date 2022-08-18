package com.smproject.cityradio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _viewState: MutableLiveData<MainViewState> =
        MutableLiveData(MainViewState(PlayerStatus.PAUSE, null))

    val viewState: LiveData<MainViewState> = _viewState

    private val playerObserver = Observer<RadioPlayer.Status> {
        when (it) {
            RadioPlayer.Status.STOP, RadioPlayer.Status.PREPARING, RadioPlayer.Status.PAUSE, null -> {
                _viewState.value = _viewState.value?.copy(btnStatus = PlayerStatus.PLAYING)
            }
            RadioPlayer.Status.PLAYING -> {
                _viewState.value = _viewState.value?.copy(btnStatus = PlayerStatus.PAUSE)
            }
        }
    }

    init {
        RadioPlayer.status.observeForever(playerObserver)
    }

    override fun onCleared() {
        RadioPlayer.status.removeObserver(playerObserver)

    }

    fun clickBtnPlayer() {
        when (viewState.value?.btnStatus) {
            PlayerStatus.PLAYING -> RadioPlayer.pausePlayer()

            PlayerStatus.PAUSE -> {
                RadioPlayer.play("https://c34.radioboss.fm:18234/stream")
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