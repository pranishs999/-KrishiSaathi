package com.krishisaathi.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.Animal
import com.krishisaathi.app.ui.components.KrishiPullToRefreshBox
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoHubScreen(
    sectionType: String,
    appContainer: AppContainer,
    onBackClick: () -> Unit,
    onOpenHelpline: () -> Unit,
    onOpenCropDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val crops by appContainer.bakaiyaCropRepository.getCrops().collectAsState(initial = emptyList())
    val contacts by appContainer.bakaiyaCropRepository.getHelplineContacts().collectAsState(initial = emptyList())
    val syncGeneration by appContainer.appSyncController.syncGeneration.collectAsState()
    var animals by remember { mutableStateOf<List<Animal>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(syncGeneration) {
        appContainer.bakaiyaCropRepository.ensureContentSeeded()
        animals = appContainer.bakaiyaCropRepository.getAllAnimals()
    }

    val refreshAction: () -> Unit = {
        scope.launch {
            isRefreshing = true
            try {
                appContainer.appSyncController.refreshNow(force = true)
                animals = appContainer.bakaiyaCropRepository.getAllAnimals()
            } finally {
                isRefreshing = false
            }
        }
    }

    val titleNe = when (sectionType) {
        "documents" -> "सरकारी कृषि दस्तावेज"
        "subsidies" -> "कृषि अनुदान तथा कार्यक्रम"
        else -> "सूचना तथा समाचार"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NaturalIvory,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = titleNe,
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NaturalIvory)
                )
                HorizontalDivider(color = DividerSage, thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        KrishiPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = refreshAction,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (sectionType) {
                    "documents" -> {
                        val cropSources = crops
                            .filter { it.sourceReference.isNotBlank() }
                            .groupBy { it.sourceReference }
                        val animalSources = animals
                            .filter { it.sourceReference.isNotBlank() }
                            .groupBy { it.sourceReference }

                        if (cropSources.isEmpty() && animalSources.isEmpty()) {
                            EmptySupabasePrompt(onSync = refreshAction)
                        } else {
                            cropSources.forEach { (source, linkedCrops) ->
                                InfoItemCard(
                                    title = source,
                                    body = "बालीहरू: ${linkedCrops.joinToString(", ") { it.nameNe }}"
                                )
                            }
                            animalSources.forEach { (source, linkedAnimals) ->
                                InfoItemCard(
                                    title = source,
                                    body = "पशुपालन: ${linkedAnimals.joinToString(", ") { it.nameNe }}"
                                )
                            }
                        }
                    }

                    "subsidies" -> {
                        if (contacts.isEmpty()) {
                            EmptySupabasePrompt(onSync = refreshAction)
                        } else {
                            contacts.forEach { contact ->
                                InfoItemCard(
                                    title = "${contact.nameNe} (${contact.wardNe})",
                                    body = "${contact.designationNe} • सम्पर्क: ${contact.mobileNumber}" +
                                        (contact.email?.let { " • $it" } ?: "")
                                )
                            }
                            Button(
                                onClick = onOpenHelpline,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestGreen,
                                    contentColor = NaturalIvory
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("किसान हेल्पलाइनमा सम्पर्क गर्नुहोस्", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> {
                        val seasonalCrops = crops.filter { !it.plantingSeason.isNullOrBlank() }
                        if (seasonalCrops.isEmpty()) {
                            EmptySupabasePrompt(onSync = refreshAction)
                        } else {
                            seasonalCrops.forEach { crop ->
                                Card(
                                    onClick = { onOpenCropDetail(crop.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "${crop.nameNe} (${crop.nameEn})",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = crop.plantingSeason ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = DeepCharcoal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySupabasePrompt(onSync: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Supabase बाट डेटा सिंक हुन बाँकी छ।",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreenGrey
            )
            Button(
                onClick = onSync,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Supabase बाट सिंक गर्नुहोस्", color = NaturalIvory)
            }
        }
    }
}

@Composable
private fun InfoItemCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = DeepCharcoal
            )
        }
    }
}
