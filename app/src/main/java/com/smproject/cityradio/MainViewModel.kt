package com.smproject.cityradio

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _songTitle: MutableLiveData<String> = MutableLiveData()
    val songTitle: LiveData<String> = _songTitle

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(
            "ERR",
            "error -> ${throwable.message} - ${throwable.localizedMessage}; throwable = $throwable"
        )
    }

    private val _viewState: MutableLiveData<MainViewState> =
        MutableLiveData(MainViewState(PlayerStatus.PAUSE, null))

    val viewState: LiveData<MainViewState> = _viewState

    private val playerObserver = Observer<RadioExoPlayer.Status> {
        when (it) {
            RadioExoPlayer.Status.STOP, RadioExoPlayer.Status.PREPARING, RadioExoPlayer.Status.PAUSE, null -> {
                _viewState.value = _viewState.value?.copy(btnStatus = PlayerStatus.PAUSE)
            }

            RadioExoPlayer.Status.PLAYING -> {
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
        when (viewState.value?.btnStatus) {
            PlayerStatus.PLAYING -> {
                MyApplication.application.startService(
                    Intent(
                        MyApplication.application,
                        PlayerService::class.java
                    ).apply {
                        action = PlayerService.ACTION_PAUSE
                        data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                    })
            }

            PlayerStatus.PAUSE -> {
                MyApplication.application.startService(
                    Intent(
                        MyApplication.application,
                        PlayerService::class.java
                    ).apply {
                        action = PlayerService.ACTION_PLAY
                        data = Uri.parse("https://c34.radioboss.fm:18234/stream")
                    })
            }

            null -> {
                //Do nothing
            }
        }
    }




    fun getSong() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val resp = repository.getSong()
            _songTitle.postValue(resp?.autoDjTitle)
        }
    }

    fun startUpdateSongTitle() {
        repository.updateTasksJob?.cancel()
        repository.updateTasksJob =
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                while (isActive) {
                    val resp = repository.getSong()
                    _songTitle.postValue(resp?.autoDjTitle)
                    delay(5000)
                }
            }
    }

}