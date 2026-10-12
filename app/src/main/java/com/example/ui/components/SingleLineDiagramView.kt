package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.roundToInt
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.SystemSizingResult
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EcoGreen
import com.example.ui.theme.SolarAmber
import com.example.ui.theme.WarningOrange

enum class SldNode {
    NONE,
    PV_ARRAY,
    DC_COMBINER_BOX,
    HYBRID_INVERTER,
    BATTERY_MCCB,
    LIFEPO4_BATTERY,
    GRID_AC_PROTECTION,
    BACKUP_LOADS,
    EARTHING_SYSTEM
}

@Composable
fun SingleLineDiagramView(
    sizing: SystemSizingResult,
    language: Language,
    modifier: Modifier = Modifier
) {
    var selectedNode by remember { mutableStateOf(SldNode.HYBRID_INVERTER) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("single_line_diagram_view")
    ) {
        // Visual Diagram Canvas / Block Layout
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == Language.MY) "ဆိုလာစနစ် လျှပ်စစ်ပုံကြမ်း (SLD)" else "Single-Line Diagram (SLD Schematic)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (language == Language.MY) "အကွက်တစ်ခုချင်းစီကို နှိပ်၍ အသေးစိတ် စစ်ဆေးပါ" else "Tap any component block to inspect electrical parameters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "IEC 60364-7-712",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Flow Architecture Grid
                // Row 1: PV Array -> DC Protection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SldBlockItem(
                        title = "PV Array",
                        subtitle = "${String.format("%.2f", sizing.actualInstalledPvKwp)} kWp",
                        subDetail = "${sizing.numberOfPanels} Panels (${sizing.parallelStrings}S)",
                        icon = Icons.Default.WbSunny,
                        color = SolarAmber,
                        isSelected = selectedNode == SldNode.PV_ARRAY,
                        onClick = { selectedNode = SldNode.PV_ARRAY },
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = SolarAmber,
                        modifier = Modifier.size(20.dp)
                    )

                    SldBlockItem(
                        title = "DC Box",
                        subtitle = "Fuses + SPD + MCB",
                        subDetail = "1000V DC Rated",
                        icon = Icons.Default.Shield,
                        color = SolarAmber,
                        isSelected = selectedNode == SldNode.DC_COMBINER_BOX,
                        onClick = { selectedNode = SldNode.DC_COMBINER_BOX },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Vertical indicator down to Inverter
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Central Hybrid Inverter with Battery on left, Grid on right
                SldBlockItem(
                    title = "Hybrid Inverter (Central Hub)",
                    subtitle = "${sizing.recommendedInverterKw} kW (230V / 51.2V DC)",
                    subDetail = "Max DC Current: ${String.format("%.1f", sizing.maxBatteryDischargeCurrentA)}A",
                    icon = Icons.Default.Memory,
                    color = MaterialTheme.colorScheme.primary,
                    isSelected = selectedNode == SldNode.HYBRID_INVERTER,
                    onClick = { selectedNode = SldNode.HYBRID_INVERTER },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row 3: Battery Branch (Left) & AC Grid / Load Branch (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Battery Column
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (language == Language.MY) "ဘက်ထရီ လမ်းကြောင်း" else "DC Battery Branch",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )

                        SldBlockItem(
                            title = "DC MCCB",
                            subtitle = "Breaking >= 25kA",
                            subDetail = "Class T Fuse",
                            icon = Icons.Default.ToggleOn,
                            color = WarningOrange,
                            isSelected = selectedNode == SldNode.BATTERY_MCCB,
                            onClick = { selectedNode = SldNode.BATTERY_MCCB }
                        )

                        SldBlockItem(
                            title = "LiFePO4 51.2V",
                            subtitle = "${String.format("%.1f", sizing.installedBatteryKwh)} kWh Bank",
                            subDetail = "${sizing.batteryPacksCount}x ${sizing.selectedPackAh.roundToInt()}Ah",
                            icon = Icons.Default.BatteryChargingFull,
                            color = WarningOrange,
                            isSelected = selectedNode == SldNode.LIFEPO4_BATTERY,
                            onClick = { selectedNode = SldNode.LIFEPO4_BATTERY }
                        )
                    }

                    // AC Branch
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (language == Language.MY) "AC မီးလိုင်းနှင့် ဝန်အား" else "AC Grid & Loads",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue
                        )

                        SldBlockItem(
                            title = "AC Protection",
                            subtitle = "V/A Relay + SPD",
                            subDetail = "TOMZN 63A + 2P MCB",
                            icon = Icons.Default.ElectricBolt,
                            color = ElectricBlue,
                            isSelected = selectedNode == SldNode.GRID_AC_PROTECTION,
                            onClick = { selectedNode = SldNode.GRID_AC_PROTECTION }
                        )

                        SldBlockItem(
                            title = "Backup Loads",
                            subtitle = "30mA Type A RCD",
                            subDetail = "${String.format("%.2f", sizing.continuousLoadKw)} kW Continuous",
                            icon = Icons.Default.Home,
                            color = EcoGreen,
                            isSelected = selectedNode == SldNode.BACKUP_LOADS,
                            onClick = { selectedNode = SldNode.BACKUP_LOADS }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Earthing bar
                SldBlockItem(
                    title = "Equipotential Earthing & Ground Rod (< 5 Ohms)",
                    subtitle = "All SPDs, Frames & Chassis bonded with 16mm² copper",
                    subDetail = "IEC 60364-5-54 Standard",
                    icon = Icons.Default.Terrain,
                    color = EcoGreen,
                    isSelected = selectedNode == SldNode.EARTHING_SYSTEM,
                    onClick = { selectedNode = SldNode.EARTHING_SYSTEM },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Node Inspection Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                NodeDetailContent(
                    node = selectedNode,
                    sizing = sizing,
                    language = language
                )
            }
        }
    }
}

