package com.krishisaathi.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.krishisaathi.app.ui.navigation.MainScaffold
import com.krishisaathi.app.ui.screens.splash.SplashScreen
import com.krishisaathi.app.ui.theme.KrishiSaathiTheme

private enum class RootStage {
    SPLASH,
    MAIN
}

/**
 * Primary Entry Activity for Bakaiya Krishi Saathi (बकैया कृषि साथी).
 * Enforces Light Mode only. Farmer browsing does not require registration.
 * Optional registration is accessible non-blockingly from the Home screen.
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
                var showSplash by rememberSaveable { mutableStateOf(true) }

                val rootStage = if (showSplash) RootStage.SPLASH else RootStage.MAIN

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

                        RootStage.MAIN -> {
                            MainScaffold(appContainer = appContainer)
                        }
                    }
                }
            }
        }
    }
}
