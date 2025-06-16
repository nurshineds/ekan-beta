package com.kel5.ekanbeta.Data

import com.google.firebase.Timestamp

data class UserData(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "user",
    val createdAt: Timestamp? = null,
    val cartItems: Map<String, Long> = emptyMap(),
    val isOnline: Boolean = false,
    val lastSeen: Long? = null,
    val typingTo: String = ""
)
