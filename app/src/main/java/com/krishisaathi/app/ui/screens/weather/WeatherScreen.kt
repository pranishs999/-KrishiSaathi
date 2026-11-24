package com.krishisaathi.app.ui.screens.weather

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "मौसम तथा कृषि पात्रो",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "मकवानपुर जिल्लाको मौसमी तथा बाली पात्रो",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Zone 1: Mid Hills
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "१. मध्य तथा उच्च पहाडी क्षेत्र (थाहा, दामन, भीमफेदी)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• आलु तथा बेमौसमी काउली/बन्दा रोप्ने समय: माघ - फागुन",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "• वर्षे मकै छर्ने उपयुक्त समय: चैत - वैशाख",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "• चिसो र तुषारोबाट जोगाउने स्याहार तालिका सक्रिय",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Zone 2: Valley Lowlands
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "२. भित्री मधेस तथा उपत्यका (हेटौंडा, मनहरी, बकैया)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• वर्षे धान रोपाईं तयारी: जेठ - असार",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "• चैते धान र हिउँदे मकै: फागुन - चैत",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "• तोरी तथा हिउँदे दलहन: असोज - कात्तिक",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

