package com.smproject.cityradio

import com.smproject.cityradio.data.model.SongInfoResp
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val api: Api
) {

    var updateTasksJob: Job? = null


    suspend fun getSong(): SongInfoResp? {
        val resp = api.getSong()
        return resp.body()
    }

}