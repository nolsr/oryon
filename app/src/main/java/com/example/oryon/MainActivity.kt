package com.example.oryon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.oryon.data.firebase.AuthRepositoryImpl
import com.example.oryon.data.firebase.FirestoreRepositoryImpl
import com.example.oryon.data.location.LocationRepositoryImpl
import com.example.oryon.domain.TrackRunUseCase
import com.example.oryon.ui.theme.OryonTheme
import com.example.oryon.ui.screens.home.HomeScreen
import com.example.oryon.ui.screens.activity.ActivityScreen
import com.example.oryon.ui.screens.challenge.ChallengeScreen
import com.example.oryon.ui.components.AppNavigationBar
import com.example.oryon.ui.components.OryonTopAppBar
import com.example.oryon.ui.components.Screen
import com.example.oryon.ui.components.navigationItems
import com.example.oryon.ui.screens.activity.ActivityViewModel
import com.example.oryon.ui.screens.activity.ActivityViewModelFactory
import com.example.oryon.ui.screens.auth.login.LogInViewModel
import com.example.oryon.ui.screens.auth.login.LogInViewModelFactory
import com.example.oryon.ui.screens.auth.login.LoginScreen
import com.example.oryon.ui.screens.auth.signup.SignUpScreen
import com.example.oryon.ui.screens.auth.signup.SignUpViewModel
import com.example.oryon.ui.screens.auth.signup.SignUpViewModelFactory
import com.example.oryon.ui.screens.challenge.ChallengeViewModel
import com.example.oryon.ui.screens.challenge.ChallengeViewModelFactory
import com.example.oryon.ui.screens.challengeDetail.ChallengeDetailScreen
import com.example.oryon.ui.screens.runDetail.RunDetailScreen
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

//App Entry mit Scaffold und Navigation
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authRepository = remember { AuthRepositoryImpl() }


    // Liste wo auf welchen Seiten Bottom Bar zu sehen ist
    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Activity.route,
        Screen.Challenge.route,
        "runDetail/{runId}",
        "challengeDetail/{challengeId}"
    )

    // Liste wo auf welchen Seiten Top Bar zu sehen ist
    val topBarScreens = listOf(
        Screen.Home.route,
        Screen.Activity.route,
        Screen.Challenge.route,
        "challengeDetail/{challengeId}"
    )

    Scaffold(
        topBar = {
            if (currentRoute in topBarScreens) {

                val title = when (currentRoute) {
                    Screen.Home.route -> "Oryon"
                    Screen.Activity.route -> "Aktivität"
                    Screen.Challenge.route -> "Challenges"
                    "challengeDetail/{challengeId}" -> "Challenge"
                    else -> "Oryon"
                }

                OryonTopAppBar(
                    authRepository = authRepository,
                    navController = navController,
                    text = title
                )
            }
        },

        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                AppNavigationBar(navController = navController, items = navigationItems)
            }
        }
    ) { innerPadding ->
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
                val factory = SignUpViewModelFactory(authRepository, firestoreRepository = FirestoreRepositoryImpl(authRepository))
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
                val firestoreRepository = FirestoreRepositoryImpl(authRepository)
                val trackRunUseCase = TrackRunUseCase(locationRepository, firestoreRepository)
                val factory = HomeViewModelFactory(locationRepository, trackRunUseCase)
                val viewModel: HomeViewModel = viewModel(factory = factory)
                HomeScreen(viewModel)
            }
            composable(Screen.Activity.route) {
                val factory = ActivityViewModelFactory(authRepository, firestoreRepository = FirestoreRepositoryImpl(authRepository))
                val viewModel: ActivityViewModel = viewModel(factory = factory)
                ActivityScreen(viewModel, navController = navController)
            }
            composable("runDetail/{runId}") { backStackEntry ->
                val runId = backStackEntry.arguments?.getString("runId")
                println("Navigated to RunDetailScreen with runId = $runId")
                val factory = ActivityViewModelFactory(authRepository, FirestoreRepositoryImpl(authRepository))
                val viewModel: ActivityViewModel = viewModel(factory = factory)
                runId?.let {
                    RunDetailScreen(runId = it, viewModel = viewModel, navController = navController)
                }
            }

            composable(Screen.Challenge.route) {
                val factory = ChallengeViewModelFactory(firestoreRepository = FirestoreRepositoryImpl(authRepository), authRepository = authRepository)
                val viewModel: ChallengeViewModel = viewModel(factory = factory)
                ChallengeScreen(viewModel, navController)

            }

            composable("challengeDetail/{challengeId}") { backStackEntry ->
                val challengeId = backStackEntry.arguments?.getString("challengeId")
                val factory = ChallengeViewModelFactory(firestoreRepository = FirestoreRepositoryImpl(authRepository), authRepository = authRepository)
                val viewModel: ChallengeViewModel = viewModel(factory = factory)
                challengeId?.let {
                    ChallengeDetailScreen(challengeId = it, viewModel = viewModel)
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OryonTheme {
        MainApp()
    }
}