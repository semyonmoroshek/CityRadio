package com.smproject.cityradio

import com.google.gson.annotations.SerializedName

data class Song(

    val title: String?,

    @SerializedName("stream")
    private val musicStreamUrl: String
)
