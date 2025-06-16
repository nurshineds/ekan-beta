package com.kel5.ekanbeta.Data

data class MessageData(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
