package com.krishisaathi.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.ui.screens.channels.YouTubeChannelsScreen
import com.krishisaathi.app.ui.screens.crops.CropDetailScreen
import com.krishisaathi.app.ui.screens.crops.CropDetailViewModel
import com.krishisaathi.app.ui.screens.crops.CropsScreen
import com.krishisaathi.app.ui.screens.crops.CropsViewModel
import com.krishisaathi.app.ui.screens.helpline.HelplineScreen
import com.krishisaathi.app.ui.screens.home.HomeScreen
import com.krishisaathi.app.ui.screens.home.HomeViewModel
import com.krishisaathi.app.ui.screens.home.InfoHubScreen
import com.krishisaathi.app.ui.screens.livestock.AnimalDetailScreen
import com.krishisaathi.app.ui.screens.livestock.AnimalDetailViewModel
import com.krishisaathi.app.ui.screens.livestock.LivestockScreen
import com.krishisaathi.app.ui.screens.livestock.LivestockViewModel
import com.krishisaathi.app.ui.screens.profile.ProfileScreen
import com.krishisaathi.app.ui.screens.registration.RegistrationScreen
import com.krishisaathi.app.ui.screens.registration.RegistrationViewModel
import com.krishisaathi.app.ui.screens.videos.DownloadedVideosScreen
import com.krishisaathi.app.ui.screens.videos.DownloadedVideosViewModel
import com.krishisaathi.app.ui.screens.videos.VideoPlayerScreen

@Composable
fun KrishiSaathiNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier.padding(paddingValues)
    ) {
        // ---- TAB 1: गृहपृष्ठ (Home) ----
        composable(BottomNavItem.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(appContainer)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToCrops = { categoryId ->
                    val route = if (categoryId != null) {
                        "crops?categoryId=$categoryId"
                    } else {
                        BottomNavItem.Crops.route
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToCropDetail = { cropId ->
                    navController.navigate("crop_detail/$cropId") {
                        launchSingleTop = true
                    }
                },
                onNavigateToLivestock = {
                    navController.navigate(BottomNavItem.Livestock.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToHelpline = {
                    navController.navigate(BottomNavItem.Helpline.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToDownloadedVideos = {
                    navController.navigate("downloaded_videos") {
                        launchSingleTop = true
                    }
                },
                onNavigateToYouTubeChannels = {
                    navController.navigate("youtube_channels") {
                        launchSingleTop = true
                    }
                },
                onNavigateToOptionalRegistration = {
                    navController.navigate("registration") {
                        launchSingleTop = true
                    }
                },
                onNavigateToInfoSection = { sectionType ->
                    navController.navigate("info_section/$sectionType") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---- TAB 2: बाली (Crops) ----
        composable(
            route = "crops?categoryId={categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val initialCategoryId = backStackEntry.arguments?.getString("categoryId")
            val cropsViewModel: CropsViewModel = viewModel(
                key = "crops_${initialCategoryId ?: "all"}",
                factory = CropsViewModel.factory(appContainer, initialCategoryId)
            )
            CropsScreen(
                viewModel = cropsViewModel,
                onCropClick = { cropId ->
                    navController.navigate("crop_detail/$cropId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---- TAB 3: पशुपालन (Livestock) ----
        composable(
            route = "livestock?categoryId={categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            val livestockViewModel: LivestockViewModel = viewModel(
                key = "livestock_${categoryId ?: "all"}",
                factory = LivestockViewModel.factory(appContainer, categoryId)
            )
            LivestockScreen(
                viewModel = livestockViewModel,
                onAnimalClick = { animalId ->
                    navController.navigate("animal_detail/$animalId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---- TAB 4: हेल्पलाइन (Helpline) ----
        composable(BottomNavItem.Helpline.route) {
            HelplineScreen(appContainer = appContainer)
        }

        // ---- TAB 5: प्रोफाइल (Profile) ----
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                appContainer = appContainer,
                onNavigateToRegistration = {
                    navController.navigate("registration") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---- DETAIL & HUB SCREENS ----
        composable(
            route = "info_section/{sectionType}",
            arguments = listOf(navArgument("sectionType") { type = NavType.StringType })
        ) { backStackEntry ->
            val sectionType = backStackEntry.arguments?.getString("sectionType") ?: "documents"
            InfoHubScreen(
                sectionType = sectionType,
                appContainer = appContainer,
                onBackClick = { navController.popBackStack() },
                onOpenHelpline = {
                    navController.navigate(BottomNavItem.Helpline.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onOpenCropDetail = { cropId ->
                    navController.navigate("crop_detail/$cropId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "crop_detail/{cropId}",
            arguments = listOf(navArgument("cropId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cropId = backStackEntry.arguments?.getString("cropId") ?: ""
            val detailViewModel: CropDetailViewModel = viewModel(
                factory = CropDetailViewModel.factory(cropId, appContainer)
            )
            CropDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onPlayLocalVideo = { videoId ->
                    navController.navigate("video_player/$videoId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "animal_detail/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getString("animalId") ?: ""
            val animalDetailViewModel: AnimalDetailViewModel = viewModel(
                factory = AnimalDetailViewModel.factory(animalId, appContainer)
            )
            AnimalDetailScreen(
                viewModel = animalDetailViewModel,
                onBackClick = { navController.popBackStack() },
                onPlayLocalVideo = { videoId ->
                    navController.navigate("video_player/$videoId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("downloaded_videos") {
            val downloadedViewModel: DownloadedVideosViewModel = viewModel(
                factory = DownloadedVideosViewModel.factory(appContainer)
            )
            DownloadedVideosScreen(
                viewModel = downloadedViewModel,
                onPlayVideo = { videoId ->
                    navController.navigate("video_player/$videoId") {
                        launchSingleTop = true
                    }
                },
                onBackClick = { navController.popBackStack() },
                onBrowseCropsClick = {
                    navController.navigate(BottomNavItem.Crops.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "video_player/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            VideoPlayerScreen(
                videoId = videoId,
                repository = appContainer.bakaiyaCropRepository,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("youtube_channels") {
            YouTubeChannelsScreen(
                appContainer = appContainer,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("registration") {
            val registrationViewModel: RegistrationViewModel = viewModel(
                factory = RegistrationViewModel.factory(appContainer, androidx.compose.ui.platform.LocalContext.current)
            )
            RegistrationScreen(
                viewModel = registrationViewModel,
                onRegistrationComplete = { navController.popBackStack() },
                dataStoreManager = appContainer.dataStoreManager
            )
        }
    }
}
