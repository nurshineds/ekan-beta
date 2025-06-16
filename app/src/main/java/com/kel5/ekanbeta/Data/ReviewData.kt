package com.kel5.ekanbeta.Data

import com.google.firebase.Timestamp

data class ReviewData(
    val uid: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Timestamp? = null,
    val username: String = ""
)
