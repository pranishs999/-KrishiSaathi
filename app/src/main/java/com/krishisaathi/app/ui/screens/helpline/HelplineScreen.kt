package com.krishisaathi.app.ui.screens.helpline

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.domain.model.HelplineContact
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ErrorMaroon
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen

@Composable
fun HelplineScreen(
    viewModel: HelplineViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "किसान हेल्पलाइन",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Text(
                text = "बकैया गाउँपालिका आधिकारिक कृषि तथा कार्यालय सम्पर्क विवरण",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreenGrey
            )
        }

        // Ward Filter Chips (All, Central Office, Wards 1-12)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HelplineWardChip(
                label = "सबै सम्पर्क",
                selected = state.selectedWardFilter == null,
                onClick = { viewModel.selectWard(null) }
            )
            HelplineWardChip(
                label = "गाउँपालिका कृषि शाखा",
                selected = state.selectedWardFilter == 0,
                onClick = { viewModel.selectWard(0) }
            )
            (1..12).forEach { ward ->
                HelplineWardChip(
                    label = "वडा नं. $ward",
                    selected = state.selectedWardFilter == ward,
                    onClick = { viewModel.selectWard(ward) }
                )
            }
        }

        state.dialErrorMessage?.let { errorMsg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSage)
            ) {
                Text(
                    text = errorMsg,
                    color = ErrorMaroon,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForestGreen)
            }
        } else if (state.contacts.isEmpty()) {
            // Useful Empty State (Section 10)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhoneInTalk,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "यस वडाको छुट्टै प्राविधिक नम्बर प्रमाणित सूचीमा उपलब्ध छैन।",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                        Text(
                            text = "तपाईंले बकैया गाउँपालिकाको मुख्य कृषि तथा आर्थिक विकास शाखामा सिधै सम्पर्क गरी सल्लाह लिन सक्नुहुन्छ।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGreenGrey
                        )
                        Button(
                            onClick = { viewModel.selectWard(null) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = NaturalIvory
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("मुख्य कार्यालय सम्पर्क हेर्नुहोस्", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.contacts, key = { it.id }) { contact ->
                    HelplineContactCard(
                        contact = contact,
                        onDialClick = {
                            launchDialer(context, contact.mobileNumber) { err ->
                                viewModel.setDialError(err)
                            }
                        }
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftSage),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "स्रोत: बकैया गाउँपालिका कृषि पोर्टल (bakaiyakrishi.gov.np) बाट प्रमाणित आधिकारिक सम्पर्क विवरण।",
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
private fun HelplineWardChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = RoundedCornerShape(50),
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ForestGreen,
            selectedLabelColor = NaturalIvory,
            containerColor = SoftSage,
            labelColor = DeepCharcoal
        )
    )
}

@Composable
private fun HelplineContactCard(
    contact: HelplineContact,
    onDialClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = contact.nameNe,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal,
                    modifier = Modifier.weight(1f)
                )
                if (contact.isVerified) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = "प्रमाणित सम्पर्क",
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = contact.designationNe,
                style = MaterialTheme.typography.titleSmall,
                color = ForestGreen,
                fontWeight = FontWeight.SemiBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MutedGreenGrey,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = contact.wardNe,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey
                )
            }

            contact.email?.let { email ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = MutedGreenGrey,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Large tap-to-call button using ACTION_DIAL (Section 10)
            Button(
                onClick = onDialClick,
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
                    imageVector = Icons.Outlined.Call,
                    contentDescription = "डायल गर्नुहोस्",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "फोन डायल गर्नुहोस् (${contact.mobileNumber})",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun launchDialer(
    context: Context,
    rawNumber: String,
    onError: (String) -> Unit
) {
    if (!ValidationUtils.isValidNepalPhoneNumber(rawNumber)) {
        onError("अमान्य फोन नम्बर भएकोले डायल गर्न सकिएन।")
        return
    }
    val cleaned = rawNumber.replace(Regex("[\\s-]"), "")
    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleaned")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(dialIntent)
    } catch (_: ActivityNotFoundException) {
        onError("तपाईंको फोनमा कल डायल गर्ने एप उपलब्ध छैन।")
    } catch (_: Exception) {
        onError("फोन डायलर खोल्न समस्या आयो।")
    }
}
