package com.krishisaathi.app.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.data.remote.RemoteFarmerProfileSnapshot
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Farmer Profile ("प्रोफाइल") screen displaying actual registered farmer data from local/Supabase storage.
 * Handles missing data gracefully without hardcoded sample records or unnecessary profile editing UI.
 */
@Composable
fun ProfileScreen(
    appContainer: AppContainer,
    onNavigateToRegistration: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val localRegistration by appContainer.bakaiyaCropRepository
        .getFarmerRegistration()
        .collectAsState(initial = null)
    val storedTole by appContainer.dataStoreManager.toleVillage.collectAsState(initial = "")
    var remoteSnapshot by remember { mutableStateOf<RemoteFarmerProfileSnapshot?>(null) }

    LaunchedEffect(localRegistration?.phoneNumber) {
        val phone = localRegistration?.phoneNumber
        if (!phone.isNullOrBlank()) {
            remoteSnapshot = appContainer.remoteContentSyncClient.fetchFarmerProfileByPhone(phone)
        }
    }

    val displayName = remoteSnapshot?.fullName ?: localRegistration?.fullName
    val displayPhone = remoteSnapshot?.phoneNumber ?: localRegistration?.phoneNumber
    val displayMunicipality = remoteSnapshot?.municipality ?: localRegistration?.municipality
    val displayWard = remoteSnapshot?.wardNumber ?: localRegistration?.wardNumber
    val displayTole = (remoteSnapshot?.toleVillage ?: storedTole).takeIf { !it.isNullOrBlank() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "प्रोफाइल",
                tint = ForestGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "किसान प्रोफाइल",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Text(
                    text = "दर्ता गरिएको व्यक्तिगत कृषि विवरण",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey
                )
            }
        }

        if (displayName == null && displayPhone == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "किसान विवरण दर्ता गरिएको छैन।",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "तपाईंको नाम, वडा, टोल/गाउँ र मोबाइल नम्बर यहाँ देखिन ऐच्छिक किसान दर्ता पूरा गर्नुहोस्।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                    Button(
                        onClick = onNavigateToRegistration,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestGreen,
                            contentColor = NaturalIvory
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("दर्ता गर्नुहोस्", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "दर्ता विवरण",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.weight(1f)
                        )
                        if (remoteSnapshot != null) {
                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = "Supabase प्रमाणित",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = DividerSage, thickness = 1.dp)

                    ProfileFieldRow(label = "पूरा नाम", value = displayName ?: "उपलब्ध छैन")
                    ProfileFieldRow(
                        label = "मोबाइल नम्बर",
                        value = displayPhone?.let { "+977 $it" } ?: "उपलब्ध छैन"
                    )
                    ProfileFieldRow(label = "पालिका", value = displayMunicipality ?: "उपलब्ध छैन")
                    ProfileFieldRow(
                        label = "वडा",
                        value = displayWard?.let { "वडा नं. $it" } ?: "उपलब्ध छैन"
                    )
                    ProfileFieldRow(label = "टोल / गाउँ", value = displayTole ?: "उपलब्ध छैन")
                    val registeredAtMs = localRegistration?.registeredAt ?: 0L
                    if (registeredAtMs > 0L) {
                        val formattedDate = remember(registeredAtMs) {
                            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(registeredAtMs))
                        }
                        ProfileFieldRow(label = "दर्ता मिति", value = formattedDate)
                    }
                }
            }
        }

        // Exact required status notice for additional features
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftSage),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Construction,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "थप सुविधाहरू निर्माण हुँदैछन्।",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            }
        }
    }
}

@Composable
private fun ProfileFieldRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MutedGreenGrey
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    }
}
