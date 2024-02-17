package com.smproject.cityradio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smproject.cityradio.data.model.SongInfoResp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val api: Api
) {

    var updateTasksJob: Job? = null

    private val _songTitle: MutableLiveData<String> = MutableLiveData()
    val songTitle: LiveData<String> = _songTitle

    suspend fun getSong(): SongInfoResp? {
        val resp = api.getSong()
        _songTitle.postValue(resp.body()?.autoDjTitle)
        return resp.body()
    }

}