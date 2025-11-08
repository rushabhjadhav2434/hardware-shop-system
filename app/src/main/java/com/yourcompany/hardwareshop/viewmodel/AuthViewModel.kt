package com.yourcompany.hardwareshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.yourcompany.hardwareshop.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> get() = _registerState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _loginState.postValue(
                if (result.isSuccess) {
                    LoginState.Success(result.getOrNull()!!)
                } else {
                    LoginState.Error(result.exceptionOrNull()?.message ?: "Login failed")
                }
            )
        }
    }

    fun register(email: String, password: String, name: String) {
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            val result = authRepository.register(email, password, name)
            _registerState.postValue(
                if (result.isSuccess) {
                    RegisterState.Success(result.getOrNull()!!)
                } else {
                    RegisterState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
                }
            )
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }
}

// State classes for Login
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: FirebaseUser) : LoginState()
    data class Error(val message: String) : LoginState()
}

// State classes for Register
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: FirebaseUser) : RegisterState()
    data class Error(val message: String) : RegisterState()
}