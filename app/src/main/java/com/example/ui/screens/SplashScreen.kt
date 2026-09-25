package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

// Authentic Sitaram Brand Palette for Launch Integration
val SplashDarkGreen = Color(0xFF0D281C)
val SplashGoldAccent = Color(0xFFC5A059)
val SplashMutedGold = Color(0xFFE2C481)
val SplashBrownTone = Color(0xFF2A2216)

@Composable
fun AyurvedaSplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Graceful startup timer to ensure seamless launch and data preparation
    LaunchedEffect(Unit) {
        delay(1800)
        onTimeout()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_halo")
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SplashDarkGreen)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Centered Brand Crest & Identity
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            content = {
                // Flexible spacer to guarantee perfect vertical balance
                Spacer(modifier = Modifier.weight(1f))

                // Luxury Logo Medallion with Seamless Integrated Background
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(176.dp)
                ) {
                    // Soft ambient warm gold halo glow behind the medallion
                    Box(
                        modifier = Modifier
                            .size(176.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SplashGoldAccent.copy(alpha = haloAlpha),
                                        SplashBrownTone.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Perfectly smooth circular medallion matching the logo's dark/gold/brown tones
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(156.dp)
                            .clip(CircleShape)
                            .background(SplashBrownTone)
                            .border(1.5.dp, SplashGoldAccent.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sitaram_splash_logo),
                            contentDescription = "Sitaram Ayurveda Official Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(154.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Brand Title Typography
                Text(
                    text = "SITARAM AYURVEDA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = SplashMutedGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "ESTD 1921 • APOTHECARY",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.8.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )

                // Flexible spacer balancing top and bottom
                Spacer(modifier = Modifier.weight(1f))

                // Subtle minimalist loading indicator at bottom
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(48.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = SplashGoldAccent.copy(alpha = 0.85f),
                        strokeWidth = 2.dp,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        )
    }
}
