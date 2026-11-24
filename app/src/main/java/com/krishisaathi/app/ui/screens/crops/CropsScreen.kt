package com.krishisaathi.app.ui.screens.crops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CropPreviewItem(
    val nameNe: String,
    val nameEn: String,
    val category: String,
    val region: String
)

@Composable
fun CropsScreen(
    modifier: Modifier = Modifier
) {
    val sampleCrops = listOf(
        CropPreviewItem("धान (Paddy)", "Oryza sativa", "खाद्यान्न बाली", "तराई तथा भित्री मधेस (हेटौंडा/मनहरी)"),
        CropPreviewItem("मकै (Maize)", "Zea mays", "खाद्यान्न बाली", "मध्य पहाड तथा बेंसी (मकवानपुरगढी/थाहा)"),
        CropPreviewItem("काउली (Cauliflower)", "Brassica oleracea var. botrytis", "मौसमी तरकारी", "थाहा नगरपालिका / पालुङ"),
        CropPreviewItem("बन्दागोभी (Cabbage)", "Brassica oleracea var. capitata", "मौसमी तरकारी", "दामन / थाहा क्षेत्र"),
        CropPreviewItem("बेमौसमी गोलभेंडा (Tomato)", "Solanum lycopersicum", "नगदे तरकारी", "मकवानपुरगढी तथा हेटौंडा"),
        CropPreviewItem("तोरी (Mustard)", "Brassica campestris", "तेलहन बाली", "बकैया तथा मनहरी फाँट"),
        CropPreviewItem("अदुवा (Ginger)", "Zingiber officinale", "मसला बाली", "भीमफेदी तथा पहाडी भूभाग")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "बाली निर्देशिका (Crops)",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "मकवानपुरमा लगाइने प्रमुख बालीहरूको सूची",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleCrops) { crop ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = crop.nameNe,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = crop.nameEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "वर्ग: ${crop.category}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "उपयुक्त क्षेत्र: ${crop.region}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

