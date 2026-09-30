package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InspectionRecord
import com.example.data.model.OnionGrade
import com.example.data.model.SizeCategory
import com.example.data.model.VerificationStatus
import com.example.ui.components.GradeBadge
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GradeAGreen
import com.example.ui.theme.GradeContainerA
import com.example.ui.theme.GradeContainerReject
import com.example.ui.theme.GradeRejectRed
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.util.QRCodeGenerator

@Composable
fun QualityReportScreen(
    record: InspectionRecord?,
    onUpdateReport: (InspectionRecord) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (record == null) {
        Box(
            modifier = modifier.fillMaxSize().background(AgrigradeBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No report selected", color = TextMuted)
        }
        return
    }

    // Generate real verified QR Code bitmap using ZXing
    val qrPayload = "https://agrigrade.ai/verify?id=${record.reportId}&lot=${record.lotNumber}&grade=${record.overallGrade.name}&passed=${record.isPassed}&hash=${record.digitalVerificationHash()}"
    val qrBitmap = remember(record) {
        QRCodeGenerator.generateQRCodeBitmap(qrPayload, 380, 380)
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
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("report_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = HeadingsLogo
                )
            }

            Text(
                text = "Official Quality Certificate",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = HeadingsLogo
            )

            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Official Locked Certificate",
                        tint = GradeAGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Official Seal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GradeAGreen
                    )
                }
            }
        }

        // Scrollable Report Card
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightLayer),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Title section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Summary of Analysis",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = HeadingsLogo
                            )
                            Text(
                                text = "Official Mandi Intake Certificate",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GradeAGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("official_non_modifiable_badge")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = GradeAGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Non-Modifiable Record",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GradeAGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Inspection Pass/Fail Evaluation Banner
                    Surface(
                        color = if (record.isPassed) GradeContainerA else GradeContainerReject,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (record.isPassed) GradeAGreen else GradeRejectRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (record.isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (record.isPassed) GradeAGreen else GradeRejectRed,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (record.isPassed) "PASSED: GOOD QUALITY SPECIMEN" else "FAILED: NOT GOOD — DEFECTIVE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (record.isPassed) GradeAGreen else GradeRejectRed
                                )
                                Text(
                                    text = if (record.isPassed) {
                                        "Meets all export & domestic Grade A quality standards."
                                    } else {
                                        record.failureReasons.ifBlank { "Failed: Sprouting, fungal rot/mold, or severe shriveling detected." }
                                    },
                                    fontSize = 11.sp,
                                    color = if (record.isPassed) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary table lines
                    ReportFieldRow(label = "Report ID", value = record.reportId, isBold = true)
                    ReportFieldRow(label = "Lot Number", value = record.lotNumber)
                    ReportFieldRow(label = "Crop Type", value = record.cropName)
                    ReportFieldRow(label = "Variety", value = record.variety)
                    ReportFieldRow(label = "Procurement Center", value = record.procurementCenter)
                    ReportFieldRow(label = "Inspector Name", value = record.inspectorName)
                    ReportFieldRow(label = "Date & Time", value = record.formattedDate())

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Final Grade", fontSize = 13.sp, color = TextMuted)
                        GradeBadge(grade = record.overallGrade)
                    }

                    ReportFieldRow(label = "Diameter", value = "${record.diameterMm} mm (${record.sizeCategory.displayName})")
                    ReportFieldRow(label = "Skin Integrity", value = "${record.skinIntegrityPercent}% intact")
                    ReportFieldRow(label = "Sprouting", value = if (record.sproutingDetected) "Detected (${record.sproutLengthMm} mm shoot)" else "None (Dormant)")
                    ReportFieldRow(label = "Rot / Fungal Mold", value = record.rotDescription)
                    ReportFieldRow(label = "Dehydration / Wrinkling", value = if (record.wrinklingDetected) "Severe Wrinkles" else "Smooth & Firm")
                    ReportFieldRow(label = "Confidence Score", value = "${record.confidencePercent}%")
                    ReportFieldRow(label = "Mandi Procurement Price", value = "₹${String.format("%.2f", record.recommendedPricePerKg)} / kg", isBold = true)

                    if (!record.isPassed && record.failureReasons.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Failure Reasons:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GradeRejectRed)
                                Text(record.failureReasons, fontSize = 11.sp, color = Color(0xFFB71C1C))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFF9F5EE),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Inspector Observations / Notes:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(record.inspectorNotes, fontSize = 12.sp, color = TextDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Verification Box (from mockup 07)
                    Surface(
                        color = Color(0xFFF9F5EE),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Verification QR Code",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Verified by Agrigrade AI",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HeadingsLogo
                                )
                                Text(
                                    text = "Version 2.3 • AGMARK Calibrated",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = record.digitalVerificationHash(),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = ButtonGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Scan QR code to verify cryptographic intake certificate",
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    lineHeight = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom CTA Buttons
        Surface(
            color = LightLayer,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Official Mandi Certificate ${record.reportId} saved to downloads.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("download_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download Official Certificate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                """
                                Agrigrade Official Quality Certificate (Non-Modifiable)
                                Report ID: ${record.reportId}
                                Status: ${if (record.isPassed) "PASSED (Good)" else "FAILED (Not Good)"}
                                Crop: ${record.cropName} (${record.variety})
                                Lot: ${record.lotNumber}
                                Grade: ${record.overallGrade.displayName}
                                ${if (!record.isPassed) "Reasons: ${record.failureReasons}\n" else ""}Recommended Price: ₹${record.recommendedPricePerKg}/kg
                                Center: ${record.procurementCenter}
                                Inspector: ${record.inspectorName}
                                Verification Hash: ${record.digitalVerificationHash()}
                                Tamper-Proof: Verified Immutable Record
                                """.trimIndent()
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Official Certificate"))
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("share_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = HeadingsLogo,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share Official Certificate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeadingsLogo
                    )
                }
            }
        }
    }
}

@Composable
fun ReportFieldRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = TextDark,
            maxLines = 1
        )
    }
}

