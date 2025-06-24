package com.example.oryon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.oryon.data.firebase.AuthRepositoryImpl
import com.example.oryon.data.firebase.FirestoreRepository
import com.example.oryon.data.firebase.FirestoreRepositoryImpl
import com.example.oryon.data.location.LocationRepositoryImpl
import com.example.oryon.data.location.LocationTrackingService
import com.example.oryon.domain.location.TrackRunUseCase
import com.example.oryon.ui.theme.OryonTheme
import com.example.oryon.ui.screens.home.HomeScreen
import com.example.oryon.ui.screens.ActivityScreen
import com.example.oryon.ui.screens.ChallengeScreen
import com.example.oryon.ui.components.AppNavigationBar
import com.example.oryon.ui.components.OryonTopAppBar
import com.example.oryon.ui.components.Screen
import com.example.oryon.ui.components.navigationItems
import com.example.oryon.ui.screens.auth.login.LogInViewModel
import com.example.oryon.ui.screens.auth.login.LogInViewModelFactory
import com.example.oryon.ui.screens.auth.login.LoginScreen
import com.example.oryon.ui.screens.auth.signup.SignUpScreen
import com.example.oryon.ui.screens.auth.signup.SignUpViewModel
import com.example.oryon.ui.screens.auth.signup.SignUpViewModelFactory
import com.example.oryon.ui.screens.home.HomeViewModel
import com.example.oryon.ui.screens.home.HomeViewModelFactory
import com.example.oryon.ui.screens.splash.SplashScreen
import com.example.oryon.ui.screens.splash.SplashViewModel
import com.example.oryon.ui.screens.splash.SplashViewModelFactory


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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authRepository = remember { AuthRepositoryImpl() }


    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Activity.route,
        Screen.Challenge.route
    )

    val topBarScreens = listOf(
        Screen.Home.route,
    )

    Scaffold(
        topBar = {
            if (currentRoute in topBarScreens) {
                OryonTopAppBar(
                    authRepository = authRepository,
                    navController = navController
                )
            }
        },
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                AppNavigationBar(navController = navController, items = navigationItems)
            }
        }
    ) { innerPadding ->
        // NavHost zum Anzeigen der Screens
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                val factory = LogInViewModelFactory(authRepository)
                val viewModel: LogInViewModel = viewModel(factory = factory)
                LoginScreen(navController = navController, viewModel = viewModel)
            }
            composable("signup") {
                val factory = SignUpViewModelFactory(authRepository, firestoreRepository = FirestoreRepositoryImpl())
                val viewModel: SignUpViewModel = viewModel(factory = factory)
                SignUpScreen(navController = navController, viewModel = viewModel)
            }
            composable("splash") {
                val factory = SplashViewModelFactory(authRepository)
                val viewModel: SplashViewModel = viewModel(factory = factory)
                SplashScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Home.route) {
                val locationRepository = LocationRepositoryImpl()
                val trackRunUseCase = TrackRunUseCase(locationRepository)
                val factory = HomeViewModelFactory(locationRepository, trackRunUseCase)
                val viewModel: HomeViewModel = viewModel(factory = factory)
                HomeScreen(viewModel)
            }
            composable(Screen.Activity.route) { ActivityScreen() }
            composable(Screen.Challenge.route) { ChallengeScreen() }

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