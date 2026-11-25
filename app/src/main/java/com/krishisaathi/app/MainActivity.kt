package com.krishisaathi.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krishisaathi.app.ui.navigation.MainScaffold
import com.krishisaathi.app.ui.screens.registration.RegistrationScreen
import com.krishisaathi.app.ui.screens.registration.RegistrationViewModel
import com.krishisaathi.app.ui.screens.splash.SplashScreen
import com.krishisaathi.app.ui.theme.KrishiSaathiTheme

private enum class RootStage {
    SPLASH,
    REGISTRATION,
    MAIN
}

/**
 * Primary Entry Activity for Bakaiya Krishi Saathi (बकैया कृषि साथी).
 * Enforces Light Mode and verifies persistent first-launch registration status
 * before granting access to the main agricultural portal.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        val appContainer = (application as KrishiSaathiApp).appContainer

        setContent {
            KrishiSaathiTheme {
                val mainViewModel: MainViewModel = viewModel(
                    factory = MainViewModel.factory(appContainer)
                )
                val entryState by mainViewModel.entryState.collectAsState()
                var showSplash by rememberSaveable { mutableStateOf(true) }

                val rootStage = when {
                    showSplash || entryState is AppEntryState.Loading -> RootStage.SPLASH
                    entryState is AppEntryState.NeedsRegistration -> RootStage.REGISTRATION
                    else -> RootStage.MAIN
                }

                Crossfade(
                    targetState = rootStage,
                    animationSpec = tween(durationMillis = 300),
                    label = "RootStageCrossfade"
                ) { stage ->
                    when (stage) {
                        RootStage.SPLASH -> {
                            SplashScreen(
                                onSplashComplete = {
                                    showSplash = false
                                }
                            )
                        }

                        RootStage.REGISTRATION -> {
                            val registrationViewModel: RegistrationViewModel = viewModel(
                                factory = RegistrationViewModel.factory(appContainer)
                            )
                            RegistrationScreen(viewModel = registrationViewModel)
                        }

                        RootStage.MAIN -> {
                            MainScaffold(appContainer = appContainer)
                        }
                    }
                }
            }
        }
    }
}
