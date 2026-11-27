package com.krishisaathi.app.ui.screens.crops

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.ui.components.KrishiPullToRefreshBox
import com.krishisaathi.app.ui.screens.home.CropThumbnailImage
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.PlaceholderText
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen

@Composable
fun CropsScreen(
    viewModel: CropsViewModel,
    onCropClick: (String) -> Unit,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
        Column {
            Text(
                text = "बालीहरू",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Text(
                text = "बाली र खेती निर्देशिका खोज्नुहोस् (अफलाइन उपलब्ध)",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreenGrey
            )
        }

        // Search Bar (Soft Sage background, Forest Green 2dp focused outline)
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            placeholder = { Text("नेपाली वा अंग्रेजी नाम खोज्नुहोस्...", color = PlaceholderText) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "खोज्नुहोस्", tint = DeepCharcoal) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Outlined.Clear, contentDescription = "खोज हटाउनुहोस्", tint = DeepCharcoal)
                    }
                }
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SoftSage,
                unfocusedContainerColor = SoftSage,
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = SoftSage,
                focusedTextColor = DeepCharcoal,
                unfocusedTextColor = DeepCharcoal,
                cursorColor = ForestGreen
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Category Visual Cards with Crop Images
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                val isSelected = state.selectedCategoryId == null
                Card(
                    onClick = { viewModel.onCategorySelected(null) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ForestGreen else SoftSage
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CropThumbnailImage(
                            imageUrl = state.categories.firstOrNull()?.thumbnailUrl,
                            contentDescription = "सबै बाली",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "सबै वर्ग",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NaturalIvory else DeepCharcoal
                        )
                    }
                }
            }

            items(state.categories, key = { it.id }) { category ->
                val isSelected = state.selectedCategoryId == category.id
                Card(
                    onClick = { viewModel.onCategorySelected(category.id) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ForestGreen else SoftSage
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .width(104.dp)
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CropThumbnailImage(
                            imageUrl = category.thumbnailUrl,
                            contentDescription = category.nameNe,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = category.nameNe,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) NaturalIvory else DeepCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Crops List / Loading / Empty State
        if (state.isLoading && state.crops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ForestGreen)
            }
        } else if (state.crops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "खोजिएको बाली भेटिएन।",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (state.searchQuery.isBlank()) {
                            "स्थानीय डेटा खाली छ। कृपया इन्टरनेट जडान गरी Supabase बाट सिंक गर्नुहोस्।"
                        } else {
                            "कृपया अर्को नाम वा वर्ग छनोट गरी पुनः प्रयास गर्नुहोस्।"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGreenGrey
                    )
                    if (state.searchQuery.isBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::refresh,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Supabase बाट सिंक गर्नुहोस्", color = NaturalIvory)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.crops, key = { it.id }) { crop ->
                    CropItemCard(
                        crop = crop,
                        onClick = { onCropClick(crop.id) }
                    )
                }
            }
        }
        }
    }
}

@Composable
fun CropItemCard(
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
                modifier = Modifier.size(86.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${crop.nameNe} (${crop.nameEn})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (crop.isVerifiedContent) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = "प्रमाणित स्रोत",
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = crop.descriptionNe,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                crop.plantingSeason?.let { season ->
                    Spacer(modifier = Modifier.height(6.dp))
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

                crop.altitude?.let { alt ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Terrain,
                            contentDescription = null,
                            tint = MutedGreenGrey,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = alt,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGreenGrey,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