@Composable
private fun SldBlockItem(
    title: String,
    subtitle: String,
    subDetail: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
        border = if (isSelected)
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(color))
        else
            CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subDetail,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun NodeDetailContent(
    node: SldNode,
    sizing: SystemSizingResult,
    language: Language
) {
    when (node) {
        SldNode.PV_ARRAY -> {
            Text(
                text = if (language == Language.MY) "ဆိုလာပြား တပ်ဆင်မှု အချက်အလက် (PV Array)" else "PV Array Parameters & Sizing",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SolarAmber
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "စုစုပေါင်း ဝပ်: ${String.format("%.2f", sizing.actualInstalledPvKwp)} kWp | ပြားအရေအတွက်: ${sizing.numberOfPanels} ချပ်\n" +
                            "လိုင်းဖွဲ့စည်းပုံ: ${sizing.parallelStrings} လိုင်း (တစ်လိုင်းလျှင် ${sizing.panelsPerString} ချပ် ဆက်ထား)\n" +
                            "မနက်စောစော အေးချိန် အဖွင့်ဗို့ (Voc cold): ${String.format("%.1f", sizing.stringVocCold)} V (MPPT အမြင့်ဆုံး 500V အောက် လုံခြုံစွာ ရှိရမည်)\n" +
                            "အလုပ်လုပ်ချိန် ဗို့အား (Vmp): ${String.format("%.1f", sizing.stringVmp)} V (Inverter MPPT 150V-450V အတွင်း ကောင်းစွာ အလုပ်လုပ်)"
                else
                    "Total Array: ${String.format("%.2f", sizing.actualInstalledPvKwp)} kWp | Module Count: ${sizing.numberOfPanels} Units\n" +
                            "Configuration: ${sizing.parallelStrings} Parallel String(s) of ${sizing.panelsPerString} Panels in series\n" +
                            "Cold Morning Voc (15°C): ${String.format("%.1f", sizing.stringVocCold)} V (Safely below 500V DC inverter limit)\n" +
                            "Operating String Vmp: ${String.format("%.1f", sizing.stringVmp)} V (Optimally within 150V - 450V MPPT window)",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        SldNode.DC_COMBINER_BOX -> {
            Text(
                text = if (language == Language.MY) "DC အကာအကွယ် ဘောက်စ် (IEC 60364-7-712)" else "DC Switchgear & Combiner Protection",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SolarAmber
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "• DC Fuses: gPV 1000V DC 25A (အပြိုင်လိုင်းများ အချင်းချင်း ပြောင်းပြန်လျှပ်စီး မစီးစေရန်)\n" +
                            "• DC Isolator MCB: 32A / 1000V DC 2-Pole (DC arc chute ပါဝင်သော ဘရိတ်ကာ)\n" +
                            "• DC Type 2 SPD: Uc 600V/1000V DC, In 20kA, Imax 40kA (မိုးကြိုးဒဏ် ကာကွယ်ရန်)"
                else
                    "• DC Fuses: gPV 1000V DC cylindrical 10x38mm (Reverse overcurrent protection)\n" +
                            "• DC Isolator MCB: 2-Pole True DC Circuit Breaker (Rated for cold Voc and 1.25x Isc)\n" +
                            "• DC Type 2 SPD: Clamps lightning surges and shunts to earthing rod.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        SldNode.HYBRID_INVERTER -> {
            Text(
                text = if (language == Language.MY) "အင်ဗာတာ တွက်ချက်မှု (Hybrid Inverter)" else "Hybrid Inverter Sizing & Hub",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "အကြံပြုအင်ဗာတာ: ${sizing.inverterModelSuggestion}\n" +
                            "• ပုံမှန်ဝန်အား (Continuous Load): ${String.format("%.2f", sizing.continuousLoadKw)} kW (Safety Factor 1.25x ထည့်သွင်းတွက်ချက်)\n" +
                            "• မော်တာ/ကွန်ပရက်ဆာ ရုတ်တရက်တက်အား (Motor Surge): ${String.format("%.2f", sizing.surgeLoadKw)} kW\n" +
                            "• ဘက်ထရီ စီးဆင်းနိုင်သော အမြင့်ဆုံးလျှပ်စီး: ${String.format("%.1f", sizing.maxBatteryDischargeCurrentA)} A"
                else
                    "Recommended: ${sizing.inverterModelSuggestion}\n" +
                            "• Continuous Load: ${String.format("%.2f", sizing.continuousLoadKw)} kW (Sized with 1.25x engineering headroom)\n" +
                            "• Peak Inductive Surge Load: ${String.format("%.2f", sizing.surgeLoadKw)} kW (Handled by inverter surge rating)\n" +
                            "• Max Battery Full-Load Discharge: ${String.format("%.1f", sizing.maxBatteryDischargeCurrentA)} A @ 44.0V Cutoff",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        SldNode.BATTERY_MCCB -> {
            Text(
                text = if (language == Language.MY) "ဘက်ထရီ DC MCCB & Class T ဖျူးစ်" else "Battery DC MCCB & Class T Protection",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WarningOrange
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "လစ်သီယမ်ဘက်ထရီသည် Short-Circuit ဖြစ်ပါက လျှပ်စီးအလွန်မြင့်မားသဖြင့် Breaking Capacity အနည်းဆုံး 20kA - 25kA ရှိသော DC MCCB ကို မဖြစ်မနေ အသုံးပြုရမည်။ Standard AC MCCB လုံးဝ မသုံးရပါ။\n" +
                            "ဖျူးစ်အဖြစ် Class T သို့မဟုတ် NH00 DC High-Speed Fuse ကို ထပ်ဆောင်းတပ်ဆင်ပါ။"
                else
                    "LiFePO4 battery chemistry yields extremely low internal resistance and massive short-circuit currents. Breaking capacity Icu MUST be >= 25kA AIC at DC voltage.\n" +
                            "Class T or NH00 ultra-fast DC fuse clears dead shorts in milliseconds before catastrophic thermal runaway.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        SldNode.LIFEPO4_BATTERY -> {
            Text(
                text = if (language == Language.MY) "လစ်သီယမ်ဘက်ထရီ ဘဏ် (51.2V DC Nominal)" else "LiFePO4 Battery Bank (51.2V Nominal)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WarningOrange
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "တပ်ဆင်ပမာဏ: ${String.format("%.1f", sizing.installedBatteryKwh)} kWh (${sizing.batteryPacksCount} ချပ် x ${sizing.selectedPackAh.roundToInt()}Ah @ 51.2V)\n" +
                            "• ညဘက် လိုအပ်သော စွမ်းအင်: ${String.format("%.2f", sizing.nighttimeKwh)} kWh\n" +
                            "• အသုံးပြုရန် သတ်မှတ် DoD: 80% (စက်ဝိုင်း ၆၀၀၀ အကြိမ် ကျော်ကြာရှည်ခံ)\n" +
                            "• BMS ဆက်သွယ်ရေး: RS485 / CAN Bus ချိတ်ဆက်ကာ Inverter နှင့် closed-loop protocol သုံးရန်"
                else
                    "Installed Capacity: ${String.format("%.1f", sizing.installedBatteryKwh)} kWh (${sizing.batteryPacksCount}x ${sizing.selectedPackAh.roundToInt()}Ah @ 51.2V DC)\n" +
                            "• Nighttime Demand: ${String.format("%.2f", sizing.nighttimeKwh)} kWh (Autonomy: ${sizing.autonomyDays} days)\n" +
                            "• Usable DoD: 80% DoD (ensures 6,000+ cycle life, 10-15 years operation)\n" +
                            "• Closed-Loop BMS: CAN / RS485 protocol communication with hybrid inverter.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        SldNode.GRID_AC_PROTECTION -> {
            Text(
                text = if (language == Language.MY) "AC မီးလိုင်း အကာအကွယ် (TOMZN V/A Protector)" else "AC Grid & Utility Protection (Power Quality)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "မြန်မာနိုင်ငံ လျှပ်စစ်လိုင်းအတွက် မရှိမဖြစ်လိုအပ်သော အကာအကွယ်:\n" +
                            "• TOMZN / SINOTIMER 63A Digital V/A Protector:\n" +
                            "  - အမြင့်ဗို့အား ဖြတ်တောက်မှု: 260V\n" +
                            "  - အနိမ့်ဗို့အား ဖြတ်တောက်မှု: 175V\n" +
                            "  - မီးလာပြီးနောက် ပြန်လည်စောင့်ဆိုင်းချိန်: ၃၀ မှ ၆၀ စက္ကန့်\n" +
                            "• AC Type 2 SPD: မီးတိုင်မှ မိုးကြိုးလှိုင်းများ အင်ဗာတာသို့ မရောက်စေရန် တားဆီး"
                else
                    "Essential for Myanmar grid stability:\n" +
                            "• Digital Over/Under Voltage Relay (e.g. TOMZN 63A):\n" +
                            "  - Over-voltage limit: 260V | Under-voltage limit: 175V\n" +
                            "  - Reconnect delay: 30 - 60s (prevents erratic reclosure transformer surges)\n" +
                            "• AC Type 2 SPD (275V AC): Protects inverter AC input from utility transients.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        SldNode.BACKUP_LOADS -> {
            Text(
                text = if (language == Language.MY) "အရေးပေါ်မီးသုံးစွဲမှု ဘုတ် (Backup Panel & RCD)" else "Backup Sub-Panel & Shock Protection",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EcoGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "• 30mA Type A RCD (ELCB): ဓာတ်လိုက်မှု အန္တရာယ်မှ ကာကွယ်ရန် မဖြစ်မနေ တပ်ဆင်ရမည်။\n" +
                            "• Neutral-Earth Bonding: မီးပျက်ချိန် အင်ဗာတာ EPS မုဒ်တွင် Neutral ချိတ်ဆက်မှု လုံခြုံစွာ စီမံထားရပါမည်။"
                else
                    "• 30mA Type A RCD / RCBO: Critical personal shock safety on final socket circuits.\n" +
                            "• Neutral-Earth Bond: Dynamic relay bonding during off-grid EPS operation to ensure RCD trips correctly during blackouts.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        SldNode.EARTHING_SYSTEM -> {
            Text(
                text = if (language == Language.MY) "မြေစိုက်စနစ် (Earthing System < 5 Ohms)" else "Grounding & Lightning Earthing System",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EcoGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == Language.MY)
                    "• မြေစိုက်တိုင်: ၅/၈ လက်မ x ၁၀ ပေ ကြေးနီကပ် သံတိုင် (Copper-bonded steel rod)\n" +
                            "• မြေစိုက်ခုခံမှု: ၅ အုမ်း အောက် မဖြစ်မနေ ရရှိရပါမည် (မန္တလေး မိုးကြိုးထစ်ချုန်းမှုတွင် SPD အလုပ်လုပ်နိုင်ရန်)\n" +
                            "• ဆိုလာဘောင်မြေစိုက်ကြိုး: 6-10 mm² အစိမ်းဝါကြိုး\n" +
                            "• ပင်မမြေစိုက်ဘားမှ တိုင်သို့: 16 mm² ကြေးနီကြိုးကြီး"
                else
                    "• Ground Electrode: 5/8\" x 10ft Copper-bonded steel rod driven into soil.\n" +
                            "• Earth Resistance: Must test < 5.0 Ohms (critical for Myanmar monsoons and SPD surge discharge).\n" +
                            "• PV Array Frame: 6-10 mm² yellow-green copper equipotential bonding.\n" +
                            "• Main Earthing Busbar to Electrode: 16 mm² pure copper conductor.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
        else -> {}
    }
}
