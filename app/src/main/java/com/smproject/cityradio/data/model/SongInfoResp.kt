package com.smproject.cityradio.data.model

import com.google.gson.annotations.SerializedName

data class SongInfoResp(
    val autodj: Boolean,

    @SerializedName("autodj_title")
    val autoDjTitle: String,

    val currenttrack_artist: String,
    val currenttrack_title: String,
    val listeners: String,
    val live: Boolean,
    val nexttrack: String,
    val nexttrack_artist: String,
    val nexttrack_title: String,
    val nowplaying: String
)