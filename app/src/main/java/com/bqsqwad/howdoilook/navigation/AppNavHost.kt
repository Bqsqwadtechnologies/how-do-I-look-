package com.bqsqwad.howdoilook.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bqsqwad.howdoilook.feature.history.HistoryScreen
import com.bqsqwad.howdoilook.feature.history.HistoryViewModel
import com.bqsqwad.howdoilook.feature.history.RatingDetailScreen
import com.bqsqwad.howdoilook.feature.history.RatingDetailViewModel
import com.bqsqwad.howdoilook.feature.home.HomeScreen
import com.bqsqwad.howdoilook.feature.rating.CameraScreen
import com.bqsqwad.howdoilook.feature.rating.RatingScreen
import com.bqsqwad.howdoilook.feature.rating.RatingViewModel
import com.bqsqwad.howdoilook.feature.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val RATING = "rating"
    const val CAMERA = "camera"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val RATING_DETAIL = "rating_detail/{ratingId}"

    fun ratingDetail(id: String) = "rating_detail/${Uri.encode(id)}"
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
                onRatingClick = { rating -> navController.navigate(Routes.ratingDetail(rating.id)) }
            )
        }

        composable(Routes.RATING) {
            val ratingVm: RatingViewModel = hiltViewModel()
            RatingScreen(
                onBack = { navController.popBackStack() },
                onOpenCamera = { navController.navigate(Routes.CAMERA) },
                viewModel = ratingVm
            )
        }

        composable(Routes.CAMERA) {
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Routes.RATING)
            }
            val ratingVm: RatingViewModel = hiltViewModel(parentEntry)

            CameraScreen(
                onBack = { navController.popBackStack() },
                onPhotoCaptured = { uri: Uri ->
                    ratingVm.onPhotoSelected(uri)
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = hiltViewModel()
            val ratings by vm.ratings.collectAsStateWithLifecycle()
            HistoryScreen(
                ratings = ratings,
                onBack = { navController.popBackStack() },
                onRatingClick = { rating -> navController.navigate(Routes.ratingDetail(rating.id)) },
                onDelete = vm::delete
            )
        }

        composable(Routes.RATING_DETAIL) {
            val vm: RatingDetailViewModel = hiltViewModel()
            RatingDetailScreen(
                onBack = { navController.popBackStack() },
                viewModel = vm
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
