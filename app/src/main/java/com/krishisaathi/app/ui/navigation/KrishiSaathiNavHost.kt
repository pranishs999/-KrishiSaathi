package com.krishisaathi.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.krishisaathi.app.ui.screens.profile.ProfileScreen
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
                onNavigateToDownloadedVideos = {
                    navController.navigate("downloaded_videos") {
                        launchSingleTop = true
                    }
                },
                onNavigateToYouTubeChannels = {
                    navController.navigate("youtube_channels") {
                        launchSingleTop = true
                    }
                }
            )
        }

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

        composable(BottomNavItem.Helpline.route) {
            HelplineScreen()
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
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

        composable("downloaded_videos") {
            val downloadedViewModel: DownloadedVideosViewModel = viewModel(
                factory = DownloadedVideosViewModel.factory(appContainer)
            )
            DownloadedVideosScreen(
                viewModel = downloadedViewModel,
                onBackClick = { navController.popBackStack() },
                onPlayVideo = { videoId ->
                    navController.navigate("video_player/$videoId") {
                        launchSingleTop = true
                    }
                },
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

        composable("youtube_channels") {
            YouTubeChannelsScreen(
                onBackClick = { navController.popBackStack() }
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
    }
}
