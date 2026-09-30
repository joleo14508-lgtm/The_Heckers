package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InspectionRecord
import com.example.data.model.OnionGrade
import com.example.ui.components.DonutFreshRottenChart
import com.example.ui.components.GradeDistributionBars
import com.example.ui.components.MetricProgressBar
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GradeAGreen
import com.example.ui.theme.GradeBOrange
import com.example.ui.theme.GradeRejectRed
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun AnalyticsScreen(
    inspections: List<InspectionRecord>,
    modifier: Modifier = Modifier
) {
    val total = inspections.size.coerceAtLeast(1)
    val freshCount = inspections.count { it.isFresh }
    val rottenCount = inspections.count { !it.isFresh }
    val freshPercent = (freshCount * 100) / total
    val rottenPercent = 100 - freshPercent

    val gradeACount = inspections.count { it.overallGrade == OnionGrade.GRADE_A }
    val gradeBCount = inspections.count { it.overallGrade == OnionGrade.GRADE_B }
    val rejectCount = inspections.count { it.overallGrade == OnionGrade.REJECT }

    val sproutingDefectCount = inspections.count { it.sproutingDetected }
    val rotDefectCount = inspections.count { it.rotFungalDetected }
    val doubleBulbCount = inspections.count { it.isDoubleBulb }
    val peelingDefectCount = inspections.count { it.skinIntegrityPercent < 75 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgrigradeBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Analytics",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = HeadingsLogo
        )
        Text(
            text = "Quality distribution & defect trends across procurement intake",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Overview Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricSummaryBox(
                label = "Inspected",
                value = "$total",
                color = HeadingsLogo,
                modifier = Modifier.weight(1f)
            )
            MetricSummaryBox(
                label = "Grade A %",
                value = "${(gradeACount * 100) / total}%",
                color = GradeAGreen,
                modifier = Modifier.weight(1f)
            )
            MetricSummaryBox(
                label = "Rejection %",
                value = "${(rejectCount * 100) / total}%",
                color = GradeRejectRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 1: Fresh vs Rotten (mockup 06)
        Card(
            colors = CardDefaults.cardColors(containerColor = LightLayer),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Fresh vs Rotten",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeadingsLogo
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DonutFreshRottenChart(
                        freshPercent = freshPercent,
                        rottenPercent = rottenPercent
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(LeafGreen))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Fresh", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                                Text("$freshPercent% ($freshCount bulbs)", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(GradeRejectRed))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Rotten / Cull", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                                Text("$rottenPercent% ($rottenCount bulbs)", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: Grade Distribution Bar Chart (mockup 06)
        Card(
            colors = CardDefaults.cardColors(containerColor = LightLayer),
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
                    Text(
                        text = "Grade Distribution",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeadingsLogo
                    )
                    Text(
                        text = "Standard 3-Tier",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                GradeDistributionBars(
                    gradeACount = gradeACount,
                    gradeBCount = gradeBCount,
                    rejectCount = rejectCount
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: Defect Incidence Analysis
        Card(
            colors = CardDefaults.cardColors(containerColor = LightLayer),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Defect Incidence Breakdown",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeadingsLogo
                )
                Spacer(modifier = Modifier.height(8.dp))

                MetricProgressBar(
                    label = "Fungal Decay / Black Mold",
                    valueText = "$rotDefectCount found",
                    progress = rotDefectCount.toFloat() / total,
                    barColor = GradeRejectRed
                )

                MetricProgressBar(
                    label = "Sprouting / Shoot Emergence",
                    valueText = "$sproutingDefectCount found",
                    progress = sproutingDefectCount.toFloat() / total,
                    barColor = GradeBOrange
                )

                MetricProgressBar(
                    label = "Outer Tunic Peeling (>25%)",
                    valueText = "$peelingDefectCount found",
                    progress = peelingDefectCount.toFloat() / total,
                    barColor = SagePanel
                )

                MetricProgressBar(
                    label = "Double / Twin Bulb Deformity",
                    valueText = "$doubleBulbCount found",
                    progress = doubleBulbCount.toFloat() / total,
                    barColor = ButtonGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun MetricSummaryBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LightLayer,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 11.sp, color = TextMuted)
        }
    }
}
