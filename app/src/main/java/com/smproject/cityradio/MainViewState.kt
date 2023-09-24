package com.smproject.cityradio

data class MainViewState(
    val btnStatus: PlayerStatus? = PlayerStatus.PAUSE,
    val songName: String? = null
)

enum class PlayerStatus {
    PLAYING,
    PAUSE
}