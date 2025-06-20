package com.kel5.ekanbeta.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long
)
