package com.krishisaathi.app.ui.screens.crops

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropImage
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.model.VideoTopic
import com.krishisaathi.app.media.VideoDownloadState
import com.krishisaathi.app.media.YouTubeLaunchResult
import com.krishisaathi.app.media.YouTubeLauncher
import com.krishisaathi.app.ui.screens.home.CropThumbnailImage
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ErrorMaroon
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LeafGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.PureWhite
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen
import com.krishisaathi.app.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropDetailScreen(
    viewModel: CropDetailViewModel,
    onBackClick: () -> Unit,
    onPlayLocalVideo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NaturalIvory,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = state.crop?.nameNe ?: "बाली विवरण",
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "पछाडि फर्कनुहोस्",
                                tint = DeepCharcoal
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NaturalIvory,
                        titleContentColor = DeepCharcoal
                    )
                )
                HorizontalDivider(color = DividerSage, thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        if (state.isLoading && state.crop == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ForestGreen)
            }
        } else if (state.crop == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "बालीको विवरण उपलब्ध छैन।",
                    style = MaterialTheme.typography.titleMedium,
                    color = MutedGreenGrey
                )
            }
        } else {
            val crop = state.crop!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(NaturalIvory)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Photo & Section 1: बालीको परिचय (Introduction) + Source Badge
                CropOverviewHeaderCard(crop = crop)

                // Section 2: उपयुक्त जातहरू (Varieties)
                crop.varieties?.takeIf { it.isNotBlank() }?.let { varieties ->
                    ExpandableSectionCard(
                        numberLabel = "२",
                        title = "उपयुक्त जातहरू",
                        initiallyExpanded = true
                    ) {
                        Text(
                            text = varieties,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeepCharcoal
                        )
                    }
                }

                // Section 3: खेती गर्ने उपयुक्त समय & Section 4: हावापानी तथा वातावरण
                ExpandableSectionCard(
                    numberLabel = "३-४",
                    title = "खेती समय र हावापानी",
                    initiallyExpanded = true
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailInfoLine(
                            label = "३. खेती गर्ने उपयुक्त समय",
                            value = crop.plantingSeason ?: "जानकारी उपलब्ध छैन"
                        )
                        HorizontalDivider(color = DividerSage, thickness = 1.dp)
                        DetailInfoLine(
                            label = "४. हावापानी तथा वातावरण",
                            value = crop.climateOverview ?: "जानकारी उपलब्ध छैन"
                        )
                    }
                }

                // Sections 5 - 11: Structured Environmental Requirement Cards (Section 6.5)
                EnvironmentalRequirementsSection(crop = crop)

                // Sections 12 - 20: Cultivation Guidance (Expandable numbered instructions)
                Text(
                    text = "खेती प्रविधि तथा व्यवस्थापन चरणहरू",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )

                val cultivationSections = listOf(
                    Triple("१२", "जग्गा तयारी", crop.landPreparation),
                    Triple("१३", "बीउ छनोट तथा रोपाइँ", crop.seedSelection),
                    Triple("१४", "दूरी तथा रोपाइँ विधि", crop.spacing),
                    Triple("१५", "मलखाद तथा पोषण व्यवस्थापन", crop.fertilizer),
                    Triple("१६", "सिँचाइ व्यवस्थापन", crop.irrigation),
                    Triple("१७", "बालीको हेरचाह", crop.cropCare),
                    Triple("१८", "रोग तथा कीरा व्यवस्थापन", crop.pestDiseaseControl),
                    Triple("१९", "कटानी", crop.harvesting),
                    Triple("२०", "उत्पादन तथा भण्डारण", crop.storage)
                ).filter { !it.third.isNullOrBlank() }

                cultivationSections.forEachIndexed { idx, (num, title, content) ->
                    ExpandableSectionCard(
                        numberLabel = num,
                        title = title,
                        initiallyExpanded = idx == 0
                    ) {
                        Text(
                            text = content ?: "जानकारी उपलब्ध छैन",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeepCharcoal
                        )
                    }
                }

                // Section 21: तस्बिरहरू (Agricultural Image Gallery - Section 7)
                CropImageGallerySection(
                    images = state.images,
                    onImageClick = viewModel::openFullScreenImage
                )

                // Section 22: भिडियोबाट सिक्नुहोस् (Topic Playlists & Authorized Offline Videos - Section 8)
                CropVideoLearningSection(
                    videos = state.videos,
                    selectedTopicId = state.selectedTopicId,
                    downloadStates = state.downloadStates,
                    launchErrorMessage = state.launchErrorMessage,
                    onSelectTopic = viewModel::selectTopic,
                    onOpenYouTube = { video ->
                        when (val result = YouTubeLauncher.openYouTubeVideo(context, video.videoUrl)) {
                            is YouTubeLaunchResult.Launched -> viewModel.setLaunchError(null)
                            is YouTubeLaunchResult.Error -> viewModel.setLaunchError(result.messageNe)
                        }
                    },
                    onDownloadClick = viewModel::startOrResumeDownload,
                    onPauseClick = viewModel::pauseDownload,
                    onPlayOfflineClick = { video -> onPlayLocalVideo(video.id) },
                    onDeleteClick = viewModel::deleteDownloadedVideo
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Full-screen Image Gallery Viewer Modal (Section 7)
    state.fullScreenImage?.let { img ->
        FullScreenImageDialog(
            image = img,
            onDismiss = viewModel::closeFullScreenImage
        )
    }
}

