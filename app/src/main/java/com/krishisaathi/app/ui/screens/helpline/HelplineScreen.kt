package com.krishisaathi.app.ui.screens.helpline

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.HelplineContact
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen

@Composable
fun HelplineScreen(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val contacts by appContainer.bakaiyaCropRepository
        .getHelplineContacts()
        .collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        appContainer.bakaiyaCropRepository.ensureContentSeeded()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.SupportAgent,
                contentDescription = null,
                tint = ForestGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "किसान हेल्पलाइन",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Text(
                    text = "बकैया गाउँपालिका तथा मकवानपुरका प्रमाणित कृषि सम्पर्क",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftSage),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "निःशुल्क कृषि तथा पशु प्राविधिक सल्लाहका लागि प्रमाणित आधिकारिक नम्बरमा फोन गर्नुहोस्।",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = ForestGreen,
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (contacts.isEmpty()) {
            Text(
                text = "सम्पर्क विवरण लोड हुँदैछ...",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreenGrey
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(contacts, key = { it.id }) { contact ->
                    HelplineContactCard(contact = contact)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun HelplineContactCard(
    contact: HelplineContact,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.nameNe,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (contact.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = "प्रमाणित सम्पर्क",
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = contact.designationNe,
                style = MaterialTheme.typography.bodySmall,
                color = ForestGreen,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = contact.wardNe,
                style = MaterialTheme.typography.bodySmall,
                color = MutedGreenGrey
            )
            contact.email?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGreenGrey
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val dialNumber = if (contact.mobileNumber.startsWith("0")) {
                        contact.mobileNumber
                    } else {
                        "+977${contact.mobileNumber}"
                    }
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$dialNumber")
                    }
                    context.startActivity(intent)
                },
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
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "फोन गर्नुहोस् (${contact.mobileNumber})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
