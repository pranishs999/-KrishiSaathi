package com.krishisaathi.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropCategory
import com.krishisaathi.app.ui.screens.dialogs.RegistrationDialog
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LeafGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.WarningOrange

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCrops: (String?) -> Unit,
    onNavigateToCropDetail: (String) -> Unit,
    onNavigateToHelpline: () -> Unit,
    onNavigateToDownloadedVideos: () -> Unit,
    onNavigateToFarmRecords: () -> Unit,
    onNavigateToYouTubeChannels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Identity & Welcome Header (Section 6.1)
        Column {
            Text(
                text = "बकैया कृषि साथीमा स्वागत छ।",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "बकैया गाउँपालिकाका कृषकहरूका लागि बाली खेती प्रविधि, वातावरण र कृषि परामर्शसम्बन्धी सरल नेपाली ज्ञान केन्द्र।",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepCharcoal
            )
        }

        // Crop Search Action Pill (Light Green background, Deep Charcoal text and icon)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(50))
                .background(LightGreen)
                .clickable { onNavigateToCrops(null) }
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "बाली खोज्नुहोस्",
                tint = DeepCharcoal,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "बाली खोज्नुहोस् (उदा: धान, मकै, काउली)...",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepCharcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Optional Farmer Profile Card (Section 11 - Non-blocking)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "किसान प्रोफाइल",
                            tint = NaturalIvory,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        if (state.farmerProfile != null) {
                            Text(
                                text = "नमस्ते, ${state.farmerProfile!!.fullName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )
                            Text(
                                text = "बकैया वडा नं. ${state.farmerProfile!!.wardNumber} • ${state.farmerProfile!!.phoneNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedGreenGrey
                            )
                        } else {
                            Text(
                                text = "ऐच्छिक किसान विवरण",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )
                            Text(
                                text = "विना लगइन सबै जानकारी पढ्न सकिन्छ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedGreenGrey
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = viewModel::openRegistrationDialog,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = NaturalIvory
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = if (state.farmerProfile == null) "दर्ता" else "परिमार्जन",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Crop Categories Grid (Section 6.2 - No invented counts)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "बालीका वर्गहरू",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                Text(
                    text = "सबै हेर्नुहोस्",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreen,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onNavigateToCrops(null) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.categories.chunked(2).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCategories.forEach { category ->
                            CategoryCard(
                                category = category,
                                onClick = { onNavigateToCrops(category.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Shortcuts: Farm Records, Helpline, Downloaded Videos & YouTube Channels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onNavigateToFarmRecords),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "फार्म हिसाब",
                        tint = ForestGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "फार्म हिसाब",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "आम्दानी खर्च लेखा",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onNavigateToDownloadedVideos),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DownloadDone,
                        contentDescription = "डाउनलोड गरिएका भिडियो",
                        tint = ForestGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "डाउनलोड भिडियो",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "अफलाइन पुस्तकालय",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onNavigateToYouTubeChannels),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircle,
                        contentDescription = "कृषि युट्यूब",
                        tint = ForestGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "कृषि युट्यूब",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "प्रविधि च्यानलहरू",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onNavigateToHelpline),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Call,
                        contentDescription = "किसान हेल्पलाइन",
                        tint = ForestGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "किसान हेल्पलाइन",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "कृषि शाखा सम्पर्क",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                }
            }
        }

        // Verified Seasonal Crops Highlights (Section 6.1)
        if (state.seasonalCrops.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "मौसमी खेती समय जानकारी (NARC)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.seasonalCrops.forEachIndexed { index, crop ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToCropDetail(crop.id) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${crop.nameNe} (${crop.nameEn})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                                Text(
                                    text = crop.plantingSeason ?: "जानकारी उपलब्ध छैन",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DeepCharcoal
                                )
                            }
                            if (index < state.seasonalCrops.lastIndex) {
                                HorizontalDivider(color = DividerSage, thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }

        // Featured Crops
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "बकैयाका प्रमुख बालीहरू",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal
            )

            state.featuredCrops.forEach { crop ->
                FeaturedCropCard(
                    crop = crop,
                    onClick = { onNavigateToCropDetail(crop.id) }
                )
            }
        }
    }

    if (state.isRegistrationDialogOpen) {
        RegistrationDialog(
            currentName = state.farmerProfile?.fullName ?: "",
            currentWard = state.farmerProfile?.wardNumber ?: 1,
            currentPhone = state.farmerProfile?.phoneNumber ?: "",
            isExistingProfile = state.farmerProfile != null,
            onDismiss = viewModel::closeRegistrationDialog,
            onConfirm = viewModel::registerFarmer,
            onClearProfile = viewModel::clearRegistration
        )
    }
}

@Composable
fun CategoryCard(
    category: CropCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = categoryIconFor(category.iconName),
                contentDescription = category.nameNe,
                tint = ForestGreen,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = category.nameNe,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                Text(
                    text = category.nameEn,
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
fun FeaturedCropCard(
    crop: BakaiyaCrop,
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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CropThumbnailImage(
                imageUrl = crop.thumbnailUrl,
                contentDescription = crop.nameNe,
                modifier = Modifier.size(76.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${crop.nameNe} (${crop.nameEn})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                Text(
                    text = crop.scientificName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey
                )
                crop.plantingSeason?.let { season ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = season,
                            style = MaterialTheme.typography.labelSmall,
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CropThumbnailImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SoftSage),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Outlined.Agriculture,
                contentDescription = contentDescription,
                tint = LeafGreen,
                modifier = Modifier.size(32.dp)
            )
        } else {
            SubcomposeAsyncImage(
                model = imageUrl,
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

private fun categoryIconFor(iconName: String?): ImageVector {
    return when (iconName) {
        "eco" -> Icons.Outlined.Eco
        "park" -> Icons.Outlined.Park
        "spa" -> Icons.Outlined.Spa
        "water_drop" -> Icons.Outlined.WaterDrop
        "storefront" -> Icons.Outlined.Storefront
        else -> Icons.Outlined.Grass
    }
}
