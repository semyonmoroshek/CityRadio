package com.smproject.cityradio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _viewState: MutableLiveData<MainViewState> =
        MutableLiveData(MainViewState(PlayerStatus.PAUSE, null))

    val viewState: LiveData<MainViewState> = _viewState

    fun clickBtnPlayer() {
        when (viewState.value?.btnStatus) {
            PlayerStatus.PLAYING -> _viewState.value =
                _viewState.value?.copy(btnStatus = PlayerStatus.PAUSE)

            PlayerStatus.PAUSE -> _viewState.value =
                _viewState.value?.copy(btnStatus = PlayerStatus.PLAYING)
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