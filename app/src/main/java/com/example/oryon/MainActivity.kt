package com.example.oryon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oryon.ui.theme.OryonTheme
import com.example.oryon.ui.screens.HomeScreen
import com.example.oryon.ui.screens.ActivityScreen
import com.example.oryon.ui.screens.ChallengeScreen
import com.example.oryon.ui.components.AppNavigationBar
import com.example.oryon.ui.components.Screen
import com.example.oryon.ui.components.navigationItems


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OryonTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController() // NavController erstellen

    Scaffold(
        bottomBar = {
            AppNavigationBar(navController = navController, items = navigationItems)
        }
    ) { innerPadding ->
        // NavHost zum Anzeigen der Screens
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route, // Start-Screen
            modifier = Modifier.padding(innerPadding) // Wichtig: Padding vom Scaffold anwenden
        ) {
            composable(Screen.Home.route) { HomeScreen(/* Du kannst innerPadding hier übergeben, wenn nötig */) }
            composable(Screen.Activity.route) { ActivityScreen(/* Du kannst innerPadding hier übergeben, wenn nötig */) }
            composable(Screen.Challenge.route) { ChallengeScreen(/* Du kannst innerPadding hier übergeben, wenn nötig */) }

        }
    }
}

// Preview für MainApp (optional, aber hilfreich)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OryonTheme {
        MainApp()
    }
}