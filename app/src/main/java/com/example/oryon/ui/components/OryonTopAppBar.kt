package com.example.oryon.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.oryon.data.firebase.AuthRepository
import com.example.oryon.ui.theme.FiraSansFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OryonTopAppBar(
    authRepository: AuthRepository,
    navController: NavController,
    text: String = "Oryon"
) {
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    // Top Bar mit logout Funktion wird im Scaffold verwendet
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (navController.currentDestination?.route == "challengeDetail/{challengeId}") {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Zurück",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Text(
                    text,
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = FiraSansFontFamily,
                )
            }
                },
        actions = {
            if(navController.currentDestination?.route == "home") {
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menü")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                scope.launch {
                                    authRepository.logout()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}


