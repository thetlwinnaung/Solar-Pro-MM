package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.SwitchgearCategory
import com.example.ui.SolarAppViewModel
import com.example.ui.components.SwitchgearItemCard
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SolarAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchgearProtectionScreen(
    viewModel: SolarAppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val switchgearList = viewModel.getCurrentSwitchgear()

    var selectedCategoryFilter by remember { mutableStateOf<SwitchgearCategory?>(null) }

    val filteredList = remember(switchgearList, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) switchgearList
        else switchgearList.filter { it.category == selectedCategoryFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("switchgear_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Standard Banner
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
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == Language.MY) "IEC 60364-7-712 စံချိန်မီ အကာအကွယ်ပစ္စည်းများ" else "IEC 60364-7-712 Switchgear Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (language == Language.MY) "ဆိုလာ၊ ဘက်ထရီ၊ အစိုးရမီတာနှင့် အိမ်တွင်းမီးလိုင်း အဆင့်တိုင်းအတွက် ဘေးကင်းလုံခြုံရေး" else "Solar PV, LiFePO4 battery, grid, and load circuit protection ratings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Critical Safety Warning Card for Myanmar Installations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DangerRed.copy(alpha = 0.4f)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = DangerRed,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == Language.MY) "မြန်မာနိုင်ငံတွင် တွေ့ရလေ့ရှိသော အန္တရာယ်ကြီးမားသည့် အမှားများ:" else "Critical Field Safety Warnings (Myanmar Context):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.MY)
                                "၁။ ဆိုလာလိုင်းတွင် သာမန် AC MCB ကို လုံးဝ အသုံးမပြုရပါ။ DC မီးပွား (Arc) သည် မီးလောင်ကျွမ်းစေပါသည်။\n" +
                                        "၂။ လစ်သီယမ်ဘက်ထရီတွင် Breaking Capacity (>=20kA) မပြည့်သော ဘရိတ်ကာ မတပ်ဆင်ရပါ။\n" +
                                        "၃။ မြန်မာနိုင်ငံ မီးအားအတက်အကျများသဖြင့် TOMZN Over/Under Voltage Protector ကို အင်ဗာတာအဝင်တွင် မဖြစ်မနေ တပ်ဆင်ပါ။"
                            else
                                "1. NEVER substitute AC circuit breakers for DC solar arrays! DC arcs do not cross zero and will ignite fires.\n" +
                                        "2. Battery MCCB must have >= 20-25kA AIC breaking capacity to survive LiFePO4 dead-short faults.\n" +
                                        "3. Due to extreme grid fluctuations in Myanmar (160V-270V), a digital V/A protector is mandatory.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text(if (language == Language.MY) "အားလုံး (${switchgearList.size})" else "All (${switchgearList.size})", fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_all")
                )
                FilterChip(
                    selected = selectedCategoryFilter == SwitchgearCategory.DC_SOLAR,
                    onClick = { selectedCategoryFilter = SwitchgearCategory.DC_SOLAR },
                    label = { Text("DC Solar", fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_dc_solar")
                )
                FilterChip(
                    selected = selectedCategoryFilter == SwitchgearCategory.DC_BATTERY,
                    onClick = { selectedCategoryFilter = SwitchgearCategory.DC_BATTERY },
                    label = { Text("DC Battery", fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_dc_battery")
                )
                FilterChip(
                    selected = selectedCategoryFilter == SwitchgearCategory.AC_GRID,
                    onClick = { selectedCategoryFilter = SwitchgearCategory.AC_GRID },
                    label = { Text("AC Grid", fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_ac_grid")
                )
            }
        }

        // Switchgear Items
        items(filteredList, key = { it.code }) { item ->
            SwitchgearItemCard(
                item = item,
                language = language
            )
        }
    }
}
