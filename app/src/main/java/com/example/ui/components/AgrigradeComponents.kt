package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OnionGrade
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GradeAGreen
import com.example.ui.theme.GradeBOrange
import com.example.ui.theme.GradeContainerA
import com.example.ui.theme.GradeContainerB
import com.example.ui.theme.GradeContainerReject
import com.example.ui.theme.GradeRejectRed
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun GradeBadge(
    grade: OnionGrade,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val (bgColor, textColor, label) = when (grade) {
        OnionGrade.GRADE_A -> Triple(GradeContainerA, GradeAGreen, if (large) "Grade A — Premium Fresh" else "Grade A")
        OnionGrade.GRADE_B -> Triple(GradeContainerB, GradeBOrange, if (large) "Grade B — Domestic Market" else "Grade B")
        OnionGrade.REJECT -> Triple(GradeContainerReject, GradeRejectRed, if (large) "Reject / Cull" else "Reject")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
        modifier = modifier.testTag("grade_badge_${grade.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = if (large) 16.dp else 10.dp, vertical = if (large) 8.dp else 4.dp)
        ) {
            Icon(
                imageVector = when (grade) {
                    OnionGrade.GRADE_A -> Icons.Default.Verified
                    OnionGrade.GRADE_B -> Icons.Default.Warning
                    OnionGrade.REJECT -> Icons.Default.Close
                },
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(if (large) 18.dp else 14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (large) 14.sp else 12.sp
            )
        }
    }
}

@Composable
fun MetricProgressBar(
    label: String,
    valueText: String,
    progress: Float,
    barColor: Color = ButtonGreen,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "metric_anim")

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
            Text(
                text = valueText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE6DEC8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun ReticleViewfinder(
    modifier: Modifier = Modifier,
    reticleColor: Color = LeafGreen,
    bracketLength: Dp = 48.dp,
    strokeWidth: Dp = 4.dp
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val sw = strokeWidth.toPx()
        val bl = bracketLength.toPx()
        val pad = 40.dp.toPx()

        val left = pad
        val top = pad
        val right = size.width - pad
        val bottom = size.height - pad

        // Top-left bracket
        drawLine(reticleColor, Offset(left, top), Offset(left + bl, top), sw, StrokeCap.Round)
        drawLine(reticleColor, Offset(left, top), Offset(left, top + bl), sw, StrokeCap.Round)

        // Top-right bracket
        drawLine(reticleColor, Offset(right, top), Offset(right - bl, top), sw, StrokeCap.Round)
        drawLine(reticleColor, Offset(right, top), Offset(right, top + bl), sw, StrokeCap.Round)

        // Bottom-left bracket
        drawLine(reticleColor, Offset(left, bottom), Offset(left + bl, bottom), sw, StrokeCap.Round)
        drawLine(reticleColor, Offset(left, bottom), Offset(left, bottom - bl), sw, StrokeCap.Round)

        // Bottom-right bracket
        drawLine(reticleColor, Offset(right, bottom), Offset(right - bl, bottom), sw, StrokeCap.Round)
        drawLine(reticleColor, Offset(right, bottom), Offset(right, bottom - bl), sw, StrokeCap.Round)

        // Subtle center crosshair dot
        val cx = size.width / 2f
        val cy = size.height / 2f
        drawCircle(reticleColor.copy(alpha = 0.6f), radius = 4.dp.toPx(), center = Offset(cx, cy))
    }
}

@Composable
fun DonutFreshRottenChart(
    freshPercent: Int,
    rottenPercent: Int,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(140.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Butt)
            val diameter = size.minDimension - stroke.width
            val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
            val arcSize = Size(diameter, diameter)

            val freshSweep = (freshPercent / 100f) * 360f
            val rottenSweep = (rottenPercent / 100f) * 360f

            // Fresh arc (Green)
            drawArc(
                color = LeafGreen,
                startAngle = -90f,
                sweepAngle = freshSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            // Rotten / Reject arc (Red)
            drawArc(
                color = GradeRejectRed,
                startAngle = -90f + freshSweep,
                sweepAngle = rottenSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$freshPercent%",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HeadingsLogo
            )
            Text(
                text = "Fresh",
                fontSize = 11.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun GradeDistributionBars(
    gradeACount: Int,
    gradeBCount: Int,
    rejectCount: Int,
    modifier: Modifier = Modifier
) {
    val total = (gradeACount + gradeBCount + rejectCount).coerceAtLeast(1)
    val maxCount = maxOf(gradeACount, gradeBCount, rejectCount).coerceAtLeast(1)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        BarItem(label = "A", count = gradeACount, percent = (gradeACount * 100) / total, max = maxCount, color = ButtonGreen)
        BarItem(label = "B", count = gradeBCount, percent = (gradeBCount * 100) / total, max = maxCount, color = LeafGreen)
        BarItem(label = "Reject", count = rejectCount, percent = (rejectCount * 100) / total, max = maxCount, color = SagePanel)
    }
}

@Composable
private fun BarItem(
    label: String,
    count: Int,
    percent: Int,
    max: Int,
    color: Color
) {
    val heightRatio = (count.toFloat() / max).coerceIn(0.15f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(
            text = "$count",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = HeadingsLogo
        )
        Text(
            text = "$percent%",
            fontSize = 10.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight(heightRatio * 0.7f)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
}

@Composable
fun HumanVerificationDialog(
    initialGrade: OnionGrade,
    currentNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (OnionGrade, String) -> Unit
) {
    var selectedGrade by remember { mutableStateOf(initialGrade) }
    var notes by remember { mutableStateOf(currentNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = ButtonGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inspector Verification", fontWeight = FontWeight.Bold, color = HeadingsLogo)
            }
        },
        text = {
            Column {
                Text(
                    "Standardized grading audit: Confirm or override AI assessment according to Mandi procurement criteria.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Procurement Grade:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))

                OnionGrade.values().forEach { grade ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGrade = grade }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedGrade == grade),
                            onClick = { selectedGrade = grade },
                            colors = RadioButtonDefaults.colors(selectedColor = ButtonGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(grade.displayName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
                            Text(grade.description, fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Inspector Audit Notes") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonGreen,
                        unfocusedBorderColor = CardBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedGrade, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                modifier = Modifier.testTag("confirm_verification_button")
            ) {
                Text("Confirm Grade")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextDark)
            }
        },
        containerColor = LightLayer
    )
}
