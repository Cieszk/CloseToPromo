package pl.cieszk.closetopromo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.cieszk.closetopromo.ui.screen.DetailScreen
import pl.cieszk.closetopromo.ui.screen.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController)}
        composable(
            route = "detail/{discountId}",
            arguments = listOf(navArgument("discountId") { type = NavType.StringType })
        ) { backStackEntry ->
            val discountId = backStackEntry.arguments?.getString("discountId")
            DetailScreen(itemId = discountId, navController = navController)
        }
    }
}