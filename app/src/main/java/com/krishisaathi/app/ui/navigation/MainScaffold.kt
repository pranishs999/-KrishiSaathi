package com.krishisaathi.app.ui.navigation

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krishisaathi.app.data.remote.AppSyncController
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Primary scaffold featuring:
 * - Natural Ivory navigation canvas
 * - Deep Charcoal selected state and Muted Green Grey unselected state
 * - Divider Sage structural hairlines
 * - 4 bottom navigation tabs: गृहपृष्ठ, बाली, पशुपालन, किसान हेल्पलाइन
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val syncScope = rememberCoroutineScope()

    // App-wide auto refresh: on foreground resume, on network reconnect, and periodically while open.
    LaunchedEffect(Unit) {
        appContainer.appSyncController.refreshNow(force = false)
        while (isActive) {
            delay(AppSyncController.FOREGROUND_AUTO_REFRESH_INTERVAL_MS)
            appContainer.appSyncController.refreshNow(force = false)
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                syncScope.launch {
                    appContainer.appSyncController.refreshNow(force = false)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                syncScope.launch {
                    appContainer.appSyncController.refreshNow(force = false)
                }
            }
        }
        val callbackRegistered = try {
            connectivityManager?.registerDefaultNetworkCallback(networkCallback)
            connectivityManager != null
        } catch (_: Exception) {
            false
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            if (callbackRegistered) {
                try {
                    connectivityManager?.unregisterNetworkCallback(networkCallback)
                } catch (_: Exception) {
                }
            }
        }
    }

    val showChromeBars = currentRoute == BottomNavItem.Home.route ||
            currentRoute?.startsWith("crops") == true ||
            currentRoute?.startsWith("livestock") == true ||
            currentRoute == BottomNavItem.Helpline.route ||
            currentRoute == BottomNavItem.Profile.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NaturalIvory,
        topBar = {
            if (showChromeBars) {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "बकैया कृषि साथी",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    navController.navigate("downloaded_videos") {
                                        launchSingleTop = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DownloadDone,
                                    contentDescription = "डाउनलोड गरिएका भिडियोहरू",
                                    tint = ForestGreen
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = NaturalIvory,
                            titleContentColor = ForestGreen
                        )
                    )
                    HorizontalDivider(
                        color = DividerSage,
                        thickness = 1.dp
                    )
                }
            }
        },
        bottomBar = {
            if (showChromeBars) {
                Column {
                    HorizontalDivider(
                        color = DividerSage,
                        thickness = 1.dp
                    )
                    NavigationBar(
                        containerColor = NaturalIvory,
                        contentColor = DeepCharcoal,
                        tonalElevation = 0.dp
                    ) {
                        val currentCategoryArg = navBackStackEntry?.arguments?.getString("categoryId")
                        bottomNavItems.forEach { item ->
                            val selected = when (item) {
                                BottomNavItem.Crops -> currentRoute?.startsWith("crops") == true
                                BottomNavItem.Livestock -> currentRoute?.startsWith("livestock") == true || currentRoute?.startsWith("animal_detail") == true
                                BottomNavItem.Profile -> currentRoute == BottomNavItem.Profile.route
                                else -> currentRoute == item.route
                            }
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    val shouldResetCropsFilter = item == BottomNavItem.Crops && currentCategoryArg != null
                                    if (!selected || shouldResetCropsFilter) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = (item != BottomNavItem.Crops)
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DeepCharcoal,
                                    selectedTextColor = DeepCharcoal,
                                    indicatorColor = LightGreen,
                                    unselectedIconColor = MutedGreenGrey,
                                    unselectedTextColor = MutedGreenGrey
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        KrishiSaathiNavHost(
            navController = navController,
            appContainer = appContainer,
            paddingValues = if (showChromeBars) innerPadding else PaddingValues(0.dp)
        )
    }
}