@Composable
private fun CropOverviewHeaderCard(crop: BakaiyaCrop) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!crop.thumbnailUrl.isNullOrBlank()) {
                CropThumbnailImage(
                    imageUrl = crop.thumbnailUrl,
                    contentDescription = crop.nameNe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${crop.nameNe} (${crop.nameEn})",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = crop.scientificName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MutedGreenGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Verified Source Attribution Tag (Section 6.5)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(SoftSage)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (crop.isVerifiedContent) Icons.Outlined.Verified else Icons.Outlined.Info,
                    contentDescription = null,
                    tint = if (crop.isVerifiedContent) SuccessGreen else WarningOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = crop.sourceReference,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DividerSage, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "१. बालीको परिचय",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = crop.descriptionNe,
                style = MaterialTheme.typography.bodyLarge,
                color = DeepCharcoal
            )
        }
    }
}

@Composable
private fun EnvironmentalRequirementsSection(crop: BakaiyaCrop) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "वातावरणीय आवश्यकता (५ - ११)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )

        val tempText = if (crop.tempMin != null && crop.tempMax != null) {
            "${crop.tempMin.toInt()}°C देखि ${crop.tempMax.toInt()}°C"
        } else {
            "जानकारी उपलब्ध छैन"
        }

        val humidityText = if (crop.humidityMin != null && crop.humidityMax != null) {
            "${crop.humidityMin.toInt()}% देखि ${crop.humidityMax.toInt()}%"
        } else {
            "जानकारी उपलब्ध छैन"
        }

        val phText = if (crop.soilPhMin != null && crop.soilPhMax != null) {
            "${crop.soilPhMin} देखि ${crop.soilPhMax}"
        } else {
            "जानकारी उपलब्ध छैन"
        }

        // Structured 2-column grid of Environmental Cards (Section 6.5)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EnvironmentMetricCard(
                icon = Icons.Outlined.Thermostat,
                title = "५. तापक्रम",
                value = tempText,
                modifier = Modifier.weight(1f)
            )
            EnvironmentMetricCard(
                icon = Icons.Outlined.WaterDrop,
                title = "६. सापेक्षिक आर्द्रता",
                value = humidityText,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EnvironmentMetricCard(
                icon = Icons.Outlined.Layers,
                title = "७. माटोको प्रकार",
                value = crop.soilTypes ?: "जानकारी उपलब्ध छैन",
                modifier = Modifier.weight(1f)
            )
            EnvironmentMetricCard(
                icon = Icons.Outlined.Science,
                title = "८. माटोको pH",
                value = phText,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EnvironmentMetricCard(
                icon = Icons.Outlined.WbSunny,
                title = "९. घामको आवश्यकता",
                value = crop.sunlight ?: "जानकारी उपलब्ध छैन",
                modifier = Modifier.weight(1f)
            )
            EnvironmentMetricCard(
                icon = Icons.Outlined.WaterDrop,
                title = "१०. पानीको आवश्यकता",
                value = crop.waterRequirement ?: "जानकारी उपलब्ध छैन",
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            EnvironmentMetricCard(
                icon = Icons.Outlined.Terrain,
                title = "११. उपयुक्त उचाइ",
                value = crop.altitude ?: "जानकारी उपलब्ध छैन",
                modifier = Modifier.weight(1f)
            )
            EnvironmentMetricCard(
                icon = Icons.Outlined.CalendarMonth,
                title = "रोपाइँ मौसम",
                value = crop.plantingSeason ?: "जानकारी उपलब्ध छैन",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EnvironmentMetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ForestGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = if (value == "जानकारी उपलब्ध छैन") MutedGreenGrey else DeepCharcoal,
                fontWeight = if (value == "जानकारी उपलब्ध छैन") FontWeight.Normal else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ExpandableSectionCard(
    numberLabel: String,
    title: String,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$numberLabel. $title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (expanded) "बन्द गर्नुहोस्" else "खोल्नुहोस्",
                    tint = ForestGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(color = DividerSage, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun CropImageGallerySection(
    images: List<CropImage>,
    onImageClick: (CropImage) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "२१. तस्बिरहरू (तस्बिर ग्यालरी)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )

        if (images.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "यस बालीका अतिरिक्त तस्बिरहरू हाल उपलब्ध छैनन्।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGreenGrey,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                images.forEach { image ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onImageClick(image) },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGreen),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            CropThumbnailImage(
                                imageUrl = image.imageUrl,
                                contentDescription = image.captionNe,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = image.stageCategory,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = image.captionNe,
                                style = MaterialTheme.typography.bodySmall,
                                color = DeepCharcoal,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullScreenImageDialog(
    image: CropImage,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.88f))
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "बन्द गर्नुहोस्",
                    tint = PureWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SubcomposeAsyncImage(
                    model = image.imageUrl,
                    contentDescription = image.captionNe,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = LightGreen)
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(LightGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BrokenImage,
                                contentDescription = "तस्बिर उपलब्ध छैन",
                                tint = MutedGreenGrey,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreen)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = image.stageCategory,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = image.captionNe,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeepCharcoal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CropVideoLearningSection(
    videos: List<EducationalVideo>,
    selectedTopicId: Int?,
    downloadStates: Map<String, VideoDownloadState>,
    launchErrorMessage: String?,
    onSelectTopic: (Int?) -> Unit,
    onOpenYouTube: (EducationalVideo) -> Unit,
    onDownloadClick: (EducationalVideo) -> Unit,
    onPauseClick: (String) -> Unit,
    onPlayOfflineClick: (EducationalVideo) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "२२. भिडियोबाट सिक्नुहोस्",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )

        // Topic Playlists Horizontal Selector (8 Topics)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTopicId == null,
                onClick = { onSelectTopic(null) },
                label = { Text("सबै विषय") },
                shape = RoundedCornerShape(50),
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ForestGreen,
                    selectedLabelColor = NaturalIvory,
                    containerColor = SoftSage,
                    labelColor = DeepCharcoal
                )
            )
            VideoTopic.entries.forEach { topic ->
                FilterChip(
                    selected = selectedTopicId == topic.id,
                    onClick = { onSelectTopic(topic.id) },
                    label = { Text(topic.titleNe) },
                    shape = RoundedCornerShape(50),
                    border = null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreen,
                        selectedLabelColor = NaturalIvory,
                        containerColor = SoftSage,
                        labelColor = DeepCharcoal
                    )
                )
            }
        }

        launchErrorMessage?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSage)
            ) {
                Text(
                    text = msg,
                    color = ErrorMaroon,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (videos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (selectedTopicId != null) {
                        "यो विषयको लागि हाल प्रमाणित भिडियो उपलब्ध छैन।"
                    } else {
                        "यस बालीका शैक्षिक भिडियोहरू हाल उपलब्ध छैनन्।"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGreenGrey,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            videos.forEach { video ->
                EducationalVideoCard(
                    video = video,
                    downloadState = downloadStates[video.id] ?: VideoDownloadState.Idle,
                    onOpenYouTube = { onOpenYouTube(video) },
                    onDownloadClick = { onDownloadClick(video) },
                    onPauseClick = { onPauseClick(video.id) },
                    onPlayOfflineClick = { onPlayOfflineClick(video) },
                    onDeleteClick = { onDeleteClick(video.id) }
                )
            }
        }
    }
}

@Composable
private fun EducationalVideoCard(
    video: EducationalVideo,
    downloadState: VideoDownloadState,
    onOpenYouTube: () -> Unit,
    onDownloadClick: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayOfflineClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val topicLabel = VideoTopic.fromId(video.topicId)?.titleNe ?: "शैक्षिक भिडियो"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CropThumbnailImage(
                    imageUrl = video.thumbnailUrl,
                    contentDescription = video.titleNe,
                    modifier = Modifier.size(width = 110.dp, height = 74.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topicLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = video.titleNe,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = video.channelName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                    video.duration?.let { dur ->
                        Text(
                            text = "अवधि: $dur",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGreenGrey
                        )
                    }
                }
            }

            // Provider distinction label
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val badgeText = when {
                    video.downloadPermission && video.isDownloaded -> "अफलाइन उपलब्ध (${ValidationUtils.formatFileSize(video.fileSizeBytes)})"
                    video.downloadPermission -> "अनुमतिप्राप्त अफलाइन डाउनलोड"
                    else -> "युट्युब (अन्लाइन मात्र)"
                }
                val badgeColor = when {
                    video.downloadPermission && video.isDownloaded -> SuccessGreen
                    video.downloadPermission -> ForestGreen
                    else -> WarningOrange
                }
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
                if (video.isSampleMedia) {
                    Text(
                        text = "• विकास परीक्षण नमुना",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarningOrange
                    )
                }
            }

            Text(
                text = video.descriptionNe,
                style = MaterialTheme.typography.bodySmall,
                color = DeepCharcoal
            )

            // Action area
            if (!video.downloadPermission) {
                // YouTube Video - Online playback via genuine YouTube app/browser
                Button(
                    onClick = onOpenYouTube,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = NaturalIvory
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("युट्युबमा हेर्नुहोस्", fontWeight = FontWeight.Bold)
                }
            } else {
                // Authorized Downloadable Educational Video
                when {
                    video.isDownloaded || downloadState is VideoDownloadState.Completed -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onPlayOfflineClick,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestGreen,
                                    contentColor = NaturalIvory
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DownloadDone,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("अफलाइन हेर्नुहोस्", fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "हटाउनुहोस्",
                                    tint = ErrorMaroon
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("हटाउनुहोस्", color = ErrorMaroon, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    downloadState is VideoDownloadState.Downloading -> {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            LinearProgressIndicator(
                                progress = { downloadState.progress },
                                color = ForestGreen,
                                trackColor = SoftSage,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val progressText = if (downloadState.totalBytes > 0) {
                                    "${ValidationUtils.formatFileSize(downloadState.downloadedBytes)} / ${ValidationUtils.formatFileSize(downloadState.totalBytes)}"
                                } else {
                                    "डाउनलोड हुँदैछ: ${ValidationUtils.formatFileSize(downloadState.downloadedBytes)}"
                                }
                                Text(
                                    text = progressText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DeepCharcoal
                                )
                                TextButton(onClick = onPauseClick) {
                                    Icon(
                                        imageVector = Icons.Outlined.Pause,
                                        contentDescription = "रोक्नुहोस्",
                                        tint = ForestGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("रोक्नुहोस्", color = ForestGreen)
                                }
                            }
                        }
                    }

                    downloadState is VideoDownloadState.Paused -> {
                        Button(
                            onClick = onDownloadClick,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = NaturalIvory
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("पुनः डाउनलोड सुरु गर्नुहोस्", fontWeight = FontWeight.Bold)
                        }
                    }

                    downloadState is VideoDownloadState.Failed -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = downloadState.errorMessageNe,
                                style = MaterialTheme.typography.bodySmall,
                                color = ErrorMaroon,
                                fontWeight = FontWeight.SemiBold
                            )
                            Button(
                                onClick = onDownloadClick,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestGreen,
                                    contentColor = NaturalIvory
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("पुनः प्रयास गर्नुहोस्", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> {
                        Button(
                            onClick = onDownloadClick,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = NaturalIvory
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("अफलाइन डाउनलोड गर्नुहोस्", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = DeepCharcoal
        )
    }
}
