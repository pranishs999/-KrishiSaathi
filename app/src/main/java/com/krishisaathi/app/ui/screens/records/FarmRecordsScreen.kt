package com.krishisaathi.app.ui.screens.records

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.krishisaathi.app.domain.model.FarmRecord
import com.krishisaathi.app.domain.model.FarmRecordCategories
import com.krishisaathi.app.domain.model.FarmRecordType
import com.krishisaathi.app.ui.theme.DeepCharcoal
import com.krishisaathi.app.ui.theme.DividerSage
import com.krishisaathi.app.ui.theme.ErrorMaroon
import com.krishisaathi.app.ui.theme.ForestGreen
import com.krishisaathi.app.ui.theme.LightGreen
import com.krishisaathi.app.ui.theme.MutedGreenGrey
import com.krishisaathi.app.ui.theme.NaturalIvory
import com.krishisaathi.app.ui.theme.SoftSage
import com.krishisaathi.app.ui.theme.SuccessGreen
import com.krishisaathi.app.ui.theme.WarningOrange
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FarmRecordsScreen(
    viewModel: FarmRecordsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var isGuideExpanded by remember { mutableStateOf(false) }
    var recordPendingDelete by remember { mutableStateOf<FarmRecord?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalIvory)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "फार्मको हिसाब",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "आफ्नो खेतीको आम्दानी र खर्चको सरल हिसाब अफलाइनमै राख्नुहोस्।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepCharcoal
                    )
                }
            }

            // Practical guide: फार्मको हिसाब कसरी राख्ने?
            item {
                FarmBookkeepingGuideCard(
                    expanded = isGuideExpanded,
                    onToggle = { isGuideExpanded = !isGuideExpanded }
                )
            }

            // Error notice if present
            state.errorMessageNe?.let { err ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftSage),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = err,
                            color = ErrorMaroon,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Summary row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryCard(
                        labelNe = "कुल आम्दानी",
                        amount = state.totalIncome,
                        icon = Icons.AutoMirrored.Outlined.TrendingUp,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        labelNe = "कुल खर्च",
                        amount = state.totalExpense,
                        icon = Icons.AutoMirrored.Outlined.TrendingDown,
                        color = WarningOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Net balance card
            item {
                val netBalance = state.totalIncome - state.totalExpense
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "बाँकी रकम (बचत/नोक्सान)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )
                        }
                        Text(
                            text = "रु. ${formatAmount(netBalance)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (netBalance >= 0) SuccessGreen else ErrorMaroon
                        )
                    }
                }
            }

            // Filter chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FarmRecordFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = state.selectedFilter == filter,
                            onClick = { viewModel.setFilter(filter) },
                            label = {
                                Text(
                                    text = filter.labelNe,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            border = null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = NaturalIvory,
                                containerColor = SoftSage,
                                labelColor = DeepCharcoal
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
            }

            if (state.records.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGreen),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "अहिलेसम्म कुनै हिसाब थपिएको छैन",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )
                            Text(
                                text = "तलको + बटन थिचेर बीउ, मल, ज्याला वा बाली बिक्रीको हिसाब थप्नुहोस्",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedGreenGrey
                            )
                        }
                    }
                }
            } else {
                items(state.records, key = { it.id }) { record ->
                    RecordCard(
                        record = record,
                        onDeleteRequest = { recordPendingDelete = record }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(84.dp)) }
        }

        FloatingActionButton(
            onClick = viewModel::openAddDialog,
            containerColor = ForestGreen,
            contentColor = NaturalIvory,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "नयाँ हिसाब थप्नुहोस्")
        }
    }

    if (state.isAddDialogOpen) {
        AddRecordDialog(
            onDismiss = viewModel::closeAddDialog,
            onConfirm = { type, category, amount, desc, crop ->
                viewModel.addRecord(type, category, amount, desc, crop, System.currentTimeMillis())
            }
        )
    }

    recordPendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { recordPendingDelete = null },
            containerColor = NaturalIvory,
            title = {
                Text(
                    text = "हिसाब मेट्ने हो?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
            },
            text = {
                Text(
                    text = "\"${record.categoryNe}\" (रु. ${formatAmount(record.amount)}) को विवरण स्थायी रूपमा मेटिनेछ।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepCharcoal
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecord(record.id)
                        recordPendingDelete = null
                    }
                ) {
                    Text("मेट्नुहोस्", color = ErrorMaroon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordPendingDelete = null }) {
                    Text("राखिरहनुहोस्", color = ForestGreen)
                }
            }
        )
    }
}

