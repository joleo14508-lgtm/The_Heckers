package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.InspectionRecord
import com.example.ui.SampleOnionType
import com.example.ui.components.GradeBadge
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.HillGreen
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.SurfaceCream
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    activeLot: String,
    procurementCenter: String,
    recentInspections: List<InspectionRecord>,
    onStartCameraCapture: () -> Unit,
    onImageSelected: (android.graphics.Bitmap) -> Unit,
    onLoadSample: (SampleOnionType) -> Unit,
    onSelectInspection: (InspectionRecord) -> Unit,
    onViewAllBatches: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        onImageSelected(bitmap)
                    }
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        onImageSelected(bitmap)
                    }
                }
            }
        }
    }

    fun launchImagePicker() {
        try {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (_: Exception) {
            imagePickerLauncher.launch("image/*")
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AgrigradeBackground)
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Row (Logo, Title, and Center status badge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.agrigrade_logo_1790772973744),
                        contentDescription = "Agrigrade Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Agrigrade",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HeadingsLogo
                    )
                }

                Surface(
                    color = ButtonGreen,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = "Center #4",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Greeting Section (from mockup)
            Text(
                text = "Welcome to Agrigrade",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = HeadingsLogo
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start your grading journey. Upload or capture an onion.",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Procurement Lot Banner
            Surface(
                color = SurfaceCream,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SagePanel.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = HeadingsLogo
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Active Lot: ",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = activeLot,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                        Text(
                            text = procurementCenter,
                            fontSize = 11.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Card 1: Capture Image (mockup design 02)
            Card(
                onClick = onStartCameraCapture,
                colors = CardDefaults.cardColors(containerColor = LightLayer),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capture_image_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = ButtonGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Capture Image",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeadingsLogo
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Open camera viewfinder with smart reticle",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Card 2: Upload Image (mockup design 02)
            Card(
                onClick = { launchImagePicker() },
                colors = CardDefaults.cardColors(containerColor = LightLayer),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_image_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F8E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            tint = ButtonGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Upload Image",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeadingsLogo
                    )
                    Text(
                        text = "JPG, PNG potato / onion sample photo",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = ButtonGreen,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { launchImagePicker() }
                    ) {
                        Text(
                            text = "Choose Photo",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Test Demo Samples Selector (for instant assessment testing in browser/emulator)
            Surface(
                color = LightLayer,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = ButtonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Instant Mandi Grading Test Presets",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeadingsLogo
                        )
                    }
                    Text(
                        text = "Select a simulated lot specimen to verify grading criteria:",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onLoadSample(SampleOnionType.GOOD_POTATO_GRADE_A) },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Good (Grade A)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Passed • 0% Defect", fontSize = 8.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }
                        Button(
                            onClick = { onLoadSample(SampleOnionType.FAILED_POTATO_SPROUTED) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF9A9A)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Failed (Reject)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF880E4F))
                                Text("Sprouted & Mold", fontSize = 8.sp, color = Color(0xFF880E4F).copy(alpha = 0.85f))
                            }
                        }
                        Button(
                            onClick = { onLoadSample(SampleOnionType.FRESH_GRADE_A_ONION) },
                            colors = ButtonDefaults.buttonColors(containerColor = HillGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Grade A Onion", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Fresh Nashik Red", fontSize = 8.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }
                        Button(
                            onClick = { onLoadSample(SampleOnionType.REJECT_ONION_MOLD) },
                            colors = ButtonDefaults.buttonColors(containerColor = SagePanel),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Reject Onion", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HeadingsLogo)
                                Text("Black Mold Rot", fontSize = 8.sp, color = HeadingsLogo.copy(alpha = 0.85f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Prediction Result Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Prediction Results",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeadingsLogo
                )
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ButtonGreen,
                    modifier = Modifier.clickable { onViewAllBatches() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Recent items list
        if (recentInspections.isEmpty()) {
            item {
                Surface(
                    color = LightLayer,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No inspection records yet",
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Text(
                            text = "Capture or upload an onion photo to start.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(recentInspections.take(4)) { item ->
                Card(
                    onClick = { onSelectInspection(item) },
                    colors = CardDefaults.cardColors(containerColor = LightLayer),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.reportId,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = HeadingsLogo
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                GradeBadge(grade = item.overallGrade)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${item.variety} • ⌀${item.diameterMm}mm • ${item.confidencePercent}% conf",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = item.formattedDate(),
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View",
                            tint = ButtonGreen
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
