package com.kel5.ekanbeta.Common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kel5.ekanbeta.Repository.AuthRepo
import com.kel5.ekanbeta.Repository.ChatRepo
import com.kel5.ekanbeta.ViewModel.ChatViewModel

class ChatViewModelFactory(
    private val chatRepo: ChatRepo,
    private val authRepo: AuthRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepo, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
