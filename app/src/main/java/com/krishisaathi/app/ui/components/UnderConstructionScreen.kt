package com.krishisaathi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.NaturalIvory

/**
 * Shared Under Construction screen template following Bakaiya Krishi Saathi Design System:
 * - Natural Ivory (#F6F7EF) canvas background
 * - Borderless Light Green (#DCE9DF) card with 24dp corners
 * - Forest Green (#174D36) Material Symbols Outlined icon and title
 */
@Composable
fun UnderConstructionScreen(
    title: String,
    icon: ImageVector,
    supportingText: String,
    heading: String = "निर्माणाधीन",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = heading,
                        tint = ForestGreen,
                        modifier = Modifier.size(64.dp)
                    )

                    Text(
                        text = heading,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DeepCharcoal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
