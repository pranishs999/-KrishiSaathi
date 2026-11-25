package com.krishisaathi.app.ui.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.krishisaathi.app.core.util.ValidationUtils
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.ErrorMaroon
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.PlaceholderText
import com.krishisaathi.app.ui.theme.SoftSage

/**
 * Optional minimal farmer registration dialog (Section 11).
 * Collects ONLY:
 * - किसानको नाम
 * - वडा नम्बर (१-१२)
 * - मोबाइल नम्बर
 * Strictly NO OTP, NO passwords, NO MPIN, NO login requirement.
 */
@Composable
fun RegistrationDialog(
    currentName: String,
    currentWard: Int,
    currentPhone: String,
    isExistingProfile: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (name: String, ward: Int, phone: String) -> Unit,
    onClearProfile: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(currentName) }
    var wardText by remember { mutableStateOf(if (currentWard in 1..12) currentWard.toString() else "1") }
    var phone by remember { mutableStateOf(currentPhone) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "ऐच्छिक किसान विवरण",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )

                Text(
                    text = "यो दर्ता पूर्णतया ऐच्छिक हो। दर्ता नगरी पनि सबै कृषि जानकारी निःशुल्क हेर्न सकिन्छ।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGreenGrey
                )

                errorMessage?.let { err ->
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ErrorMaroon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftSage, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("किसानको नाम", color = DeepCharcoal) },
                    placeholder = { Text("पूरा नाम लेख्नुहोस्", color = PlaceholderText) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftSage,
                        unfocusedContainerColor = SoftSage,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = SoftSage,
                        focusedTextColor = DeepCharcoal,
                        unfocusedTextColor = DeepCharcoal,
                        cursorColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = wardText,
                    onValueChange = { input ->
                        if (input.length <= 2 && input.all { it.isDigit() }) {
                            wardText = input
                            errorMessage = null
                        }
                    },
                    label = { Text("बकैया वडा नम्बर (१ - १२)", color = DeepCharcoal) },
                    placeholder = { Text("उदा: ३", color = PlaceholderText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftSage,
                        unfocusedContainerColor = SoftSage,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = SoftSage,
                        focusedTextColor = DeepCharcoal,
                        unfocusedTextColor = DeepCharcoal,
                        cursorColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { input ->
                        if (input.length <= 10 && input.all { it.isDigit() }) {
                            phone = input
                            errorMessage = null
                        }
                    },
                    label = { Text("मोबाइल नम्बर", color = DeepCharcoal) },
                    placeholder = { Text("९८XXXXXXXX", color = PlaceholderText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SoftSage,
                        unfocusedContainerColor = SoftSage,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = SoftSage,
                        focusedTextColor = DeepCharcoal,
                        unfocusedTextColor = DeepCharcoal,
                        cursorColor = ForestGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val trimmedName = name.trim()
                        val ward = wardText.toIntOrNull()
                        val trimmedPhone = phone.trim()

                        when {
                            trimmedName.isEmpty() -> {
                                errorMessage = "कृपया किसानको नाम लेख्नुहोस्।"
                            }
                            ward == null || ward !in 1..12 -> {
                                errorMessage = "बकैया गाउँपालिकाको वडा नम्बर १ देखि १२ भित्र हुनुपर्छ।"
                            }
                            !ValidationUtils.isValidNepalPhoneNumber(trimmedPhone) -> {
                                errorMessage = "कृपया १० अंकको मान्य नेपाली मोबाइल नम्बर लेख्नुहोस्।"
                            }
                            else -> onConfirm(trimmedName, ward, trimmedPhone)
                        }
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
                    Text("सुरक्षित गर्नुहोस्", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isExistingProfile && onClearProfile != null) {
                        Arrangement.SpaceBetween
                    } else {
                        Arrangement.End
                    }
                ) {
                    if (isExistingProfile && onClearProfile != null) {
                        TextButton(onClick = onClearProfile) {
                            Text("विवरण हटाउनुहोस्", color = ErrorMaroon, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    TextButton(onClick = onDismiss) {
                        Text("बन्द गर्नुहोस्", color = DeepCharcoal, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
