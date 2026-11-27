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
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.data.remote.RemoteFarmerProfileSnapshot
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.RegistrationSyncStatus
import com.krishisaathi.app.ui.components.KrishiPullToRefreshBox
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen
import com.krishisaathi.app.ui.theme.WarningOrange
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Farmer Profile ("प्रोफाइल") screen displaying actual registered farmer data from local/Supabase storage,
 * along with explicit synchronization state (SYNCED vs PENDING/FAILED) and safe manual retry.
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
    var isRetryingSync by remember { mutableStateOf(false) }
    var isPullRefreshing by remember { mutableStateOf(false) }
    var syncRetryFeedback by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(localRegistration?.phoneNumber, localRegistration?.syncStatus) {
        val phone = localRegistration?.phoneNumber
        if (!phone.isNullOrBlank()) {
            remoteSnapshot = appContainer.remoteContentSyncClient.fetchFarmerProfileByPhone(phone)
        }
    }

    val displayName = remoteSnapshot?.fullName ?: localRegistration?.fullName
    val displayPhone = remoteSnapshot?.phoneNumber ?: localRegistration?.phoneNumber
    val displayMunicipality = remoteSnapshot?.municipality ?: localRegistration?.municipality
    val displayWard = remoteSnapshot?.wardNumber ?: localRegistration?.wardNumber
    val displayTole = (
        remoteSnapshot?.toleVillage
            ?: localRegistration?.toleVillage?.takeIf { it.isNotBlank() }
            ?: storedTole.takeIf { it.isNotBlank() }
        )
    val syncStatus = if (remoteSnapshot != null) {
        RegistrationSyncStatus.SYNCED
    } else {
        localRegistration?.syncStatus ?: RegistrationSyncStatus.PENDING
    }

    KrishiPullToRefreshBox(
        isRefreshing = isPullRefreshing,
        onRefresh = {
            scope.launch {
                isPullRefreshing = true
                try {
                    appContainer.appSyncController.refreshNow(force = true)
                    val phone = localRegistration?.phoneNumber
                    if (!phone.isNullOrBlank()) {
                        remoteSnapshot =
                            appContainer.remoteContentSyncClient.fetchFarmerProfileByPhone(phone)
                    }
                } finally {
                    isPullRefreshing = false
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            // Honest Sync Status Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSage),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (syncStatus) {
                                RegistrationSyncStatus.SYNCED -> Icons.Outlined.CloudDone
                                else -> Icons.Outlined.CloudOff
                            },
                            contentDescription = null,
                            tint = if (syncStatus == RegistrationSyncStatus.SYNCED) SuccessGreen else WarningOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (syncStatus) {
                                RegistrationSyncStatus.SYNCED -> "Supabase सर्भरमा सुरक्षित सिंक भएको छ।"
                                RegistrationSyncStatus.PENDING -> "अफलाइन सुरक्षित — सर्भरमा सिंक हुन बाँकी छ।"
                                RegistrationSyncStatus.FAILED -> "सर्भर सिंक असफल भयो — कृपया पुनः प्रयास गर्नुहोस्।"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal
                        )
                    }

                    if (syncStatus != RegistrationSyncStatus.SYNCED) {
                        Button(
                            onClick = {
                                if (!isRetryingSync) {
                                    isRetryingSync = true
                                    syncRetryFeedback = null
                                    scope.launch {
                                        val res = appContainer.bakaiyaCropRepository.retryPendingRegistrationSync()
                                        isRetryingSync = false
                                        syncRetryFeedback = if (res.isSuccess) {
                                            "सिंक सफल भयो।"
                                        } else {
                                            "अहिले इन्टरनेट/सर्भर उपलब्ध छैन; पछि स्वतः सिंक हुनेछ।"
                                        }
                                    }
                                }
                            },
                            enabled = !isRetryingSync,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = NaturalIvory
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (isRetryingSync) {
                                CircularProgressIndicator(
                                    color = NaturalIvory,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("अहिले सिंक गर्नुहोस्", fontWeight = FontWeight.SemiBold)
                        }
                        syncRetryFeedback?.let { msg ->
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGreenGrey
                            )
                        }
                    }
                }
            }

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
                        text = "दर्ता विवरण",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )

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
                    ProfileFieldRow(
                        label = "सहमति स्थिति",
                        value = if (localRegistration?.consentStatus == true) "स्वीकृत" else "दर्ता भएको"
                    )
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
