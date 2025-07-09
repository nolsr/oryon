package com.example.oryon.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.oryon.data.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow

class LogInViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val loginSuccessful = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    //Login Func von AuthRepository
    //Loading Variable für Loading Animation
    suspend fun login() {
        isLoading.value = true
        errorMessage.value = null
        try {
            authRepository.login(email.value, password.value)
            loginSuccessful.value = true
        } catch (e: Exception) {
            errorMessage.value = e.message
        } finally {
            isLoading.value = false
        }
    }
}


class LogInViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LogInViewModel(authRepository) as T
    }
}