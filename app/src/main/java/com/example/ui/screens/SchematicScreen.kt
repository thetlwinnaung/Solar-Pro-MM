package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.SolarAppViewModel
import com.example.ui.components.SingleLineDiagramView

@Composable
fun SchematicScreen(
    viewModel: SolarAppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val sizing = viewModel.getCurrentSizing()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("schematic_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (language == Language.MY) "စနစ်ပုံကြမ်းနှင့် အကာအကွယ်အစီအစဉ်" else "Single-Line Diagram (SLD) & Protection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (language == Language.MY) "IEC 60364-7-712 စံနှုန်းအတိုင်း ဖွဲ့စည်းထားသော လျှပ်စစ်လိုင်းများ" else "IEC 60364-7-712 Compliant Electrical Architecture",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            OutlinedButton(
                onClick = {
                    val report = viewModel.generateBomReportText()
                    clipboardManager.setText(AnnotatedString(report))
                },
                modifier = Modifier.testTag("btn_copy_sld_report")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (language == Language.MY) "အစီရင်ခံစာ ကူးရန်" else "Copy Report", fontSize = 12.sp)
            }
        }

        // The Interactive SLD
        SingleLineDiagramView(
            sizing = sizing,
            language = language
        )

        // Electrical Engineering Installation Guidelines Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (language == Language.MY) "အင်ဂျင်နီယာ တပ်ဆင်ရေး လမ်းညွှန်ချက်များ:" else "Engineering Field Installation Rules:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (language == Language.MY)
                        "၁။ DC Cable နှင့် AC Cable များကို Conduit (ပိုက်) တစ်ခုတည်းအတွင်း အတူတကွ မပြေးရပါ။ သီးခြားစီ ခွဲထားရပါမည်။\n" +
                                "၂။ ဘက်ထရီကေဘယ်ကို ၂ မီတာထက် ပိုမရှည်စေရပါ။ ဗို့အားကျဆင်းမှု ၁% အောက် နည်းပါးရပါမည်။\n" +
                                "၃။ ဆိုလာပြားဘောင်များ၊ အင်ဗာတာနှင့် DC/AC SPD မြေစိုက်လိုင်းများအားလုံးကို Equipotential Earth Bar တွင် စုစည်းပြီး မြေစိုက်တိုင်သို့ တိုက်ရိုက်ချိတ်ဆက်ပါ။\n" +
                                "၄။ Battery BMS CAN/RS485 ဆက်သွယ်ရေးကြိုးကို Inverter BMS Port သို့ Closed-Loop ချိတ်ဆက်ပြီး Lithium Battery Protocol (e.g. Pylontech / Deye Protocol) သတ်မှတ်ပါ။"
                    else
                        "1. Segregate DC and AC cabling into independent non-metallic conduit runs to avoid induced harmonic interference.\n" +
                                "2. Maintain battery cables under 2.0 meters to ensure DC voltage drop remains strictly below 1.0% (<0.51V).\n" +
                                "3. Ground all metallic module frames, inverter enclosure, and SPD ground leads to a single equipotential earthing bar.\n" +
                                "4. Establish closed-loop CAN/RS485 communication between LiFePO4 BMS and hybrid inverter for precise SOC/temperature monitoring.",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
