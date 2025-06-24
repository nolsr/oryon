package com.example.oryon.ui.screens.auth.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.oryon.data.firebase.AuthRepository
import com.example.oryon.data.firebase.FirestoreRepository
import com.example.oryon.data.firebase.UserData
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository, private val firestoreRepository: FirestoreRepository) : ViewModel() {

    var name = mutableStateOf("")
        private set
    var email = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun onNameChange(newName: String) {
        name.value = newName
    }

    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val uid = authRepository.signUp(email.value, password.value)

                val newUser = UserData(
                    id = uid,
                    name = name.value,
                    email = email.value
                )

                firestoreRepository.createUser(newUser)

                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unbekannter Fehler"
            } finally {
                isLoading.value = false
            }
        }
    }
}

class SignUpViewModelFactory(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpViewModel(authRepository, firestoreRepository) as T
    }
}

