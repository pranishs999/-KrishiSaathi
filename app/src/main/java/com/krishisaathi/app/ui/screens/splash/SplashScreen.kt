package com.krishisaathi.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.R
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import kotlinx.coroutines.delay

/**
 * Clean, calm boot screen adhering strictly to Bakaiya Krishi Saathi Design System:
 * - Natural Ivory light mode canvas (no gradients, no neon, no borders, no excessive animation)
 * - Direct transition into the agricultural knowledge portal without authentication gates
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450)
        )
        delay(950)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .alpha(contentAlpha.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_krishi_logo),
                contentDescription = "बकैया कृषि साथी लोगो",
                modifier = Modifier.size(104.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "बकैया कृषि साथी",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "बकैया गाउँपालिका • मकवानपुर",
                style = MaterialTheme.typography.titleSmall,
                color = DeepCharcoal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "कृषि ज्ञान र परामर्श प्रणाली",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreenGrey,
                textAlign = TextAlign.Center
            )
        }
    }
}
