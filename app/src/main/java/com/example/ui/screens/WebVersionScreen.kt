package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.example.model.Language
import com.example.ui.SolarAppViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EcoGreen
import com.example.ui.theme.SolarAmber
import java.io.File
import java.io.FileOutputStream

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebVersionScreen(
    viewModel: SolarAppViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("web_version_screen")
    ) {
        // Cross-platform Header Info Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == Language.MY) "မိုဘိုင်း ဝဘ်ဗားရှင်း (iOS & Android သဟဇာတ)" else "Mobile Web / PWA (iOS & Android Compatible)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == Language.MY) "iPhone (Safari) နှင့် Android (Chrome) နှစ်မျိုးလုံးတွင် သုံးနိုင်သည်" else "Works seamlessly in Safari on iOS & Chrome on Android",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Reload Web App
                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share Standalone Web App for iPhone (iOS) & Android Users
                    FilledTonalButton(
                        onClick = { shareStandaloneWebFile(context) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_share_web_app")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == Language.MY) "iOS အတွက် ဝေမျှရန်" else "Share for iOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Embedded Android WebView running the bundled HTML5/CSS3/PWA application
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("web_view_component"),
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        allowFileAccess = true
                        allowContentAccess = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    loadUrl("file:///android_asset/web/index.html")
                    webViewInstance = this
                }
            },
            update = { view ->
                webViewInstance = view
            }
        )
    }
}

/**
 * Copies the bundled standalone single-file HTML web application to the app cache
 * and launches an Android Share Intent. This allows users to send the complete
 * interactive calculator to iPhone (iOS) users who can open it in Safari without installing an APK!
 */
private fun shareStandaloneWebFile(context: Context) {
    try {
        val assetManager = context.assets
        val inputStream = assetManager.open("web/index.html")
        val cacheFile = File(context.cacheDir, "SolarPro_MM_Web_Calculator.html")
        val outputStream = FileOutputStream(cacheFile)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            cacheFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SolarPro MM - Solar PV Sizer (iOS & Android Compatible)")
            putExtra(
                Intent.EXTRA_TEXT,
                "Here is the Solar PV & Protection Sizer web app (compatible with iPhone Safari & Android Chrome). Open the attached HTML file in your mobile browser to run offline!"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share SolarPro Web App for iOS / Android"))
    } catch (e: Exception) {
        // Fallback: share plain text link or description
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "SolarPro MM - Solar PV Sizer Web Version")
            putExtra(
                Intent.EXTRA_TEXT,
                "SolarPro MM Solar PV & Protection Sizer (Myanmar MOEP Tariffs & IEC 60364-7-712). Works on iOS and Android!"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share SolarPro MM"))
    }
}
