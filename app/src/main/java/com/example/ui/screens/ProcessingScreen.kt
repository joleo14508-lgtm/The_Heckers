package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.PipelineStage
import com.example.ui.ProcessingUiState
import com.example.ui.components.MetricProgressBar
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun ProcessingScreen(
    processingState: ProcessingUiState,
    scannedBitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    // Scanning horizontal laser line animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffsetRatio by infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_anim"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgrigradeBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Analyzing",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = HeadingsLogo,
            modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
        )

        // Onion Image with Laser Scanner bar (from mockup 04)
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEFE8DB))
                .border(2.dp, CardBorder, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (scannedBitmap != null) {
                Image(
                    bitmap = scannedBitmap.asImageBitmap(),
                    contentDescription = "Scanned specimen",
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.sample_onion_lot_1790773055908),
                    contentDescription = "Specimen placeholder",
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                )
            }

            // Laser scan beam line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(3.dp)
                    .offset(y = (230.dp * laserOffsetRatio))
                    .background(LeafGreen)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Analyzing your image...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HeadingsLogo
        )

        Text(
            text = "This will take only a few seconds",
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Overall Progress Bar
        MetricProgressBar(
            label = "Computer Vision Pipeline",
            valueText = "${(processingState.progress * 100).toInt()}%",
            progress = processingState.progress,
            barColor = ButtonGreen,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Multi-stage status cards (from mockup 04)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PipelineStageCard(
                title = "Preprocessing",
                subtitle = "Color normalization & calibration",
                status = when {
                    processingState.stageStatus[PipelineStage.PREPROCESSING] == true -> StageStatus.DONE
                    processingState.stage == PipelineStage.PREPROCESSING -> StageStatus.IN_PROGRESS
                    else -> StageStatus.WAITING
                }
            )

            PipelineStageCard(
                title = "Computer Vision",
                subtitle = "Contour segmentation & diameter sizing",
                status = when {
                    processingState.stageStatus[PipelineStage.COMPUTER_VISION] == true -> StageStatus.DONE
                    processingState.stage == PipelineStage.COMPUTER_VISION -> StageStatus.IN_PROGRESS
                    else -> StageStatus.WAITING
                }
            )

            PipelineStageCard(
                title = "Defect Detection",
                subtitle = "Sprouting, black mold & double bulb AI check",
                status = when {
                    processingState.stageStatus[PipelineStage.DEFECT_DETECTION] == true -> StageStatus.DONE
                    processingState.stage == PipelineStage.DEFECT_DETECTION -> StageStatus.IN_PROGRESS
                    else -> StageStatus.WAITING
                }
            )
        }
    }
}

enum class StageStatus {
    DONE,
    IN_PROGRESS,
    WAITING
}

@Composable
fun PipelineStageCard(
    title: String,
    subtitle: String,
    status: StageStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LightLayer,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (status == StageStatus.IN_PROGRESS) ButtonGreen else CardBorder
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (status) {
                    StageStatus.DONE -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                            tint = ButtonGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    StageStatus.IN_PROGRESS -> {
                        CircularProgressIndicator(
                            color = ButtonGreen,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    StageStatus.WAITING -> {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Waiting",
                            tint = TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Surface(
                color = when (status) {
                    StageStatus.DONE -> Color(0xFFE8F5E9)
                    StageStatus.IN_PROGRESS -> SagePanel
                    StageStatus.WAITING -> Color(0xFFF0EAE1)
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when (status) {
                        StageStatus.DONE -> "Done"
                        StageStatus.IN_PROGRESS -> "In progress"
                        StageStatus.WAITING -> "Waiting"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (status) {
                        StageStatus.DONE -> ButtonGreen
                        StageStatus.IN_PROGRESS -> HeadingsLogo
                        StageStatus.WAITING -> TextMuted
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
