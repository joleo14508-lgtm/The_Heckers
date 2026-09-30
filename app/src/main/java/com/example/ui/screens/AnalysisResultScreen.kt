package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.InspectionRecord
import com.example.data.model.OnionGrade
import com.example.data.model.VerificationStatus
import com.example.ui.components.GradeBadge
import com.example.ui.components.MetricProgressBar
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GradeAGreen
import com.example.ui.theme.GradeBOrange
import com.example.ui.theme.GradeContainerA
import com.example.ui.theme.GradeContainerReject
import com.example.ui.theme.GradeRejectRed
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.io.File

@Composable
fun AnalysisResultScreen(
    record: InspectionRecord?,
    currentBitmap: Bitmap?,
    onVerifyConfirm: (OnionGrade, String) -> Unit,
    onViewReport: (InspectionRecord) -> Unit,
    onScanAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (record == null) {
        Box(
            modifier = modifier.fillMaxSize().background(AgrigradeBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No inspection data available", color = TextMuted)
        }
        return
    }

    val displayBitmap = remember(record) {
        currentBitmap ?: record.imagePath?.let { path ->
            val file = File(path)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgrigradeBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = HeadingsLogo
                )
            }

            Text(
                text = "Analysis Result",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HeadingsLogo
            )

            // Verification status indicator
            Surface(
                color = if (record.verificationStatus == VerificationStatus.HUMAN_VERIFIED) Color(0xFFE8F5E9) else SagePanel,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = if (record.verificationStatus == VerificationStatus.HUMAN_VERIFIED) GradeAGreen else HeadingsLogo,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (record.verificationStatus == VerificationStatus.HUMAN_VERIFIED) "Verified" else "AI Graded",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.verificationStatus == VerificationStatus.HUMAN_VERIFIED) GradeAGreen else HeadingsLogo
                    )
                }
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Center Image Card (from mockup 05)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE0EDC7))
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (displayBitmap != null) {
                    Image(
                        bitmap = displayBitmap.asImageBitmap(),
                        contentDescription = "Analyzed specimen",
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.sample_onion_lot_1790773055908),
                        contentDescription = "Specimen",
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent Evaluation Banner: GOOD (PASSED) vs NOT GOOD (FAILED)
            Surface(
                color = if (record.isPassed) GradeContainerA else GradeContainerReject,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (record.isPassed) GradeAGreen else GradeRejectRed
                ),
                modifier = Modifier.fillMaxWidth().testTag("evaluation_banner")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (record.isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (record.isPassed) GradeAGreen else GradeRejectRed,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (record.isPassed) "STATUS: GOOD — GRADE A (PASSED)" else "STATUS: NOT GOOD — FAILED INSPECTION",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (record.isPassed) GradeAGreen else GradeRejectRed
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (record.isPassed) {
                                "Healthy sound produce. 0% sprouting, no fungal rot or surface defects."
                            } else {
                                record.failureReasons.ifBlank { "Failed: Active eye sprouting, mold colonization, or severe shriveling detected." }
                            },
                            fontSize = 12.sp,
                            color = if (record.isPassed) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Identification & Grade Card (from mockup 05)
            Surface(
                color = LightLayer,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.cropName,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HeadingsLogo
                            )
                            Text(
                                text = "${record.variety} • ${record.lotNumber}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        GradeBadge(grade = record.overallGrade, large = true)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confidence score progress bar (mockup 05)
                    MetricProgressBar(
                        label = "Quality Assessment Confidence",
                        valueText = "${record.confidencePercent}%",
                        progress = record.confidencePercent / 100f,
                        barColor = if (record.confidencePercent > 90) ButtonGreen else GradeBOrange
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mandi Recommended Rate
                    Surface(
                        color = Color(0xFFF9F5EE),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Standardized Procurement Rate",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "₹${String.format("%.2f", record.recommendedPricePerKg)} / kg",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (record.isPassed) ButtonGreen else GradeRejectRed
                                )
                            }
                            Text(
                                text = if (record.isPassed) "Procurement Approved" else "Reject / Cull",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (record.isPassed) GradeAGreen else GradeRejectRed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Defect & Morphological Analysis Breakdown
            Surface(
                color = LightLayer,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Physical Characteristics & Defect Checklist",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeadingsLogo
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DefectCheckRow(
                        title = "Sprouting / Shoot Emergence",
                        detected = record.sproutingDetected,
                        positiveNote = "Dormant eyes (0 mm sprout growth)",
                        negativeNote = "Active sprouting (${record.sproutLengthMm} mm green shoot) — toxic solanine risk"
                    )

                    DefectCheckRow(
                        title = "Rot & Fungal Mold (Blight/Aspergillus)",
                        detected = record.rotFungalDetected,
                        positiveNote = "0% fungal decay or mold detected",
                        negativeNote = record.rotDescription
                    )

                    DefectCheckRow(
                        title = "Skin Firmness & Wrinkling",
                        detected = record.wrinklingDetected || record.skinIntegrityPercent < 70,
                        positiveNote = "${record.skinIntegrityPercent}% sound firm skin coverage",
                        negativeNote = "Severe dehydration wrinkling & tissue softening"
                    )

                    DefectCheckRow(
                        title = "Bulb / Tuber Dimension",
                        detected = false,
                        positiveNote = "${record.diameterMm} mm (${record.sizeCategory.displayName} - ${record.sizeCategory.range})",
                        negativeNote = ""
                    )

                    DefectCheckRow(
                        title = "Curing & Internal Soundness",
                        detected = record.neckTightness.contains("Soft"),
                        positiveNote = record.neckTightness,
                        negativeNote = "Soft neck / internal water rot hazard"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inspector Observations
            Surface(
                color = Color(0xFFF9F5EE),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Inspection Findings & Diagnosis:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = record.inspectorNotes,
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Action Buttons Row: View Official Report, Sign & Seal, Scan Again
        Surface(
            color = LightLayer,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                // View Official Report (Primary button)
                Button(
                    onClick = { onViewReport(record) },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("view_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Official Report (Non-Modifiable)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Digital seal and lock certificate without modifying grade
                            onVerifyConfirm(record.overallGrade, "Officially verified and sealed by inspector: ${record.inspectorNotes}")
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("verify_grade_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = HeadingsLogo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (record.verificationStatus == VerificationStatus.HUMAN_VERIFIED) "Sealed & Locked" else "Digital Seal",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeadingsLogo
                        )
                    }

                    Button(
                        onClick = onScanAgain,
                        colors = ButtonDefaults.buttonColors(containerColor = SagePanel),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("scan_again_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = HeadingsLogo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scan Again",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HeadingsLogo
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefectCheckRow(
    title: String,
    detected: Boolean,
    positiveNote: String,
    negativeNote: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (!detected) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (!detected) GradeAGreen else GradeRejectRed,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Text(
                text = if (!detected) positiveNote else negativeNote,
                fontSize = 11.sp,
                color = if (!detected) TextMuted else GradeRejectRed
            )
        }
    }
}
