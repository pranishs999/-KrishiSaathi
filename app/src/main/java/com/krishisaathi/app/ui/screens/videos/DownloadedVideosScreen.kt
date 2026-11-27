package com.krishisaathi.app.ui.screens.videos

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.ui.components.KrishiPullToRefreshBox
import com.krishisaathi.app.ui.screens.home.CropThumbnailImage
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ErrorMaroon
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedVideosScreen(
    viewModel: DownloadedVideosViewModel,
    onBackClick: () -> Unit,
    onPlayVideo: (String) -> Unit,
    onBrowseCropsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var pendingDeleteItem by remember { mutableStateOf<DownloadedVideoItem?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NaturalIvory,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "डाउनलोड गरिएका भिडियोहरू",
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
        KrishiPullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ForestGreen)
                }
            } else if (state.videos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DownloadDone,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "कुनै अफलाइन भिडियो डाउनलोड गरिएको छैन।",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                        Text(
                            text = "बालीको विवरण पृष्ठमा गई अनुमतिप्राप्त शैक्षिक भिडियो डाउनलोड गरी विना इन्टरनेट हेर्न सक्नुहुन्छ।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGreenGrey
                        )
                        Button(
                            onClick = onBrowseCropsClick,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = NaturalIvory
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("बालीहरू हेर्नुहोस्", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.videos, key = { it.video.id }) { item ->
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
                                    imageUrl = item.video.thumbnailUrl,
                                    contentDescription = item.video.titleNe,
                                    modifier = Modifier.size(width = 104.dp, height = 70.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.cropNameNe,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                    Text(
                                        text = item.video.titleNe,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepCharcoal
                                    )
                                    Text(
                                        text = "आकार: ${ValidationUtils.formatFileSize(item.video.fileSizeBytes)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onPlayVideo(item.video.id) },
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
                                        imageVector = Icons.Outlined.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("चलाउनुहोस्", fontWeight = FontWeight.Bold)
                                }
                                TextButton(
                                    onClick = { pendingDeleteItem = item },
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
                    }
                }
            }
            }
        }
    }

    pendingDeleteItem?.let { target ->
        AlertDialog(
            onDismissRequest = { pendingDeleteItem = null },
            containerColor = LightGreen,
            title = {
                Text(
                    text = "भिडियो हटाउने?",
                    color = DeepCharcoal,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "'${target.video.titleNe}' फोनको भण्डारणबाट हटाउन चाहनुहुन्छ?",
                    color = DeepCharcoal
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteVideo(target.video.id)
                        pendingDeleteItem = null
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorMaroon,
                        contentColor = NaturalIvory
                    )
                ) {
                    Text("हटाउनुहोस्")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteItem = null }) {
                    Text("रद्द गर्नुहोस्", color = DeepCharcoal)
                }
            }
        )
    }
}
