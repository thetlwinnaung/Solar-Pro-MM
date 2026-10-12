package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SolarEngineeringEngine
import com.example.model.Language
import com.example.ui.SolarAppViewModel
import com.example.ui.components.ApplianceRow
import com.example.ui.components.MetricCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EcoGreen
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadSizerScreen(
    viewModel: SolarAppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val appliances by viewModel.appliances.collectAsState()
    val selectedLocationIndex by viewModel.selectedLocationIndex.collectAsState()
    val selectedPanelIndex by viewModel.selectedPanelIndex.collectAsState()
    val selectedBatteryAh by viewModel.selectedBatteryAh.collectAsState()
    val autonomyDays by viewModel.autonomyDays.collectAsState()
    val batteryDod by viewModel.batteryDod.collectAsState()

    val sizing = viewModel.getCurrentSizing()
    val panel = viewModel.getCurrentPanelSpec()
    val location = SolarEngineeringEngine.LOCATION_PRESETS[selectedLocationIndex]

    var showAddApplianceDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("load_sizer_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Sizing Metrics (Row 1: Inverter & Battery)
        item {
            Text(
                text = if (language == Language.MY) "ဆိုလာစနစ် အရွယ်အစား တွက်ချက်မှု ရလဒ်" else "Recommended System Sizing Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (language == Language.MY) "ဝန်အား၊ နေရောင်ခြည် (PSH) နှင့် လစ်သီယမ်ဘက်ထရီ လိုအပ်ချက်ပေါ်မူတည်၍" else "Based on connected electrical loads & Mandalay / regional solar irradiance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    titleEn = "Hybrid Inverter",
                    titleMy = "အင်ဗာတာ အရွယ်အစား",
                    value = "${sizing.recommendedInverterKw}",
                    unit = "kW",
                    subtextEn = "Continuous: ${String.format("%.2f", sizing.continuousLoadKw)} kW (Surge: ${String.format("%.1f", sizing.surgeLoadKw)} kW)",
                    subtextMy = "ပုံမှန်: ${String.format("%.2f", sizing.continuousLoadKw)} kW | မော်တာဆွဲအား: ${String.format("%.1f", sizing.surgeLoadKw)} kW",
                    icon = Icons.Default.Memory,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_inverter"
                )

                MetricCard(
                    titleEn = "LiFePO4 Battery",
                    titleMy = "လစ်သီယမ်ဘက်ထရီ",
                    value = "${String.format("%.1f", sizing.installedBatteryKwh)}",
                    unit = "kWh",
                    subtextEn = "${sizing.batteryPacksCount}x ${sizing.selectedPackAh.toInt()}Ah @ 51.2V (${sizing.autonomyDays}d Autonomy)",
                    subtextMy = "${sizing.batteryPacksCount} ချပ် x ${sizing.selectedPackAh.toInt()}Ah @ 51.2V DC",
                    icon = Icons.Default.BatteryChargingFull,
                    accentColor = WarningOrange,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_battery"
                )
            }
        }

        // Hero Sizing Metrics (Row 2: PV Array & Daily Energy)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    titleEn = "Solar PV Array",
                    titleMy = "ဆိုလာပြား စုစုပေါင်း",
                    value = "${String.format("%.2f", sizing.actualInstalledPvKwp)}",
                    unit = "kWp",
                    subtextEn = "${sizing.numberOfPanels}x ${panel.ratedWatts.toInt()}W Panels (${sizing.parallelStrings} String)",
                    subtextMy = "${panel.ratedWatts.toInt()}W ပြား ${sizing.numberOfPanels} ချပ် (${sizing.parallelStrings} လိုင်း)",
                    icon = Icons.Default.WbSunny,
                    accentColor = SolarAmber,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_pv"
                )

                MetricCard(
                    titleEn = "Daily Consumption",
                    titleMy = "နေ့စဉ် သုံးစွဲမှု စွမ်းအင်",
                    value = "${String.format("%.1f", sizing.totalDailyKwh)}",
                    unit = "kWh/d",
                    subtextEn = "Day: ${String.format("%.1f", sizing.daytimeKwh)} kWh | Night: ${String.format("%.1f", sizing.nighttimeKwh)} kWh",
                    subtextMy = "နေ့: ${String.format("%.1f", sizing.daytimeKwh)} kWh | ည: ${String.format("%.1f", sizing.nighttimeKwh)} kWh",
                    icon = Icons.Default.ElectricBolt,
                    accentColor = EcoGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_daily_kwh"
                )
            }
        }

        // Climate & Location Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = SolarAmber)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (language == Language.MY) "ဒေသနှင့် နေရောင်ခြည် (PSH)" else "Location & Peak Sun Hours (PSH)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (language == Language.MY) location.nameMy else location.nameEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${sizing.pshUsed} PSH",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == Language.MY) location.descriptionMy else location.descriptionEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Location selector chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SolarEngineeringEngine.LOCATION_PRESETS.forEachIndexed { index, loc ->
                            FilterChip(
                                selected = selectedLocationIndex == index,
                                onClick = { viewModel.setLocationIndex(index) },
                                label = {
                                    Text(
                                        text = loc.nameEn.split(" ")[0],
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedLocationIndex == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("chip_loc_$index")
                            )
                        }
                    }
                }
            }
        }

        // LiFePO4 Battery & Solar Panel Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.MY) "LiFePO4 ဘက်ထရီ နှင့် ဆိုလာပြား ရွေးချယ်မှု" else "LiFePO4 Battery & PV Module Specs",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (language == Language.MY) "လစ်သီယမ်ဘက်ထရီ မော်ဂျူး (51.2V DC Nominal):" else "LiFePO4 Module Capacity @ 51.2V DC:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(100.0, 200.0, 280.0, 300.0).forEach { ah ->
                            FilterChip(
                                selected = selectedBatteryAh == ah,
                                onClick = { viewModel.setBatteryPackAh(ah) },
                                label = { Text("${ah.toInt()}Ah (${String.format("%.1f", (ah * 51.2)/1000)}kWh)", fontSize = 11.sp) },
                                modifier = Modifier.testTag("chip_batt_${ah.toInt()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // DoD and Autonomy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.MY) "ဘက်ထရီ DoD အသုံးပြုမှု: ${(batteryDod * 100).toInt()}%" else "Usable DoD: ${(batteryDod * 100).toInt()}% (6,000 cycles)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (language == Language.MY) "ရက်စောင့်ဆိုင်း: ${autonomyDays} ရက်" else "Autonomy: ${autonomyDays} day(s)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == Language.MY) "ဆိုလာပြား မော်ဒယ် (Tier 1 Modules):" else "Tier 1 PV Module Model:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SolarEngineeringEngine.PANEL_PRESETS.forEachIndexed { idx, p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setPanelIndex(idx) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPanelIndex == idx,
                                onClick = { viewModel.setPanelIndex(idx) }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = p.modelName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (selectedPanelIndex == idx) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "Voc: ${p.voc}V | Vmp: ${p.vmp}V | Isc: ${p.isc}A | Imp: ${p.imp}A",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // String Electrical Check Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.MY) "ဆိုလာလိုင်း လျှပ်စစ်ဗို့အား စစ်ဆေးမှု" else "String Electrical Parameters Check",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SolarAmber
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EcoGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "MPPT SAFE",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == Language.MY)
                            "• အေးမြသော မနက်ခင်း အမြင့်ဆုံး အဖွင့်ဗို့ (Cold Voc 15°C): ${String.format("%.1f", sizing.stringVocCold)} V (အင်ဗာတာ 500V limit အောက် လုံခြုံစွာ ရှိပါသည်)\n" +
                                    "• အလုပ်လုပ်ချိန် လိုင်းဗို့အား (Vmp): ${String.format("%.1f", sizing.stringVmp)} V (MPPT 150V - 450V အတွင်း စံပြဖြစ်ပါသည်)\n" +
                                    "• အမြင့်ဆုံး လျှပ်စီး (Array Isc): ${String.format("%.2f", sizing.arrayIsc)} A\n" +
                                    "• ဘက်ထရီ စီးဆင်းနိုင်သော အမြင့်ဆုံး လျှပ်စီး (Full Inverter Load): ${String.format("%.1f", sizing.maxBatteryDischargeCurrentA)} A"
                        else
                            "• Cold Morning Open-Circuit Voltage (Voc 15°C): ${String.format("%.1f", sizing.stringVocCold)} V (Safe under 500V DC limit)\n" +
                                    "• Operating String Voltage (Vmp): ${String.format("%.1f", sizing.stringVmp)} V (Optimally centered in 150-450V MPPT range)\n" +
                                    "• Total Array Short-Circuit Current (Isc): ${String.format("%.2f", sizing.arrayIsc)} A\n" +
                                    "• Max Continuous Battery Discharge Current @ 44V Cutoff: ${String.format("%.1f", sizing.maxBatteryDischargeCurrentA)} A",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Appliance Load Builder Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == Language.MY) "ချိတ်ဆက်မည့် လျှပ်စစ်ပစ္စည်းများ စာရင်း" else "Connected Electrical Appliances",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == Language.MY) "အရေအတွက်နှင့် နေ့/ည သုံးစွဲချိန်များကို ပြင်ဆင်ပါ" else "Adjust quantities and day/night usage hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                FilledTonalButton(
                    onClick = { showAddApplianceDialog = true },
                    modifier = Modifier.testTag("btn_add_custom_appliance")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (language == Language.MY) "အသစ်ထည့်" else "Add Custom", fontSize = 12.sp)
                }
            }
        }

        // Appliance List
        items(appliances, key = { it.id }) { app ->
            ApplianceRow(
                appliance = app,
                language = language,
                onQuantityChange = { viewModel.updateApplianceQuantity(app.id, it) },
                onHoursChange = { day, night -> viewModel.updateApplianceHours(app.id, day, night) },
                onDelete = { viewModel.removeAppliance(app.id) }
            )
        }

        // Reset to Defaults Button
        item {
            OutlinedButton(
                onClick = { viewModel.resetToDefaults() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_reset_defaults")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (language == Language.MY) "မူလစံနှုန်းများသို့ ပြန်ထားရန်" else "Reset to Engineering Defaults")
            }
        }
    }

    if (showAddApplianceDialog) {
        AddApplianceDialog(
            language = language,
            onDismiss = { showAddApplianceDialog = false },
            onAdd = { nameEn, nameMy, watts, surge, qty, dayH, nightH, ind ->
                viewModel.addCustomAppliance(nameEn, nameMy, watts, surge, qty, dayH, nightH, ind)
                showAddApplianceDialog = false
            }
        )
    }
}

