package com.kel5.ekanbeta.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kel5.ekanbeta.Data.UserData
import com.kel5.ekanbeta.Repository.AuthRepo
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val authRepo: AuthRepo = AuthRepo()
): ViewModel() {
    private val _user = mutableStateOf<UserData?>(null)
    val user: State<UserData?> = _user

    private val _selectedUser = MutableStateFlow<UserData?>(null)
    val selectedUser : StateFlow<UserData?> = _selectedUser

    private var listenerRegistration : ListenerRegistration? = null

    init{
        startUserListener()
    }

    private fun startUserListener(){
        listenerRegistration = authRepo.getUserListener { newUser ->
            _user.value = newUser
        }
    }

    fun loadUserById(userId: String){
        viewModelScope.launch {
            val user = authRepo.getUserById(userId)
            _selectedUser.value = user
        }
    }

    fun getUserById(userId: String): StateFlow<UserData?>{
        loadUserById(userId)
        return selectedUser
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}