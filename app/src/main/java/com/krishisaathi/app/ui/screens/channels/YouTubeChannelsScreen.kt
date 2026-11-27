package com.krishisaathi.app.ui.screens.channels

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.media.YouTubeLaunchResult
import com.krishisaathi.app.media.YouTubeLauncher
import com.krishisaathi.app.ui.components.KrishiPullToRefreshBox
import com.krishisaathi.app.ui.screens.home.CropThumbnailImage
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ErrorMaroon
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

/**
 * कृषि भिडियो तथा च्यानल सामग्री — Driven strictly by Supabase `educational_videos`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeChannelsScreen(
    appContainer: AppContainer,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val videos by appContainer.bakaiyaCropRepository.getAllVideos().collectAsState(initial = emptyList())
    val crops by appContainer.bakaiyaCropRepository.getCrops().collectAsState(initial = emptyList())
    val cropNameMap = remember(crops) { crops.associate { it.id to it.nameNe } }
    var isRefreshing by remember { mutableStateOf(false) }
    var launchErrorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        appContainer.bakaiyaCropRepository.ensureContentSeeded()
    }

    val triggerRefresh: () -> Unit = {
        scope.launch {
            isRefreshing = true
            try {
                appContainer.appSyncController.refreshNow(force = true)
            } finally {
                isRefreshing = false
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "कृषि शैक्षिक भिडियोहरू",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "पछाडि फर्कनुहोस्",
                                tint = ForestGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NaturalIvory
                    )
                )
                HorizontalDivider(color = DividerSage, thickness = 1.dp)
            }
        },
        containerColor = NaturalIvory
    ) { innerPadding ->
        KrishiPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = triggerRefresh,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (videos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircle,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Supabase बाट शैक्षिक भिडियो उपलब्ध छैन।",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = triggerRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Supabase बाट सिंक गर्नुहोस्", color = NaturalIvory)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Supabase बाट सिंक गरिएका आधिकारिक कृषि भिडियो सामग्री (${videos.size})। हेर्न कार्ड थिच्नुहोस्।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeepCharcoal
                        )
                    }

                    launchErrorMessage?.let { errorMsg ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SoftSage),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = errorMsg,
                                    color = ErrorMaroon,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    items(videos, key = { it.id }) { video ->
                        RemoteVideoCard(
                            video = video,
                            targetLabel = cropNameMap[video.cropId] ?: "कृषि प्रविधि",
                            onClick = {
                                when (val result = YouTubeLauncher.openYouTubeVideo(context, video.videoUrl)) {
                                    is YouTubeLaunchResult.Launched -> launchErrorMessage = null
                                    is YouTubeLaunchResult.Error -> launchErrorMessage = result.messageNe
                                }
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RemoteVideoCard(
    video: EducationalVideo,
    targetLabel: String,
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
                imageUrl = video.thumbnailUrl,
                contentDescription = video.titleNe,
                fallbackIcon = Icons.Outlined.PlayCircle,
                modifier = Modifier.size(width = 88.dp, height = 64.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = video.titleNe,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = "प्रमाणित",
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "${video.channelName} • $targetLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForestGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = video.descriptionNe,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SoftSage),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = "भिडियो खोल्नुहोस्",
                    tint = ForestGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