@Composable
private fun FarmBookkeepingGuideCard(
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSage),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "फार्मको हिसाब कसरी राख्ने?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (expanded) "बन्द गर्नुहोस्" else "खोल्नुहोस्",
                    tint = ForestGreen
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = DividerSage, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "१. दैनिक टिपोट: बीउ, मल, विषादी, सिँचाइ र मजदुरको ज्याला खर्च भएको दिनमै लेख्नुहोस्।",
                        style = MaterialTheme.typography.bodySmall,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "२. बाली अनुसार वर्गीकरण: हरेक हिसाबमा बालीको नाम लेख्दा कुन बालीबाट कति फाइदा भयो थाहा हुन्छ।",
                        style = MaterialTheme.typography.bodySmall,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "३. आम्दानी र अनुदान: उपज बिक्री गर्दा प्राप्त रकम र सरकारी/गाउँपालिका अनुदान छुट्टै राख्नुहोस्।",
                        style = MaterialTheme.typography.bodySmall,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "४. यामको समीक्षा: कटानीपछि कुल आम्दानीबाट कुल खर्च घटाई वास्तविक नाफा-नोक्सान मूल्याङ्कन गर्नुहोस्।",
                        style = MaterialTheme.typography.bodySmall,
                        color = DeepCharcoal
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    labelNe: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = labelNe,
                style = MaterialTheme.typography.labelMedium,
                color = MutedGreenGrey
            )
            Text(
                text = "रु. ${formatAmount(amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal
            )
        }
    }
}

@Composable
private fun RecordCard(
    record: FarmRecord,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = record.type == FarmRecordType.INCOME
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isIncome) SuccessGreen.copy(alpha = 0.15f) else WarningOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) {
                        Icons.AutoMirrored.Outlined.TrendingUp
                    } else {
                        Icons.AutoMirrored.Outlined.TrendingDown
                    },
                    contentDescription = null,
                    tint = if (isIncome) SuccessGreen else WarningOrange,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.categoryNe,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (record.descriptionNe.isNotBlank()) {
                    Text(
                        text = record.descriptionNe,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGreenGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                record.cropNameNe?.let { crop ->
                    Text(
                        text = "बाली: $crop",
                        style = MaterialTheme.typography.labelSmall,
                        color = ForestGreen
                    )
                }
                Text(
                    text = formatDate(record.dateMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGreenGrey
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${if (isIncome) "+" else "-"} रु. ${formatAmount(record.amount)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) SuccessGreen else ErrorMaroon
            )

            IconButton(onClick = onDeleteRequest, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "मेट्नुहोस्",
                    tint = MutedGreenGrey,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecordDialog(
    onDismiss: () -> Unit,
    onConfirm: (FarmRecordType, String, Double, String, String?) -> Unit
) {
    var selectedType by remember { mutableStateOf(FarmRecordType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf("") }
    var amount by remember { mutableDoubleStateOf(0.0) }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cropName by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = if (selectedType == FarmRecordType.INCOME) {
        FarmRecordCategories.incomeCategories
    } else {
        FarmRecordCategories.expenseCategories
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NaturalIvory,
        title = {
            Text(
                text = "नयाँ हिसाब थप्नुहोस्",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FarmRecordType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                selectedCategory = ""
                            },
                            label = { Text(type.labelNe) },
                            border = null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = NaturalIvory,
                                containerColor = SoftSage,
                                labelColor = DeepCharcoal
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("वर्ग") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = DividerSage
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input
                        amount = input.toDoubleOrNull() ?: 0.0
                    },
                    label = { Text("रकम (रु.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = DividerSage
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("विवरण (ऐच्छिक)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = DividerSage
                    )
                )

                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    label = { Text("बालीको नाम (ऐच्छिक)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = DividerSage
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedCategory.isNotBlank() && amount > 0) {
                        onConfirm(
                            selectedType,
                            selectedCategory,
                            amount,
                            description.trim(),
                            cropName.trim().ifBlank { null }
                        )
                    }
                },
                enabled = selectedCategory.isNotBlank() && amount > 0
            ) {
                Text("थप्नुहोस्", color = ForestGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द गर्नुहोस्", color = MutedGreenGrey)
            }
        }
    )
}

private fun formatAmount(amount: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("ne-NP"))
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = 0
    return nf.format(amount)
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(millis))
}
