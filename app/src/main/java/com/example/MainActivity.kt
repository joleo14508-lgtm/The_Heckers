package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AgrigradeViewModel
import com.example.ui.AppScreen
import com.example.ui.screens.AnalysisResultScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BatchHistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProcessingScreen
import com.example.ui.screens.QualityReportScreen
import com.example.ui.screens.SmartCaptureScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.AgrigradeTheme
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

class MainActivity : ComponentActivity() {

    private val viewModel: AgrigradeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AgrigradeTheme {
                AgrigradeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AgrigradeApp(viewModel: AgrigradeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val inspections by viewModel.inspections.collectAsStateWithLifecycle()
    val activeLot by viewModel.activeLotNumber.collectAsStateWithLifecycle()
    val procurementCenter by viewModel.procurementCenter.collectAsStateWithLifecycle()
    val processingState by viewModel.processingState.collectAsStateWithLifecycle()
    val currentRecord by viewModel.currentInspectedRecord.collectAsStateWithLifecycle()
    val currentBitmap by viewModel.currentImageBitmap.collectAsStateWithLifecycle()
    val selectedReport by viewModel.selectedReport.collectAsStateWithLifecycle()

    // Handle Android system back gestures appropriately
    BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.SPLASH) {
        when (currentScreen) {
            AppScreen.QUALITY_REPORT -> viewModel.navigateTo(AppScreen.ANALYSIS_RESULT)
            AppScreen.ANALYSIS_RESULT -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.SMART_CAPTURE -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.PROCESSING -> viewModel.navigateTo(AppScreen.SMART_CAPTURE)
            AppScreen.ANALYTICS -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.BATCH_HISTORY -> viewModel.navigateTo(AppScreen.HOME)
            else -> viewModel.navigateTo(AppScreen.HOME)
        }
    }

    val showBottomBar = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.ANALYTICS,
        AppScreen.BATCH_HISTORY
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = LightLayer,
                    contentColor = TextDark,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        },
                        label = {
                            Text(
                                "Home",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ButtonGreen,
                            selectedTextColor = ButtonGreen,
                            indicatorColor = SagePanel.copy(alpha = 0.6f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SMART_CAPTURE,
                        onClick = { viewModel.navigateTo(AppScreen.SMART_CAPTURE) },
                        icon = {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Capture")
                        },
                        label = {
                            Text(
                                "Capture",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == AppScreen.SMART_CAPTURE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ButtonGreen,
                            selectedTextColor = ButtonGreen,
                            indicatorColor = SagePanel.copy(alpha = 0.6f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_capture")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.BATCH_HISTORY,
                        onClick = { viewModel.navigateTo(AppScreen.BATCH_HISTORY) },
                        icon = {
                            Icon(Icons.Default.Inventory2, contentDescription = "History")
                        },
                        label = {
                            Text(
                                "History",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == AppScreen.BATCH_HISTORY) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ButtonGreen,
                            selectedTextColor = ButtonGreen,
                            indicatorColor = SagePanel.copy(alpha = 0.6f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_history")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.ANALYTICS,
                        onClick = { viewModel.navigateTo(AppScreen.ANALYTICS) },
                        icon = {
                            Icon(Icons.Default.BarChart, contentDescription = "Analytics")
                        },
                        label = {
                            Text(
                                "Analytics",
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == AppScreen.ANALYTICS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ButtonGreen,
                            selectedTextColor = ButtonGreen,
                            indicatorColor = SagePanel.copy(alpha = 0.6f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_analytics")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == AppScreen.HOME || currentScreen == AppScreen.BATCH_HISTORY) {
                FloatingActionButton(
                    onClick = { viewModel.navigateTo(AppScreen.SMART_CAPTURE) },
                    containerColor = ButtonGreen,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_smart_capture")
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = "Quick Scan Onion",
                        modifier = Modifier.size(26.dp)
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
            when (currentScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onGetStarted = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.HOME -> {
                    HomeScreen(
                        activeLot = activeLot,
                        procurementCenter = procurementCenter,
                        recentInspections = inspections,
                        onStartCameraCapture = { viewModel.navigateTo(AppScreen.SMART_CAPTURE) },
                        onImageSelected = { bitmap -> viewModel.processCapturedImage(bitmap) },
                        onLoadSample = { sampleType -> viewModel.loadSampleOnion(sampleType) },
                        onSelectInspection = { record ->
                            viewModel.viewReport(record)
                        },
                        onViewAllBatches = { viewModel.navigateTo(AppScreen.BATCH_HISTORY) }
                    )
                }

                AppScreen.SMART_CAPTURE -> {
                    SmartCaptureScreen(
                        onImageCaptured = { bitmap -> viewModel.processCapturedImage(bitmap) },
                        onBack = { viewModel.navigateTo(AppScreen.HOME) },
                        onLoadSample = { sampleType -> viewModel.loadSampleOnion(sampleType) }
                    )
                }

                AppScreen.PROCESSING -> {
                    ProcessingScreen(
                        processingState = processingState,
                        scannedBitmap = currentBitmap
                    )
                }

                AppScreen.ANALYSIS_RESULT -> {
                    AnalysisResultScreen(
                        record = currentRecord,
                        currentBitmap = currentBitmap,
                        onVerifyConfirm = { finalGrade, notes ->
                            currentRecord?.let { viewModel.verifyAndConfirmGrade(it, finalGrade, notes) }
                        },
                        onViewReport = { record ->
                            viewModel.viewReport(record)
                        },
                        onScanAgain = { viewModel.navigateTo(AppScreen.SMART_CAPTURE) },
                        onBack = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.ANALYTICS -> {
                    AnalyticsScreen(
                        inspections = inspections
                    )
                }

                AppScreen.QUALITY_REPORT -> {
                    QualityReportScreen(
                        record = selectedReport ?: currentRecord,
                        onUpdateReport = { updatedRecord ->
                            viewModel.updateFullInspectionRecord(updatedRecord)
                        },
                        onBack = { viewModel.navigateTo(AppScreen.ANALYSIS_RESULT) }
                    )
                }

                AppScreen.BATCH_HISTORY -> {
                    BatchHistoryScreen(
                        inspections = inspections,
                        onSelectInspection = { record ->
                            viewModel.viewReport(record)
                        },
                        onDeleteInspection = { id ->
                            viewModel.deleteRecord(id)
                        },
                        onBack = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }
            }
        }
    }
}
