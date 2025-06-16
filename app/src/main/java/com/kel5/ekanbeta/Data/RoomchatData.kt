package com.kel5.ekanbeta.Data

data class RoomchatData(
    val participants: List<String> = listOf(),
    val lastMessage: String = "",
    val lastTimestamp: Long = System.currentTimeMillis()
)
