package com.kel5.ekanbeta.ViewModel

import com.kel5.ekanbeta.Repository.AuthRepo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel() : ViewModel() {
    private val authRepo = AuthRepo()

    val username = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    val usernameError = MutableStateFlow<String?>(null)
    val emailError = MutableStateFlow<String?>(null)
    val passwordError = MutableStateFlow<String?>(null)
    val confirmPasswordError = MutableStateFlow<String?>(null)

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> get() = _userRole

    private val _registerSuccess = MutableStateFlow<Boolean?>(null)
    val registerSuccess : StateFlow<Boolean?> get() = _registerSuccess

    private val _loginSuccess = MutableStateFlow<Boolean?>(null)
    val loginSuccess : StateFlow<Boolean?> get() = _loginSuccess

    private val _generalError = MutableStateFlow<String?>(null)
    val generalError :StateFlow<String?> get() = _generalError

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading

    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername : StateFlow<String?> get() = _currentUsername

    private val _currentEmail = MutableStateFlow<String?>(null)
    val currentEmail : StateFlow<String?> get() = _currentEmail

    init {
        checkIfUserLoggedIn()
    }

    fun onUsernameChanged(value : String){
        username.value = value
        if(value.isNotBlank()) usernameError.value = null
    }

    fun onEmailChanged(value : String){
        email.value = value
        if(value.isNotBlank()) emailError.value = null
    }

    fun onPasswordChanged(value : String){
        password.value = value
        if(value.isNotBlank()) passwordError.value = null
    }

    fun onConfirmPasswordChanged(value : String){
        confirmPassword.value = value
        if(value == password.value) confirmPasswordError.value = null
    }

    fun register(){
        val valid = validateFields()
        if(!valid) return

        _isLoading.value = true

        viewModelScope.launch {
            val(success, errorMessage) = authRepo.registerUser(
                username.value.trim(),
                email.value.trim(),
                password.value
            )
            if(success){
                _registerSuccess.value = true
            } else {
                _registerSuccess.value = false
                when {
                    errorMessage?.contains("Username sudah digunakan", true) == true -> {
                        usernameError.value = errorMessage
                    }
                    errorMessage?.contains("Email sudah digunakan", true) == true -> {
                        emailError.value = errorMessage
                    }
                    else -> {
                        _generalError.value = errorMessage
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun login() {
        var isValid = true
        if (email.value.isBlank()) {
            emailError.value = "Email tidak boleh kosong"
            isValid = false
        } else {
            emailError.value = null
        }

        if (password.value.isBlank()) {
            passwordError.value = "Password tidak boleh kosong"
            isValid = false
        } else {
            passwordError.value = null
        }

        if (!isValid) return

        _isLoading.value = true
        _generalError.value = null

        viewModelScope.launch {
            val (success, errorMessage) = authRepo.loginUser(
                email.value.trim(),
                password.value
            )

            if (success) {
                val role = authRepo.getUserRole()
                if(role != null){
                    _userRole.value = role
                    _loginSuccess.value = true
                } else {
                    _loginSuccess.value = false
                    _generalError.value = "Gagal mengambil data pengguna"
                }
            } else {
                _loginSuccess.value = false
                when {
                    errorMessage?.contains("Email tidak terdaftar", true) == true -> {
                        emailError.value = errorMessage
                    }
                    errorMessage?.contains("Password salah", true) == true -> {
                        passwordError.value = errorMessage
                    }
                    else -> {
                        _generalError.value = errorMessage
                    }
                }
            }

            _isLoading.value = false
        }
    }

    private fun validateFields(
        checkUsername: Boolean = true,
        checkConfirmPassword: Boolean = true): Boolean{
        var isValid = true
        if(checkUsername && username.value.isBlank()){
            usernameError.value = "Username tidak boleh kosong"
            isValid = false
        }

        if(email.value.isBlank()){
            emailError.value = "Email tidak boleh kosong"
            isValid = false
        }

        if(password.value.isBlank()){
            passwordError.value = "Password tidak boleh kosong"
            isValid = false
        }

        if(checkConfirmPassword && confirmPassword.value != password.value){
            confirmPasswordError.value = "Password tidak sesuai"
            isValid = false
        }

        return isValid
    }

    fun clearErrors(){
        usernameError.value = null
        emailError.value = null
        passwordError.value = null
        confirmPasswordError.value = null
        _generalError.value = null
    }

    fun logout(){
        viewModelScope.launch {
            authRepo.logout()
            _loginSuccess.value = false
            _userRole.value = null
            _currentUsername.value = null
            _currentEmail.value = null
        }
    }

    private fun checkIfUserLoggedIn(){
        val currentUser = authRepo._getCurrentUser()
        if(currentUser != null){
            viewModelScope.launch {
                _loginSuccess.value = true
            }
        } else {
            _loginSuccess.value = false
        }
    }

    fun fetchCurrentUserData(){
        viewModelScope.launch {
            val data = authRepo.getCurrentUserData()
            _currentUsername.value = data?.get("username") as? String
            _currentEmail.value = data?.get("email") as? String
        }
    }
}