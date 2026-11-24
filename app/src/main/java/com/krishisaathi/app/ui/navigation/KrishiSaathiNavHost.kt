package com.krishisaathi.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.krishisaathi.app.ui.screens.crops.CropsScreen
import com.krishisaathi.app.ui.screens.home.HomeScreen
import com.krishisaathi.app.ui.screens.profile.ProfileScreen
import com.krishisaathi.app.ui.screens.weather.WeatherScreen

@Composable
fun KrishiSaathiNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier.padding(paddingValues)
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(BottomNavItem.Crops.route) {
            CropsScreen()
        }
        composable(BottomNavItem.Weather.route) {
            WeatherScreen()
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}

