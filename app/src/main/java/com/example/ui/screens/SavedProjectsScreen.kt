package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SavedProject
import com.example.model.Language
import com.example.ui.SolarAppViewModel
import com.example.ui.theme.EcoGreen
import com.example.ui.theme.SolarAmber
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedProjectsScreen(
    viewModel: SolarAppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val projects by viewModel.savedProjects.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var showSaveDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("saved_projects_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Actions Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.MY) "သိမ်းဆည်းထားသော ပရောဂျက်များနှင့် အစီရင်ခံစာ" else "Saved Client Proposals & BOM Schedules",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == Language.MY) "ဆိုလာစနစ် တွက်ချက်မှုများကို သိမ်းဆည်းပြီး ဖုန်းထဲတွင် ပြန်လည်ကြည့်ရှုနိုင်သည်" else "Persistent client project quotations stored in local Room database",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier.weight(1f).testTag("btn_open_save_dialog")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == Language.MY) "သိမ်းဆည်းရန်" else "Save Current")
                        }

                        OutlinedButton(
                            onClick = {
                                val report = viewModel.generateBomReportText()
                                clipboardManager.setText(AnnotatedString(report))
                            },
                            modifier = Modifier.weight(1f).testTag("btn_copy_bom_report")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == Language.MY) "BOM ကူးယူရန်" else "Copy BOM")
                        }
                    }
                }
            }
        }

        // Projects List Header
        item {
            Text(
                text = if (language == Language.MY) "သိမ်းဆည်းထားသည့် ပရောဂျက်များ (${projects.size})" else "Saved Projects (${projects.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (projects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.MY) "သိမ်းဆည်းထားသော ပရောဂျက် မရှိသေးပါ" else "No saved client proposals yet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (language == Language.MY) "လက်ရှိ တွက်ချက်မှုကို 'သိမ်းဆည်းရန်' ခလုတ်ဖြင့် သိမ်းဆည်းပါ" else "Tap 'Save Current' above to save your calculation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            items(projects, key = { it.id }) { proj ->
                SavedProjectCard(
                    project = proj,
                    language = language,
                    onDelete = { viewModel.deleteProject(proj) }
                )
            }
        }
    }

    if (showSaveDialog) {
        var title by remember { mutableStateOf("") }
        var client by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(if (language == Language.MY) "ပရောဂျက် သိမ်းဆည်းရန်" else "Save Project Proposal")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Project Title (e.g. Mandalay Home 5kW)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_save_title")
                    )

                    OutlinedTextField(
                        value = client,
                        onValueChange = { client = it },
                        label = { Text("Client Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_save_client")
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Address") },
                        modifier = Modifier.fillMaxWidth().testTag("input_save_notes")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveProject(title, client, notes)
                        showSaveDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_save_project")
                ) {
                    Text(if (language == Language.MY) "သိမ်းမည်" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text(if (language == Language.MY) "မလုပ်တော့ပါ" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SavedProjectCard(
    project: SavedProject,
    language: Language,
    onDelete: () -> Unit
) {
    val dateStr = remember(project.timestamp) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.format(Date(project.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_project_${project.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Client: ${project.clientName} | $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Text(
                        text = "${project.inverterKw} kW Inverter",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Surface(shape = RoundedCornerShape(6.dp), color = SolarAmber.copy(alpha = 0.2f)) {
                    Text(
                        text = "${String.format("%.2f", project.pvKwp)} kWp PV",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SolarAmber
                    )
                }

                Surface(shape = RoundedCornerShape(6.dp), color = EcoGreen.copy(alpha = 0.2f)) {
                    Text(
                        text = "${String.format("%.1f", project.batteryKwh)} kWh LiFePO4",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Savings: ${String.format("%,.0f", project.monthlySavingsMmk)} MMK/mo | Payback: ${String.format("%.1f", project.paybackYears)} yrs",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            if (project.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${project.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}
