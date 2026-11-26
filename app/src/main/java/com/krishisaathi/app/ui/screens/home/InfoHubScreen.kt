package com.krishisaathi.app.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (sectionType) {
                "documents" -> {
                    InfoItemCard(
                        title = "नेपाल कृषि अनुसन्धान परिषद् (NARC)",
                        body = "बाली जात सिफारिस, धान/मकै/तरकारी खेती प्रविधि तथा बाली संरक्षण मार्गनिर्देशन (narc.gov.np) यस एपमा समावेश छ।"
                    )
                    InfoItemCard(
                        title = "कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal)",
                        body = "कृषि तथा पशुपन्छी विकास मन्त्रालय अन्तर्गत आधिकारिक कृषि डायरी र किसान शैक्षिक सामग्री।"
                    )
                    InfoItemCard(
                        title = "पशु सेवा विभाग (DLS) तथा भेटेरिनरी अस्पताल मकवानपुर",
                        body = "पशुपालन खोर, दाना व्यवस्थापन र पशु रोग सचेतना मार्गनिर्देशनको आधिकारिक स्रोत।"
                    )
                }

                "subsidies" -> {
                    InfoItemCard(
                        title = "बकैया गाउँपालिका कृषि तथा पशु सेवा शाखा",
                        body = "गाउँपालिकास्तरीय बीउ वितरण, अनुदान कार्यक्रम, माटो परीक्षण तथा पशु खोप अभियानका अद्यावधिक विवरणका लागि बकैया कृषि शाखामा सिधा सम्पर्क गर्नुहोस्।"
                    )
                    InfoItemCard(
                        title = "आधिकारिक पोर्टल तथा कार्यालय",
                        body = "कार्यालय: बकैया-३, हात्तीसुँडे, मकवानपुर (bakaiyakrishi.gov.np)। कृषि प्राविधिक सेवाका लागि किसान हेल्पलाइन प्रयोग गर्नुहोस्।"
                    )
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

                else -> {
                    InfoItemCard(
                        title = "मौसमी खेती समय सल्लाह (NARC आधारित)",
                        body = "बकैया गाउँपालिकाको उचाइ र हावापानी अनुसार प्रमुख बाली लगाउने समय जानकारी तल हेर्नुहोस्।"
                    )
                    crops.filter { !it.plantingSeason.isNullOrBlank() }.take(6).forEach { crop ->
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftSage),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "थप गाउँपालिका सूचना र अद्यावधिक समाचारका लागि बकैया कृषि शाखामा सम्पर्क गर्नुहोस्।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedGreenGrey,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
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
