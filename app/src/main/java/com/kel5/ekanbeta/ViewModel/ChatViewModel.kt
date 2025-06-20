package com.kel5.ekanbeta.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel5.ekanbeta.Data.MessageData
import com.kel5.ekanbeta.Data.RoomchatData
import com.kel5.ekanbeta.Repository.AuthRepo
import com.kel5.ekanbeta.Repository.ChatRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ListenerRegistration

class ChatViewModel(
    private val chatRepo: ChatRepo,
    private val authRepo: AuthRepo
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageData>>(emptyList())
    val messages: StateFlow<List<MessageData>> get() = _messages

    private val _chatRooms = MutableStateFlow<List<RoomchatData>>(emptyList())
    val chatRooms: StateFlow<List<RoomchatData>> = _chatRooms

    private val _isOnline = MutableStateFlow<Boolean?>(null)
    val isOnline: StateFlow<Boolean?> = _isOnline

    private val _lastSeen = MutableStateFlow<Long?>(null)
    val lastSeen: StateFlow<Long?> = _lastSeen

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    private var chatListListener: ListenerRegistration? = null
    private var messageListener: ListenerRegistration? = null
    private var presenceListener: ListenerRegistration? = null

    val currentUserId: String
        get() = authRepo.generateUserId().orEmpty()

    fun loadAllChatRooms() {
        chatListListener?.remove()
        chatListListener = chatRepo.getAllChatRooms()
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val rooms = snapshot.documents.mapNotNull { it.toObject(RoomchatData::class.java) }
                _chatRooms.value = rooms
            }
    }

    fun startListening(toUserId: String) {
        val roomId = chatRepo.generateRoomId(currentUserId, toUserId)
        messageListener?.remove()

        messageListener = chatRepo.getMessageStream(toUserId)
            .addSnapshotListener { snapshot, error ->
                viewModelScope.launch {
                    if (error != null || snapshot == null) {
                        val local = chatRepo.getMessagesLocal(roomId)
                        _messages.value = local
                        return@launch
                    }

                    val msg = snapshot.documents.mapNotNull { it.toObject(MessageData::class.java) }
                    _messages.value = msg
                    chatRepo.saveMessagesLocal(roomId, msg)
                }
            }

        presenceListener?.remove()
        presenceListener = chatRepo.getPresenceListener(toUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                _isOnline.value = snapshot.getBoolean("isOnline")
                _lastSeen.value = snapshot.getLong("lastSeen")
                _isTyping.value = snapshot.getString("typingTo") == currentUserId
            }

        chatRepo.updateOnlineStatus(true)
    }

    fun stopListening() {
        messageListener?.remove()
        presenceListener?.remove()
        messageListener = null
        presenceListener = null
        chatRepo.setTypingStatus("", false)
        chatRepo.updateOnlineStatus(false)
        chatRepo.updateLastSeen()
    }

    fun sendMessage(toUserId: String, text: String) {
        viewModelScope.launch {
            chatRepo.sendMessage(toUserId, text)
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatListListener?.remove()
        messageListener?.remove()
        presenceListener?.remove()
    }

    fun getUserData(userId: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = authRepo.getUserById(userId)
                onResult(user?.username)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun getAdminUID(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val admin = authRepo.getUserByIdWithRole("admin")
                onResult(admin?.uid)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun setTyping(toUserId: String, isTyping: Boolean) {
        chatRepo.setTypingStatus(toUserId, isTyping)
    }
}
