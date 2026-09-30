package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.components.ManifestaLogoIcon
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaGlassBorderActive
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaPrimaryGradient
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText

@Composable
fun PremiumScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val features = listOf(
        "Unlimited AI manifestations tailored to your exact life goals",
        "Unlimited high-fidelity audio rituals with 432Hz ambient soundscapes",
        "Advanced voice customization (Pitch, tempo, calm pacing)",
        "Full vision board creation with infinite goal cards",
        "Offline playback & instant local synthesis",
        "Encrypted sanctuary backup across devices"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("premium_screen")
            .background(ManifestaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Close Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = ManifestaSecondaryText
                        )
                    }
                }
            }

            // Top Emblem & Headline
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ManifestaLogoIcon(size = 64.dp, animated = true)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Go deeper with Manifesta ✨",
                        style = MaterialTheme.typography.headlineLarge,
                        color = ManifestaText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Transform your daily mindset with unconstrained access to personal AI wellness rituals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ManifestaSecondaryText,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // Feature List
            item {
                ManifestaGlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        features.forEach { feat ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(ManifestaPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = ManifestaSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = feat,
                                    color = ManifestaText,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Plan Card: ₹299 / year
            item {
                ManifestaGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ManifestaAccent.copy(alpha = 0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        ManifestaAccent.copy(alpha = 0.12f),
                                        ManifestaSurface
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ManifestaAccent.copy(alpha = 0.25f))
                                    .border(1.dp, ManifestaAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ANNUAL PASS • BEST VALUE",
                                    color = ManifestaAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "₹299",
                                    color = ManifestaText,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " / year",
                                    color = ManifestaSecondaryText,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Less than ₹25 / month. Cancel anytime through Google Play.",
                                color = ManifestaSecondaryText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // CTA & Guarantee
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ManifestaButton(
                        text = "Start Premium ✨",
                        onClick = { viewModel.activatePremium() },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "start_premium_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "7-day money-back guarantee. Secure transaction.",
                        color = ManifestaSecondaryText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
