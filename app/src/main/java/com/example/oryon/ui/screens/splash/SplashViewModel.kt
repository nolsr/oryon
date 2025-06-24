package com.example.oryon.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.oryon.data.firebase.AuthRepository
import com.example.oryon.ui.components.Screen

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (authRepository.hasUser()) {
            openAndPopUp(Screen.Home.route, "splash")
        } else {
            openAndPopUp("login", "splash")
        }
    }
}


class SplashViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashViewModel(authRepository) as T
    }
}