@Composable
private fun AddApplianceDialog(
    language: Language,
    onDismiss: () -> Unit,
    onAdd: (String, String, Double, Double, Int, Double, Double, Boolean) -> Unit
) {
    var nameEn by remember { mutableStateOf("") }
    var nameMy by remember { mutableStateOf("") }
    var wattsStr by remember { mutableStateOf("100") }
    var surgeStr by remember { mutableStateOf("1.0") }
    var qtyStr by remember { mutableStateOf("1") }
    var dayHStr by remember { mutableStateOf("2.0") }
    var nightHStr by remember { mutableStateOf("2.0") }
    var isInductive by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == Language.MY) "လျှပ်စစ်သုံးပစ္စည်း အသစ်ထည့်ရန်" else "Add Connected Appliance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text("Name (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_app_name_en")
                )

                OutlinedTextField(
                    value = nameMy,
                    onValueChange = { nameMy = it },
                    label = { Text("အမည် (မြန်မာ)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_app_name_my")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wattsStr,
                        onValueChange = { wattsStr = it },
                        label = { Text("Power (Watts)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_app_watts")
                    )

                    OutlinedTextField(
                        value = surgeStr,
                        onValueChange = { surgeStr = it },
                        label = { Text("Surge Mult.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("input_app_surge")
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = qtyStr,
                        onValueChange = { qtyStr = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = dayHStr,
                        onValueChange = { dayHStr = it },
                        label = { Text("Day Hours") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = nightHStr,
                        onValueChange = { nightHStr = it },
                        label = { Text("Night Hours") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = wattsStr.toDoubleOrNull() ?: 100.0
                    val s = surgeStr.toDoubleOrNull() ?: 1.0
                    val q = qtyStr.toIntOrNull() ?: 1
                    val dh = dayHStr.toDoubleOrNull() ?: 2.0
                    val nh = nightHStr.toDoubleOrNull() ?: 2.0
                    onAdd(nameEn, nameMy, w, s, q, dh, nh, isInductive)
                },
                modifier = Modifier.testTag("btn_confirm_add_appliance")
            ) {
                Text(if (language == Language.MY) "ထည့်မည်" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == Language.MY) "မလုပ်တော့ပါ" else "Cancel")
            }
        }
    )
}
