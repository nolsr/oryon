package com.example.oryon.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.oryon.R
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy

sealed class Screen(val route: String, val label: String, @DrawableRes val iconResId: Int) {
    object Home : Screen("home", "Home", R.drawable.lucide_map)
    object Activity : Screen("activity", "Aktivität", R.drawable.lucide_route)
    object Challenge : Screen("challenge", "Challenge", R.drawable.lucide_trophy)
}

val navigationItems = listOf(
    Screen.Home,
    Screen.Activity,
    Screen.Challenge
    // Füge hier weitere Screens aus Screen hinzu
)

@Composable
fun AppNavigationBar(
    navController: NavController,
    items: List<Screen>
) {
    NavigationBar(
        // Hier kannst du Modifier für die gesamte NavigationBar hinzufügen,
        // z.B. containerColor, tonalElevation etc.
        containerColor = MaterialTheme.colorScheme.surface, // Beispiel aus deinem Code
        // tonalElevation = NavigationBarDefaults.TonalElevation // Standard Elevation
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconResId),
                        contentDescription = screen.label
                    )
                },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer, //sets the color of the icon when the item is selected
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer, //sets the color of the text when the item is selected
                    indicatorColor = MaterialTheme.colorScheme.primary, //sets the background color of the item when it is selected
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer, //sets the color of the icon when the item is not selected
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer //sets the color of the text when the item is not selected
                )
                // Hier könntest du auch einen Modifier für jedes NavigationBarItem hinzufügen, falls nötig
            )
        }
    }
}