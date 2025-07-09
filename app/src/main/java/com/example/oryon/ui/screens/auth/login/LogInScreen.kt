package com.example.oryon.ui.screens.auth.login

import android.graphics.Paint.Align
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.oryon.ui.components.Screen
import androidx.lifecycle.ViewModel
import com.example.oryon.R
import com.mapbox.maps.extension.style.style
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(
                horizontalBias = -0.7f,
                verticalBias = 0f,
            ),

            modifier = Modifier.fillMaxSize().graphicsLayer { scaleX=-1f }
        )

        //Logo
        Text(
            text = "ORYON",
            style = MaterialTheme.typography.headlineLarge,
            fontStyle = FontStyle.Italic,
            color = darkColorScheme().onBackground,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        //Title Slogen
        Box(modifier = Modifier
            .align(Alignment.TopStart)
            .padding( 16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy((-8).dp)
            ) {
                Text(
                    text = "START",
                    style = MaterialTheme.typography.headlineLarge,
                    color = darkColorScheme().onBackground,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                )
                Text(
                    text = "YOUR",
                    style = MaterialTheme.typography.headlineLarge,
                    color = darkColorScheme().onBackground,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                )
                Text(
                    text = "RUN",
                    style = MaterialTheme.typography.headlineLarge,
                    color = darkColorScheme().onBackground,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                )
            }
        }

        //Login Felder
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
                .height(265.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center) {

                Text(text = "LOGIN", style = MaterialTheme.typography.headlineLarge, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Black, color = darkColorScheme().onBackground)

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.email.value = it },
                    label = { Text("E-Mail") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(unfocusedTextColor = darkColorScheme().onBackground, unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.password.value = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(unfocusedTextColor = darkColorScheme().onBackground, unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier
                    .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween){

                    Button(
                        onClick = { coroutineScope.launch { viewModel.login() } },
                        modifier = Modifier.weight(1f).padding(end=16.dp).height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Login")
                        }
                    }

                    OutlinedButton(
                        onClick = { coroutineScope.launch { navController.navigate("signup") } },
                        modifier = Modifier.weight(1f).padding(start=16.dp).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = darkColorScheme().onBackground),
                        border = BorderStroke(2.dp, darkColorScheme().onBackground),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Sign Up")
                        }
                    }
                }

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = Color.Red)
                }
        }
    }
    }
}
