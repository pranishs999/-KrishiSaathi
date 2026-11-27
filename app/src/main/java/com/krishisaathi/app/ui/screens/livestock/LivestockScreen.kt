package com.krishisaathi.app.ui.screens.livestock

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
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Pets
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.ui.components.KrishiPullToRefreshBox
import com.krishisaathi.app.ui.screens.home.CropThumbnailImage
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LeafGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage

@Composable
fun LivestockScreen(
    viewModel: LivestockViewModel,
    onAnimalClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var activeCategoryId by rememberSaveable(state.selectedCategoryId) {
        mutableStateOf(state.selectedCategoryId)
    }

    val filteredAnimals = if (activeCategoryId == null) {
        state.animals
    } else {
        state.animals.filter { it.categoryId == activeCategoryId }
    }

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
                .padding(horizontal = 16.dp)
        ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "पशुपालन",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "बकैया गाउँपालिकाका लागि प्रमाणित पशुपालन तथा मत्स्यपालन जानकारी",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedGreenGrey
        )

        if (state.categories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    val isSelected = activeCategoryId == null
                    Card(
                        onClick = { activeCategoryId = null },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ForestGreen else SoftSage
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CropThumbnailImage(
                                imageUrl = state.categories.firstOrNull()?.thumbnailUrl,
                                contentDescription = "सबै पशुपालन",
                                fallbackIcon = Icons.Outlined.Pets,
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
                    val isSelected = activeCategoryId == category.id
                    Card(
                        onClick = { activeCategoryId = category.id },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ForestGreen else SoftSage
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .width(106.dp)
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CropThumbnailImage(
                                imageUrl = category.thumbnailUrl,
                                contentDescription = category.nameNe,
                                fallbackIcon = Icons.Outlined.Pets,
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredAnimals.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = LeafGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (state.isLoading) "पशुपालन जानकारी लोड हुँदैछ..." else "यस वर्गमा प्रकाशित पशु विवरण उपलब्ध छैन।",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedGreenGrey
                )
                if (!state.isLoading) {
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
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredAnimals, key = { it.id }) { animal ->
                    AnimalCard(
                        animal = animal,
                        onClick = { onAnimalClick(animal.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
        }
    }
}

@Composable
private fun AnimalCard(
    animal: Animal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SoftSage),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(animal.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = animal.nameNe,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Icon(
                            imageVector = Icons.Outlined.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(34.dp),
                            tint = LeafGreen
                        )
                    },
                    error = {
                        Icon(
                            imageVector = Icons.Outlined.BrokenImage,
                            contentDescription = "तस्बिर लोड हुन सकेन",
                            modifier = Modifier.size(28.dp),
                            tint = MutedGreenGrey
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${animal.nameNe} (${animal.nameEn})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                animal.scientificName?.let { sci ->
                    Text(
                        text = sci,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                }
                animal.purposeTagsNe?.let { tags ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tags,
                        style = MaterialTheme.typography.bodySmall,
                        color = ForestGreen,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
