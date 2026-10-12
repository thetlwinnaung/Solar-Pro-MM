package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CableCalculation
import com.example.model.Language
import com.example.ui.SolarAppViewModel
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EcoGreen
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.WarningOrange

@Composable
fun CableGroundingScreen(
    viewModel: SolarAppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val pvCableLength by viewModel.pvCableLength.collectAsState()
    val batteryCableLength by viewModel.batteryCableLength.collectAsState()
    val acCableLength by viewModel.acCableLength.collectAsState()

    val cables = viewModel.getCurrentCables()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cable_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Cable,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.MY) "ကြေးနီကေဘယ်နှင့် မြေစိုက်လိုင်း တွက်ချက်မှု" else "Copper Cable & Grounding Sizing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == Language.MY) "ဗို့အားကျဆင်းမှု (Voltage Drop < 1.5%) နှင့် စွမ်းအင်ဆုံးရှုံးမှု အနိမ့်ဆုံးဖြစ်စေရန်" else "Calculated via Ohm's Law: ΔV = (2 * L * I * ρ) / S (ρ = 0.0175)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Cable Length Adjustment Sliders Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.MY) "ဝါယာအလျား သတ်မှတ်ချက်များ (မီတာ):" else "Configure Cable Run Distances (Meters):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // PV Cable slider
                    Text(
                        text = if (language == Language.MY) "ဆိုလာပြားမှ အင်ဗာတာသို့ အကွာအဝေး: ${pvCableLength.toInt()} မီတာ"
                        else "PV Array to Inverter Distance: ${pvCableLength.toInt()} meters",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SolarAmber
                    )
                    Slider(
                        value = pvCableLength.toFloat(),
                        onValueChange = { viewModel.setPvCableLength(it.toDouble()) },
                        valueRange = 5f..60f,
                        steps = 11,
                        modifier = Modifier.fillMaxWidth().testTag("slider_pv_length")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Battery Cable slider
                    Text(
                        text = if (language == Language.MY) "ဘက်ထရီမှ အင်ဗာတာသို့ အကွာအဝေး: ${String.format("%.1f", batteryCableLength)} မီတာ (အထူးတိုရမည်)"
                        else "Battery Bank to Inverter Distance: ${String.format("%.1f", batteryCableLength)} meters (Keep <= 2m)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = WarningOrange
                    )
                    Slider(
                        value = batteryCableLength.toFloat(),
                        onValueChange = { viewModel.setBatteryCableLength(it.toDouble()) },
                        valueRange = 0.5f..5.0f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth().testTag("slider_batt_length")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // AC Cable slider
                    Text(
                        text = if (language == Language.MY) "အစိုးရမီတာမှ အင်ဗာတာသို့ အကွာအဝေး: ${acCableLength.toInt()} မီတာ"
                        else "AC Grid Meter to Inverter Distance: ${acCableLength.toInt()} meters",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricBlue
                    )
                    Slider(
                        value = acCableLength.toFloat(),
                        onValueChange = { viewModel.setAcCableLength(it.toDouble()) },
                        valueRange = 5f..50f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth().testTag("slider_ac_length")
                    )
                }
            }
        }

        // Cable Calculations List
        items(cables) { cable ->
            CableResultCard(
                cable = cable,
                language = language
            )
        }

        // Earthing & Ground Rod Engineering Spec Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terrain,
                            contentDescription = null,
                            tint = EcoGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.MY) "မြေစိုက်စနစ် စံချိန်စံညွှန်း လမ်းညွှန် (Earth Resistance < 5 Ω)" else "Earthing System Engineering Guidelines (R < 5 Ω)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == Language.MY)
                            "• မြေစိုက်တိုင် (Ground Rod): ၅/၈ လက်မ အချင်း၊ အနည်းဆုံး အလျား ၈ ပေ မှ ၁၀ ပေ ရှိသော ကြေးနီကပ် သံတိုင် (Copper-Bonded Steel Rod) ကို မြေတွင်းသို့ ဒေါင်လိုက် ရိုက်သွင်းပါ။\n" +
                                    "• ခုခံမှုတန်ဖိုး: Earth Tester ဖြင့် တိုင်းတာရာတွင် ၅ အုမ်း (5.0 Ohms) အောက် မဖြစ်မနေ ရှိရပါမည်။ မိုးကြိုးဒဏ်ခံနိုင်ရန် လိုအပ်ပါက Bentonite မြေစိုက်ဓာတုဆား သုံးပါ။\n" +
                                    "• ဆိုလာဘောင် မြေစိုက်ကြိုး: 6-10 mm² အစိမ်းဝါ ကြေးနီကြိုးဖြင့် ဆိုလာပြား ဘောင်တိုင်းကို သံချေးမတက်သော Stainless Bolt ဖြင့် ချိတ်ဆက်ပါ။\n" +
                                    "• ပင်မမြေစိုက်လိုင်း: SPD နှင့် Inverter Chassis မှ မြေစိုက်တိုင်သို့ 16 mm² ကြေးနီကြိုးကြီး သုံးပါ။"
                        else
                            "• Ground Electrode: 5/8\" diameter, 8ft to 10ft copper-bonded steel rod driven vertically into undisturbed soil.\n" +
                                    "• Resistance Requirement: Must measure strictly < 5.0 Ohms. In sandy or rocky Mandalay soils, treat soil with conductive bentonite enhancement compound.\n" +
                                    "• PV Module Frame Bonding: 6-10 mm² yellow-green bare/insulated copper with serrated grounding washers.\n" +
                                    "• Main Earth Conductor: 16 mm² pure copper bonding SPD surge ground bus to external ground rod.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CableResultCard(
    cable: CableCalculation,
    language: Language
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == Language.MY) cable.circuitNameMy else cable.circuitNameEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == Language.MY) cable.circuitNameEn else cable.circuitNameMy,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${cable.recommendedCrossSectionMm2} mm²",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == Language.MY) cable.cableTypeMy else cable.cableTypeEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Electrical metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Length / Current", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${cable.lengthMeters}m | ${String.format("%.1f", cable.currentAmps)}A", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text("Voltage Drop", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${String.format("%.2f", cable.actualVoltageDropV)}V (${String.format("%.2f", cable.voltageDropPercent)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (cable.isCompliant) EcoGreen else DangerRed
                    )
                }

                Column {
                    Text("Cable Power Loss", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format("%.1f", cable.powerLossWatts)} W", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }

            if (cable.actualVoltageDropV > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (cable.voltageDropPercent / 3.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = if (cable.isCompliant) EcoGreen else DangerRed,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}
