package com.kel5.ekanbeta.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDAO {
    @Query
        ("SELECT * FROM messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    suspend fun getMessagesByRoomId(roomId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE roomId = :roomId")
    suspend fun deleteMessagesForRoom(roomId: String)
}