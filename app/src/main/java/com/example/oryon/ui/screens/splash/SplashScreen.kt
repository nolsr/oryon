package com.example.oryon.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.oryon.data.firebase.AuthRepository
import com.example.oryon.ui.components.Screen

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel
) {
    val openAndPopUp: (String, String) -> Unit = { target, from ->
        navController.navigate(target) {
            popUpTo(from) { inclusive = true }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onAppStart(openAndPopUp)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

