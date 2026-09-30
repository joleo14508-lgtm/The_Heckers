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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.HillGreen
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AgrigradeBackground)
    ) {
        // Decorative background hill shapes at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(HillGreen.copy(alpha = 0.25f))
            )
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(LeafGreen.copy(alpha = 0.2f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Center Branding & Logo Emblem
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Emblem
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEFE4D2),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CardBorder),
                    modifier = Modifier
                        .size(170.dp)
                        .padding(8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = HeadingsLogo,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = R.drawable.agrigrade_logo_1790772973744),
                                    contentDescription = "Agrigrade Leaf Emblem",
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Agrigrade",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HeadingsLogo,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "AI-Powered Onion Quality Assessment & Grading",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ButtonGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Automated visual inspection at procurement centers to eliminate human bias & standardize mandi grading.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // Bottom CTA & Pagination Dots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(54.dp)
                        .testTag("get_started_button")
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3 Dots indicator from mockup
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ButtonGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SagePanel))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SagePanel))
                }
            }
        }
    }
}
private val CardBorder = Color(0xFFE6DEC8)
