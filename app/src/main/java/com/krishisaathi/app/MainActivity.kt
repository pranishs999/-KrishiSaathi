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

/**
 * Primary Entry Activity for Bakaiya Krishi Saathi (बकैया कृषि साथी).
 * Enforces Light Mode and provides immediate frictionless access to all agricultural resources
 * without any login, OTP, or password gates.
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

                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(durationMillis = 300),
                    label = "SplashCrossfade"
                ) { isSplash ->
                    if (isSplash) {
                        SplashScreen(
                            onSplashComplete = {
                                showSplash = false
                            }
                        )
                    } else {
                        MainScaffold(appContainer = appContainer)
                    }
                }
            }
        }
    }
}
