package com.kel5.ekanbeta.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kel5.ekanbeta.Data.MessageData
import com.kel5.ekanbeta.Data.RoomchatData
import kotlinx.coroutines.tasks.await

class ChatRepo(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val chatRooms = firestore.collection("chatRooms")
    private val users = firestore.collection("users")

    suspend fun sendMessage(toUserId: String, messageText: String) {
        val fromUserId = auth.currentUser?.uid ?: return
        val roomId = generateRoomId(fromUserId, toUserId)
        val roomRef = chatRooms.document(roomId)
        val message = MessageData(senderId = fromUserId, text = messageText)

        firestore.runTransaction { transaction ->
            val roomSnapshot = transaction.get(roomRef)
            if(!roomSnapshot.exists()){
                val newRoom = RoomchatData(participants = listOf(fromUserId, toUserId), lastMessage = messageText)
                transaction.set(roomRef, newRoom)
            } else {
                transaction.update(roomRef,mapOf(
                    "lastMessage" to messageText,
                    "lastTimestamp" to message.timestamp
                ))
            }
            val messageRef = roomRef.collection("messages").document()
            transaction.set(messageRef, message)
        }.await()
    }

    fun getMessageStream(toUserId: String): Query {
        val fromUserId = auth.currentUser?.uid ?: ""
        val roomId = generateRoomId(fromUserId, toUserId)
        return chatRooms.document(roomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
    }

    fun getAllChatRooms() : Query{
        return firestore.collection("chatRooms")
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
    }

    private fun generateRoomId(userId1: String, userId2: String): String{
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    fun updateOnlineStatus(isOnline: Boolean){
        val uid = auth.currentUser?.uid ?: return
        users.document(uid).update("isOnline", isOnline)
    }

    fun updateLastSeen(){
        val uid = auth.currentUser?.uid ?: return
        users.document(uid).update("lastSeen", System.currentTimeMillis())
    }

    fun setTypingStatus(toUserId: String, isTyping: Boolean){
        val uid = auth.currentUser?.uid ?: return
        val field = if(isTyping) "typingTo" else "typingTo"
        users.document(uid).update(field, if(isTyping) toUserId else "")
    }

    fun getPresenceListener(userId: String) = users.document(userId)
}