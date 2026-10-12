package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.SolarAppViewModel
import com.example.ui.components.MetricCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EcoGreen
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.WarningOrange

@Composable
fun TariffRoiScreen(
    viewModel: SolarAppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val hoursBlackout by viewModel.hoursBlackoutPerDay.collectAsState()
    val dieselCost by viewModel.dieselGenCostPerKwhMmk.collectAsState()
    val customTurnkeyCostLakhs by viewModel.customTurnkeyCostLakhs.collectAsState()

    val tariff = viewModel.getCurrentTariff()
    val sizing = viewModel.getCurrentSizing()

    var showCostEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tariff_roi_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.MY) "မြန်မာနိုင်ငံ မီတာခ သက်သာမှုနှင့် ROI" else "Myanmar MOEP Tariff ROI & Savings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (language == Language.MY) "လျှပ်စစ်စွမ်းအားဝန်ကြီးဌာန (MOEP / MESC) အိမ်သုံးမီတာနှုန်းထား တွက်ချက်မှု" else "MOEP Residential Tiered Tariffs (50, 100, 150, 300 MMK/unit)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    titleEn = "Monthly Total Savings",
                    titleMy = "လစဉ် သက်သာငွေ စုစုပေါင်း",
                    value = String.format("%,.0f", tariff.totalMonthlySavingsMmk),
                    unit = "MMK",
                    subtextEn = "Grid: ${String.format("%,.0f", tariff.monthlyGridSavingsMmk)} | Gen: ${String.format("%,.0f", tariff.monthlyGenFuelSavingsMmk)}",
                    subtextMy = "မီတာခ: ${String.format("%,.0f", tariff.monthlyGridSavingsMmk)} | မီးစက်ဆီ: ${String.format("%,.0f", tariff.monthlyGenFuelSavingsMmk)}",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = EcoGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_monthly_savings"
                )

                MetricCard(
                    titleEn = "Payback Period",
                    titleMy = "အရင်းကျေမည့်ကာလ",
                    value = String.format("%.1f", tariff.paybackYears),
                    unit = "Years",
                    subtextEn = "Cost: ~${String.format("%.1f", tariff.estimatedTurnkeyCostMmk / 100_000.0)} Lakhs MMK",
                    subtextMy = "ကုန်ကျစရိတ်: ~${String.format("%.1f", tariff.estimatedTurnkeyCostMmk / 100_000.0)} သိန်း",
                    icon = Icons.Default.Timeline,
                    accentColor = SolarAmber,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_payback"
                )
            }
        }

        // MOEP 4-Tier Tariff Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.MY) "MOEP အိမ်သုံး လျှပ်စစ်မီတာ အဆင့်ဆင့် နှုန်းထားများ" else "MOEP / MESC Residential Tiered Tariffs",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TierRow(tier = "Tier 1 (1–50 units)", rate = "50 MMK / unit", units = tariff.tier1Units, cost = tariff.tier1CostMmk)
                    TierRow(tier = "Tier 2 (51–100 units)", rate = "100 MMK / unit", units = tariff.tier2Units, cost = tariff.tier2CostMmk)
                    TierRow(tier = "Tier 3 (101–200 units)", rate = "150 MMK / unit", units = tariff.tier3Units, cost = tariff.tier3CostMmk)
                    TierRow(tier = "Tier 4 (201+ units)", rate = "300 MMK / unit", units = tariff.tier4Units, cost = tariff.tier4CostMmk)

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == Language.MY) "ဆိုလာမသုံးမီ လစဉ် မီတာခ:" else "Bill Without Solar:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${String.format("%,.0f", tariff.billWithoutSolarMmk)} MMK",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (language == Language.MY) "ဆိုလာတပ်ပြီး လစဉ် မီတာခ:" else "Bill With Solar:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${String.format("%,.0f", tariff.billWithSolarMmk)} MMK",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreen
                            )
                        }
                    }
                }
            }
        }

        // Generator Fuel Replacement Card (Load shedding in Myanmar)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (language == Language.MY) "မီးပျက်ချိန် မီးစက်ဒီဇယ်ဆီ သက်သာမှု" else "Avoided Generator Fuel (Load Shedding)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == Language.MY) "ဆိုလာနှင့် လစ်သီယမ်ဘက်ထရီကြောင့် မီးစက်မောင်းစရာမလိုတော့ပါ" else "Solar + LiFePO4 replaces expensive diesel/petrol generator runtime",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (language == Language.MY) "နေ့စဉ် မီးပျက်ချိန်: ${hoursBlackout.toInt()} နာရီ/ရက်"
                        else "Daily Load Shedding Blackout Duration: ${hoursBlackout.toInt()} hrs/day",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = hoursBlackout.toFloat(),
                        onValueChange = { viewModel.setHoursBlackoutPerDay(it.toDouble()) },
                        valueRange = 0f..12f,
                        steps = 11,
                        modifier = Modifier.fillMaxWidth().testTag("slider_blackout_hours")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = EcoGreen.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == Language.MY) "မီးစက်ဆီဖိုး လစဉ် သက်သာငွေ:" else "Monthly Fuel Cost Saved:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+${String.format("%,.0f", tariff.monthlyGenFuelSavingsMmk)} MMK",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = EcoGreen
                            )
                        }
                    }
                }
            }
        }

        // 10-Year Cumulative Savings & Turnkey Investment Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.MY) "၁၀ နှစ်တာ အသားတင် အမြတ်နှင့် ရင်းနှီးမြှုပ်နှံမှု" else "10-Year Cumulative Financial ROI",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = { showCostEditDialog = true }) {
                            Text(if (language == Language.MY) "စရိတ်ပြင်ရန်" else "Edit Budget", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Turnkey Investment", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${String.format("%,.0f", tariff.estimatedTurnkeyCostMmk)} MMK\n(~${String.format("%.1f", tariff.estimatedTurnkeyCostMmk / 100_000.0)} Lakhs)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("10-Year Net Profit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "+${String.format("%,.0f", tariff.tenYearNetSavingsMmk)} MMK\n(~${String.format("%.1f", tariff.tenYearNetSavingsMmk / 100_000.0)} Lakhs)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = EcoGreen
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCostEditDialog) {
        var lakhsStr by remember { mutableStateOf(String.format("%.1f", tariff.estimatedTurnkeyCostMmk / 100_000.0)) }
        AlertDialog(
            onDismissRequest = { showCostEditDialog = false },
            title = { Text(if (language == Language.MY) "စနစ်တန်ဖိုး (သိန်းကျပ်) သတ်မှတ်ရန်" else "Set Turnkey System Cost (Lakhs MMK)") },
            text = {
                OutlinedTextField(
                    value = lakhsStr,
                    onValueChange = { lakhsStr = it },
                    label = { Text("Cost in Lakhs MMK (1 Lakh = 100,000 MMK)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_turnkey_cost")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val l = lakhsStr.toDoubleOrNull()
                        viewModel.setCustomTurnkeyCostLakhs(l)
                        showCostEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCostEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TierRow(
    tier: String,
    rate: String,
    units: Double,
    cost: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.2f)) {
            Text(text = tier, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(text = rate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }

        Text(
            text = "${String.format("%.1f", units)} kWh",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = "${String.format("%,.0f", cost)} MMK",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
