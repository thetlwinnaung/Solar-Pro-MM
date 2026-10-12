package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Language
import com.example.ui.SolarAppViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SolarAmber
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SolarAppRoot()
            }
        }
    }
}

data class NavTabItem(
    val titleEn: String,
    val titleMy: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarAppRoot(
    viewModel: SolarAppViewModel = viewModel()
) {
    val language by viewModel.language.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // Observe user message from viewModel
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Android back handler: pop to Tab 0 if on any other tab
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    val tabs = remember {
        listOf(
            NavTabItem(titleEn = "Sizing", titleMy = "အရွယ်အစား", icon = Icons.Default.WbSunny, tag = "tab_sizing"),
            NavTabItem(titleEn = "Protection", titleMy = "ဘရိတ်ကာ", icon = Icons.Default.Shield, tag = "tab_protection"),
            NavTabItem(titleEn = "Cables", titleMy = "ကေဘယ်", icon = Icons.Default.Cable, tag = "tab_cables"),
            NavTabItem(titleEn = "Tariff ROI", titleMy = "မီတာခ", icon = Icons.Default.Savings, tag = "tab_tariff"),
            NavTabItem(titleEn = "SLD", titleMy = "ပုံကြမ်း", icon = Icons.Default.AccountTree, tag = "tab_sld"),
            NavTabItem(titleEn = "Projects", titleMy = "ပရောဂျက်", icon = Icons.Default.Folder, tag = "tab_projects"),
            NavTabItem(titleEn = "Web (iOS)", titleMy = "ဝဘ် (iOS)", icon = Icons.Default.Language, tag = "tab_web")
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SolarAmber.copy(alpha = 0.2f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = SolarAmber,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "SolarPro MM",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (language == Language.MY) "ဆိုလာ PV & IEC အကာအကွယ် ဒီဇိုင်း" else "Solar PV & Protection Design (Mandalay)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Quick Toggle for Web Mode (iOS & Android Cross-Platform)
                    IconButton(
                        onClick = { viewModel.setTab(6) },
                        modifier = Modifier.testTag("btn_top_web_mode")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Web Version",
                            tint = if (currentTab == 6) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Quick Copy Report
                    IconButton(
                        onClick = {
                            val rep = viewModel.generateBomReportText()
                            clipboardManager.setText(AnnotatedString(rep))
                            scope.launch {
                                snackbarHostState.showSnackbar("Engineering Schedule copied to clipboard!")
                            }
                        },
                        modifier = Modifier.testTag("btn_top_copy_report")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Schedule"
                        )
                    }

                    // Language Toggle Button (EN / မြန်မာ)
                    FilledTonalButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("btn_language_toggle"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (language == Language.EN) "မြန်မာ" else "EN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { viewModel.setTab(index) },
                        icon = {
                            Icon(imageVector = tab.icon, contentDescription = tab.titleEn)
                        },
                        label = {
                            Text(
                                text = if (language == Language.MY) tab.titleMy else tab.titleEn,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> LoadSizerScreen(viewModel = viewModel)
                1 -> SwitchgearProtectionScreen(viewModel = viewModel)
                2 -> CableGroundingScreen(viewModel = viewModel)
                3 -> TariffRoiScreen(viewModel = viewModel)
                4 -> SchematicScreen(viewModel = viewModel)
                5 -> SavedProjectsScreen(viewModel = viewModel)
                6 -> WebVersionScreen(viewModel = viewModel)
            }
        }
    }
}
