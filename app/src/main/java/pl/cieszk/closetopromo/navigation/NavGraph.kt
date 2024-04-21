package pl.cieszk.closetopromo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.cieszk.closetopromo.ui.screen.DetailScreen
import pl.cieszk.closetopromo.ui.screen.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController)}
        composable("details") { DetailScreen(navController)}
    }
}