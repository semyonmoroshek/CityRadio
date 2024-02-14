package com.smproject.cityradio

import com.smproject.cityradio.data.model.SongInfoResp
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.GET

interface Api {

    @GET("w/nowplayinginfo?u=234&nl=1&_=1707902629476")
    suspend fun getSong(): Response<SongInfoResp>

}