package com.krishisaathi.app.ui.screens.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.data.local.prefs.DataStoreManager
import com.krishisaathi.app.domain.model.BakaiyaMunicipality
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DisabledGreen
import com.krishisaathi.app.ui.theme.ErrorMaroon
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.PlaceholderText
import com.krishisaathi.app.ui.theme.SoftSage

/**
 * Mandatory first-launch farmer registration screen (no OTP, password, MPIN, or permissions).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onRegistrationComplete: () -> Unit = {},
    dataStoreManager: DataStoreManager? = null,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var wardMenuExpanded by remember { mutableStateOf(false) }
    var toleVillage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            if (toleVillage.isNotBlank()) {
                dataStoreManager?.setToleVillage(toleVillage)
            }
            onRegistrationComplete()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = SoftSage,
        unfocusedContainerColor = SoftSage,
        disabledContainerColor = SoftSage,
        errorContainerColor = SoftSage,
        focusedBorderColor = ForestGreen,
        unfocusedBorderColor = SoftSage,
        disabledBorderColor = SoftSage,
        errorBorderColor = ErrorMaroon,
        focusedTextColor = DeepCharcoal,
        unfocusedTextColor = DeepCharcoal,
        disabledTextColor = DeepCharcoal,
        cursorColor = ForestGreen
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // App Identity & Purpose Header
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Agriculture,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "बकैया कृषि साथी",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }

            Text(
                text = "किसान दर्ता",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal
            )

            Text(
                text = "कृषि ज्ञान तथा जानकारी सेवा सुरु गर्न कृपया आफ्नो विवरण दर्ता गर्नुहोस्।",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedGreenGrey
            )
        }

        // Registration Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                state.submissionError?.let { err ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftSage, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = ErrorMaroon
                        )
                    }
                }

                // 1. Farmer Name
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = viewModel::onFullNameChanged,
                        label = { Text("किसानको नाम", color = DeepCharcoal) },
                        placeholder = {
                            Text("आफ्नो पूरा नाम लेख्नुहोस्।", color = PlaceholderText)
                        },
                        isError = state.nameError != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.nameError?.let { err ->
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorMaroon,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 2. Fixed Read-Only Municipality
                OutlinedTextField(
                    value = state.municipality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("गाउँपालिका", color = DeepCharcoal) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = ForestGreen
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Ward Number Dropdown (Wards 1-12)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = wardMenuExpanded,
                        onExpandedChange = { wardMenuExpanded = !wardMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.selectedWard?.let { "वडा नं. $it" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("वडा नम्बर", color = DeepCharcoal) },
                            placeholder = {
                                Text("वडा नम्बर छान्नुहोस्", color = PlaceholderText)
                            },
                            isError = state.wardError != null,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = wardMenuExpanded)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = fieldColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )

                        ExposedDropdownMenu(
                            expanded = wardMenuExpanded,
                            onDismissRequest = { wardMenuExpanded = false },
                            containerColor = NaturalIvory
                        ) {
                            BakaiyaMunicipality.WARDS.forEach { ward ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "वडा नं. $ward",
                                            color = DeepCharcoal,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    onClick = {
                                        viewModel.onWardSelected(ward)
                                        wardMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    state.wardError?.let { err ->
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorMaroon,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 4. Nepal Mobile Number (+977)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = state.phoneNumber,
                        onValueChange = viewModel::onPhoneNumberChanged,
                        label = { Text("मोबाइल नम्बर", color = DeepCharcoal) },
                        placeholder = { Text("98XXXXXXXX", color = PlaceholderText) },
                        prefix = {
                            Text(
                                text = "+977 ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        },
                        isError = state.phoneError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    state.phoneError?.let { err ->
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorMaroon,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 5. Optional Tole / Village for Farmer Profile
                OutlinedTextField(
                    value = toleVillage,
                    onValueChange = { toleVillage = it },
                    label = { Text("टोल / गाउँ (ऐच्छिक)", color = DeepCharcoal) },
                    placeholder = { Text("उदा: हात्तीसुँडे", color = PlaceholderText) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Registration Submit Button
                Button(
                    onClick = viewModel::submitRegistration,
                    enabled = !state.isSubmitting,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = NaturalIvory,
                        disabledContainerColor = DisabledGreen,
                        disabledContentColor = NaturalIvory
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            color = NaturalIvory,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text = "दर्ता गर्नुहोस्",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Privacy & Data Usage Notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftSage),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "तपाईंको नाम, वडा नम्बर र मोबाइल नम्बर बकैया गाउँपालिकाका कृषकहरूको पहिचान र कृषि सेवा जानकारी व्यवस्थित गर्न मात्र प्रयोग गरिन्छ। यो विवरण तपाईंको फोनमा सुरक्षित रहन्छ र अन्य कुनै अनुमति मागिँदैन।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey
                )
            }
        }
    }
}
