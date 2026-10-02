package com.example.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.MihrabArchShape
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation states
    val scaleAnim = remember { Animatable(0.7f) }
    val alphaAnim = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )
        // Keep splash screen visible for ~1.8 seconds then advance
        delay(1800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF032219),
                        IslamicEmeraldDark,
                        Color(0xFF021B13)
                    )
                )
            )
            .clickable { onSplashFinished() },
        contentAlignment = Alignment.Center
    ) {
        // Decorative background glowing circles
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(IslamicEmerald.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(IslamicGold.copy(alpha = 0.06f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        ) {
            // Emblem with Mihrab Shape
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(MihrabArchShape)
                    .background(
                        Brush.linearGradient(
                            listOf(IslamicEmerald, IslamicEmeraldDark)
                        )
                    )
                    .border(2.5.dp, IslamicGold, MihrabArchShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_quran_logo),
                    contentDescription = "Quran Sunao Emblem",
                    modifier = Modifier.size(76.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Arabic Calligraphy Header
            Text(
                text = "قُرْآن سُنَاؤُ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // English App Title
            Text(
                text = "Quran Sunao",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your daily Qur'an, kept simple!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Bismillah & Quick Action Chip
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = IslamicGold.copy(alpha = 0.18f),
                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f)),
                modifier = Modifier.clickable { onSplashFinished() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = IslamicGoldLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        color = IslamicGoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = IslamicGoldLight,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Version & Made for Quran Teacher & Students
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
                .alpha(alphaAnim.value)
        ) {
            Text(
                text = "Native Android Quran Academy • v2.1",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
