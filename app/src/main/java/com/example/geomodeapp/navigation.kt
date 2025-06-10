package com.example.geomodeapp

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geomodeapp.ui.viewmodels.GeoModeViewModel
import com.example.geomodeapp.ui.views.HomeScreenUI
import com.example.geomodeapp.ui.views.MapScreenUI
import kotlinx.serialization.Serializable

@Composable
fun Navigation(){

    val geoModeViewModel:GeoModeViewModel = hiltViewModel()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeScreenRoute
    ){
        composable<HomeScreenRoute>{
            HomeScreenUI(navController,geoModeViewModel)
        }

        composable<MapScreenRoute>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(900),
                    targetOffsetX = { it }
                )
            }
        ){
            MapScreenUI(navController,geoModeViewModel)
        }
    }
}

@Serializable
object HomeScreenRoute

@Serializable
object MapScreenRoute

