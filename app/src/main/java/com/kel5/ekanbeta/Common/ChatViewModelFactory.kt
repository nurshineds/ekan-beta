package com.kel5.ekanbeta.Common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kel5.ekanbeta.Repository.ChatRepo
import com.kel5.ekanbeta.ViewModel.ChatViewModel

class ChatViewModelFactory(
    private val chatRepo: ChatRepo,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepo, firestore, auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
