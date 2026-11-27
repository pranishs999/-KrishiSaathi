package com.krishisaathi.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.krishisaathi.app.ui.components.KrishiPullToRefreshBox
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LeafGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCrops: (String?) -> Unit,
    onNavigateToCropDetail: (String) -> Unit,
    onNavigateToLivestock: () -> Unit = {},
    onNavigateToHelpline: () -> Unit = {},
    onNavigateToDownloadedVideos: () -> Unit,
    onNavigateToYouTubeChannels: () -> Unit,
    onNavigateToOptionalRegistration: () -> Unit = {},
    onNavigateToInfoSection: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    KrishiPullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // App Identity & Personalized Welcome Header
        val farmer = state.farmerRegistration
        Column {
            Text(
                text = if (farmer != null && farmer.fullName.isNotBlank()) {
                    "स्वागत छ, ${farmer.fullName}!"
                } else {
                    "बकैया कृषि साथीमा स्वागत छ।"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (farmer != null) {
                    "${farmer.municipality}, वडा नं. ${farmer.wardNumber} — कृषि तथा पशुपालन ज्ञान केन्द्र"
                } else {
                    "बकैया गाउँपालिका, मकवानपुरका किसानहरूको भरपर्दो कृषि तथा पशुपालन ज्ञान केन्द्र"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = DeepCharcoal
            )
        }

        // TOP TWO MAJOR SECTIONS SIDE BY SIDE
        // LEFT: बाली व्यवस्थापन | RIGHT: पशुपालन तथा माछापालन
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryHeroFeatureCard(
                titleNe = "बाली व्यवस्थापन",
                subtitleNe = if (state.totalCropsCount > 0) {
                    "${state.totalCropsCount} बाली प्रविधि (सिंक उपलब्ध)"
                } else {
                    "Supabase बाट सिंक गर्नुहोस्"
                },
                imageUrl = state.heroCropImageUrl,
                fallbackIcon = Icons.Outlined.Agriculture,
                onClick = { onNavigateToCrops(null) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            PrimaryHeroFeatureCard(
                titleNe = "पशुपालन तथा माछापालन",
                subtitleNe = state.livestockSubtitleNe.ifBlank {
                    if (state.totalAnimalsCount > 0) {
                        "${state.totalAnimalsCount} पशु तथा मत्स्य विवरण"
                    } else {
                        "Supabase बाट सिंक गर्नुहोस्"
                    }
                },
                imageUrl = state.heroLivestockImageUrl,
                fallbackIcon = Icons.Outlined.Pets,
                onClick = onNavigateToLivestock,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        if (state.syncErrorNe != null && state.totalCropsCount == 0 && state.totalAnimalsCount == 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSage)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = state.syncErrorNe ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = DeepCharcoal
                    )
                    Button(
                        onClick = viewModel::refresh,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("अहिले सिंक गर्नुहोस्", color = NaturalIvory)
                    }
                }
            }
        }

        // FOUR PRESERVED MAIN SECTIONS BELOW
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeSectionTile(
                titleNe = "सरकारी कृषि दस्तावेज",
                subtitleNe = "NARC, कृषि मन्त्रालय र बकैया गाउँपालिका सन्दर्भ सामग्री",
                icon = Icons.Outlined.Description,
                onClick = { onNavigateToInfoSection("documents") }
            )

            HomeSectionTile(
                titleNe = "कृषि भिडियो",
                subtitleNe = "प्रमाणित कृषि युट्यूब च्यानल तथा अफलाइन भिडियो सामग्री",
                icon = Icons.Outlined.PlayCircle,
                onClick = onNavigateToYouTubeChannels
            )

            HomeSectionTile(
                titleNe = "कृषि अनुदान तथा कार्यक्रम",
                subtitleNe = "बकैया गाउँपालिका कृषि तथा पशु सेवा समन्वय विवरण",
                icon = Icons.Outlined.AccountBalance,
                onClick = { onNavigateToInfoSection("subsidies") }
            )

            HomeSectionTile(
                titleNe = "सूचना तथा समाचार",
                subtitleNe = "मौसमी खेती समय सल्लाह तथा आधिकारिक कृषि सूचना",
                icon = Icons.Outlined.Campaign,
                onClick = { onNavigateToInfoSection("news") }
            )
        }
        }
    }
}

@Composable
private fun PrimaryHeroFeatureCard(
    titleNe: String,
    subtitleNe: String,
    imageUrl: String?,
    fallbackIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CropThumbnailImage(
                imageUrl = imageUrl,
                contentDescription = titleNe,
                fallbackIcon = fallbackIcon,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = titleNe,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitleNe,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGreenGrey,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HomeSectionTile(
    titleNe: String,
    subtitleNe: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ForestGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = titleNe,
                    tint = NaturalIvory,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleNe,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitleNe,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun CropThumbnailImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    fallbackIcon: ImageVector = Icons.Outlined.Agriculture
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SoftSage),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = contentDescription,
                tint = LeafGreen,
                modifier = Modifier.size(32.dp)
            )
        } else {
            val context = LocalContext.current
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LightGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Eco,
                            contentDescription = null,
                            tint = LeafGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SoftSage),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BrokenImage,
                            contentDescription = "तस्बिर लोड हुन सकेन",
                            tint = MutedGreenGrey,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    }
}
