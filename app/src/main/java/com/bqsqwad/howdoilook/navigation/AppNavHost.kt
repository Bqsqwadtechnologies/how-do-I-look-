package com.bqsqwad.howdoilook.navigation

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bqsqwad.howdoilook.core.data.preferences.UserPreferences
import com.bqsqwad.howdoilook.feature.StyleStudioScreen
import com.bqsqwad.howdoilook.feature.history.HistoryScreen
import com.bqsqwad.howdoilook.feature.history.HistoryViewModel
import com.bqsqwad.howdoilook.feature.history.RatingDetailScreen
import com.bqsqwad.howdoilook.feature.history.RatingDetailViewModel
import com.bqsqwad.howdoilook.feature.home.HomeScreen
import com.bqsqwad.howdoilook.feature.rating.CameraScreen
import com.bqsqwad.howdoilook.feature.rating.RatingScreen
import com.bqsqwad.howdoilook.feature.rating.RatingViewModel
import com.bqsqwad.howdoilook.feature.settings.AgeGateScreen
import com.bqsqwad.howdoilook.feature.settings.SettingsScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object Routes {
    const val AGE_GATE = "age_gate"
    const val HOME = "home"
    const val RATING = "rating"
    const val CAMERA = "camera"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val STYLE_STUDIO = "style_studio"
    const val RATING_DETAIL = "rating_detail/{ratingId}"

    fun ratingDetail(id: String) = "rating_detail/${Uri.encode(id)}"
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PrefsEntryPoint {
    fun userPreferences(): UserPreferences
}

@Composable
fun AppNavHost() {
    val context = LocalContext.current
    val prefs = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            PrefsEntryPoint::class.java
        ).userPreferences()
    }
    var ageReady by remember { mutableStateOf(false) }
    var ageConfirmed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        ageConfirmed = prefs.ageConfirmed.first()
        ageReady = true
    }

    if (!ageReady) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val start = if (ageConfirmed) Routes.HOME else Routes.AGE_GATE

    NavHost(
        navController = navController,
        startDestination = start
    ) {
        composable(Routes.AGE_GATE) {
            val activity = context as? ComponentActivity
            AgeGateScreen(
                onConfirm13OrOlder = {
                    scope.launch {
                        prefs.setAgeConfirmed(true)
                        ageConfirmed = true
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AGE_GATE) { inclusive = true }
                        }
                    }
                },
                onUnder13 = { activity?.finish() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onRateLook = { navController.navigate(Routes.RATING) },
                onHistory = { navController.navigate(Routes.HISTORY) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onStyleStudio = { navController.navigate(Routes.STYLE_STUDIO) },
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
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.STYLE_STUDIO) {
            StyleStudioScreen(onBack = { navController.popBackStack() })
        }
    }
}
