package com.krishisaathi.app.ui.screens.livestock

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Verified
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LeafGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen
import com.krishisaathi.app.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    viewModel: AnimalDetailViewModel,
    onBackClick: () -> Unit,
    onPlayLocalVideo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = NaturalIvory,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = state.animal?.nameNe ?: "पशुपालन विवरण",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "पछाडि",
                                tint = ForestGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NaturalIvory,
                        titleContentColor = ForestGreen
                    )
                )
                HorizontalDivider(color = DividerSage, thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        val animal = state.animal
        if (animal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.isLoading) "लोड हुँदैछ..." else "विवरण फेला परेन।",
                    color = MutedGreenGrey
                )
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                if (animal.thumbnailUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(24.dp))
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
                                    modifier = Modifier.size(48.dp),
                                    tint = LeafGreen
                                )
                            },
                            error = {
                                Icon(
                                    imageVector = Icons.Outlined.BrokenImage,
                                    contentDescription = "तस्बिर लोड हुन सकेन",
                                    modifier = Modifier.size(36.dp),
                                    tint = MutedGreenGrey
                                )
                            }
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${animal.nameNe} (${animal.nameEn})",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (animal.isVerifiedContent) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = "प्रमाणित सामग्री",
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    animal.scientificName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedGreenGrey
                        )
                    }
                }

                // Mandatory Veterinary Safety Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SoftSage),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalHospital,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = animal.safetyNoticeNe,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = DeepCharcoal
                        )
                    }
                }

                // Summary card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = animal.summaryNe,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeepCharcoal
                        )
                        animal.purposeTagsNe?.let { SectionBlock("मुख्य प्रयोजन", it) }
                        animal.bakaiyaRelevanceNe?.let { SectionBlock("बकैया क्षेत्रमा उपयोगिता", it) }
                        animal.climateSuitabilityNe?.let { SectionBlock("हावापानी अनुकूलता", it) }
                        animal.altitudeRangeNe?.let { SectionBlock("उचाइ उपयुक्तता", it) }
                        animal.recommendedBreedsSummaryNe?.let { SectionBlock("सिफारिस गरिएका नस्लहरू", it) }
                    }
                }

                // Image Gallery Strip
                if (state.images.isNotEmpty()) {
                    Text(
                        text = "तस्बिर ग्यालरी",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.images, key = { it.id }) { img ->
                            Card(
                                modifier = Modifier.width(220.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = LightGreen),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column {
                                    SubcomposeAsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(img.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = img.captionNe,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                    )
                                    Text(
                                        text = img.captionNe,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DeepCharcoal,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Breeds detail
                if (state.breeds.isNotEmpty()) {
                    Text(
                        text = "नस्ल विवरण",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    state.breeds.forEach { breed ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LightGreen),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = breed.breedNameNe,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                                breed.breedNameEn?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall, color = MutedGreenGrey)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(breed.characteristicsNe, style = MaterialTheme.typography.bodySmall, color = DeepCharcoal)
                                Spacer(Modifier.height(4.dp))
                                Text("प्रयोजन: ${breed.purposeNe}", style = MaterialTheme.typography.bodySmall, color = ForestGreen)
                            }
                        }
                    }
                }

                // Housing
                state.housing?.let { h ->
                    GuidanceCard(title = "गोठ / खोर व्यवस्थापन") {
                        SectionBlock("संरचना", h.shedDesignNe)
                        h.floorSpaceStandardNe?.let { SectionBlock("ठाउँ व्यवस्थापन", it) }
                        SectionBlock("हावा र उज्यालो", h.ventilationLightNe)
                        SectionBlock("सोत्तर र निकास", h.beddingDrainageNe)
                        SectionBlock("सरसफाइ", h.sanitationRoutineNe)
                        SectionBlock("जैविक सुरक्षा", h.biosecurityNotesNe)
                    }
                }

                // Feeding
                state.feeding?.let { f ->
                    GuidanceCard(title = "दाना, घाँस र पानी") {
                        SectionBlock("हरियो घाँस", f.greenFodderForageNe)
                        SectionBlock("सुक्खा घाँस", f.dryRoughageNe)
                        f.concentrateFeedNe?.let { SectionBlock("दाना", it) }
                        f.mineralSaltNe?.let { SectionBlock("खनिज र नुन", it) }
                        SectionBlock("सफा पानी", f.cleanWaterNe)
                        f.rationQuantityNoteNe?.let { SectionBlock("मात्रा सम्बन्धी सावधानी", it) }
                        SectionBlock("खुवाउने समय", f.feedingFrequencyNe)
                    }
                }

                // Health
                state.health?.let { h ->
                    GuidanceCard(title = "स्वास्थ्य र खोप सचेतना") {
                        SectionBlock("दैनिक सरसफाइ", h.dailyHygieneNe)
                        SectionBlock("अलग राख्ने (Isolation)", h.biosecurityIsolationNe)
                        SectionBlock("खोप सचेतना", h.vaccinationAwarenessNe)
                        SectionBlock("जुका तथा परजीवी नियन्त्रण", h.dewormingParasiteControlNe)
                        SectionBlock("चेतावनी लक्षण", h.warningSignsNe)
                        SectionBlock("पशु चिकित्सक परामर्श", h.vetReferralGuidanceNe)
                    }
                }

                // Diseases
                if (state.diseases.isNotEmpty()) {
                    Text(
                        text = "प्रमुख रोगहरू",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    state.diseases.forEach { d ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LightGreen),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(d.nameNe, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ForestGreen)
                                d.nameEn?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MutedGreenGrey) }
                                Text("लक्षण: ${d.symptomsNe}", style = MaterialTheme.typography.bodySmall, color = DeepCharcoal)
                                Text("रोकथाम: ${d.preventionBiosecurityNe}", style = MaterialTheme.typography.bodySmall, color = DeepCharcoal)
                                Text("तत्काल कदम: ${d.immediateFarmerActionNe}", style = MaterialTheme.typography.bodySmall, color = DeepCharcoal)
                                Text(
                                    text = "सावधानी: ${d.veterinaryCautionNe}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = WarningOrange
                                )
                            }
                        }
                    }
                }

                // Source reference
                if (animal.sourceReference.isNotBlank()) {
                    HorizontalDivider(color = DividerSage)
                    Text(
                        text = "स्रोत: ${animal.sourceReference}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GuidanceCard(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = ForestGreen
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SectionBlock(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = DeepCharcoal
        )
    }
}
