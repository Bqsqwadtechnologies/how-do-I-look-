package com.bqsqwad.howdoilook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bqsqwad.howdoilook.feature.history.HistoryScreen
import com.bqsqwad.howdoilook.feature.history.HistoryViewModel
import com.bqsqwad.howdoilook.feature.home.HomeScreen
import com.bqsqwad.howdoilook.feature.rating.RatingScreen
import com.bqsqwad.howdoilook.feature.settings.SettingsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

object Routes {
    const val HOME = "home"
    const val RATING = "rating"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onRateLook = { navController.navigate(Routes.RATING) },
                onHistory = { navController.navigate(Routes.HISTORY) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onRatingClick = { /* detail later */ }
            )
        }

        composable(Routes.RATING) {
            RatingScreen(
                onBack = { navController.popBackStack() },
                onOpenCamera = {
                    // Camera screen can be added later; for now gallery is primary
                }
            )
        }

        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = hiltViewModel()
            val ratings by vm.ratings.collectAsStateWithLifecycle()
            HistoryScreen(
                ratings = ratings,
                onBack = { navController.popBackStack() },
                onRatingClick = { /* detail */ },
                onDelete = vm::delete
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
