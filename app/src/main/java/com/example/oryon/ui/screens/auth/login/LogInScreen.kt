package com.example.oryon.ui.screens.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.oryon.ui.components.Screen
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LogInViewModel
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginSuccessful by viewModel.loginSuccessful.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(loginSuccessful) {
        if (loginSuccessful) {
            navController.navigate("home") {
                popUpTo(0)
            }
        }
    }



    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.Center) {

        Text(text = "LogIn", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = { Text("E-Mail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Passwort") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { coroutineScope.launch { viewModel.login() } },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("LogIn")
            }
        }

        Text(
            text = "Noch kein Konto? Registrieren Sie sich",
            modifier = Modifier.clickable { navController.navigate("signup") }.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}
