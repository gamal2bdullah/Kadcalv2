package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {
    val shimmerColors = listOf(
        CosmicPanel.copy(alpha = 0.3f),
        CosmicPanel.copy(alpha = 0.6f),
        CosmicPanel.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
    )
}

@Composable
fun ShimmerDashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HeroBanner Shimmer
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        )

        // KPI mini cards
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerEffect(modifier = Modifier.weight(1f).height(85.dp))
                ShimmerEffect(modifier = Modifier.weight(1f).height(85.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerEffect(modifier = Modifier.weight(1f).height(85.dp))
                ShimmerEffect(modifier = Modifier.weight(1f).height(85.dp))
            }
        }

        // Charts
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
    }
}

@Composable
fun ShimmerInventory() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search & control bar shimmer
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        // Sticky metrics shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShimmerEffect(modifier = Modifier.weight(1f).height(70.dp))
            ShimmerEffect(modifier = Modifier.weight(1f).height(70.dp))
        }

        // Lazy items repeaters shimmers
        repeat(5) {
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(98.dp)
            )
        }
    }
}

@Composable
fun ShimmerSchedule() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(64.dp))
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(160.dp))
        repeat(4) {
            ShimmerEffect(modifier = Modifier.fillMaxWidth().height(75.dp))
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(CosmicPanel.copy(alpha = 0.5f), CircleShape)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = CosmicMute.copy(alpha = 0.45f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = description,
            fontSize = 12.sp,
            color = CosmicMute,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))

            val haptic = LocalHapticFeedback.current
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAction()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicOrange,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(text = actionLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = CosmicRed
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Diagnostics Check Failed",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            fontSize = 13.sp,
            color = CosmicMute,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val haptic = LocalHapticFeedback.current
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onRetry()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = CosmicOrange,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Re-compile Sizing Engines", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

data class OnboardingSlide(
    val title: String,
    val desc: String,
    val vectorDraw: @Composable () -> Unit
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(onDismiss: () -> Unit) {
    var currentSlideIndex by remember { mutableStateOf(0) }
    val slides = listOf(
        OnboardingSlide(
            title = "KADcal Solar Optimization",
            desc = "Welcome to KADcal Solar Engineering Suite. Designed to match dynamic power factors, NEC safety codes, and robust phase balances."
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                // Drawing an elegant abstract sun and solar arrays
                drawCircle(color = CosmicOrange.copy(alpha = 0.2f), radius = 70.dp.toPx())
                drawCircle(color = CosmicOrange, radius = 40.dp.toPx())
                
                // Ray accents
                for (i in 0 until 8) {
                    val angle = (i * 45) * (Math.PI / 180f)
                    val startX = (center.x + Math.cos(angle) * 48.dp.toPx()).toFloat()
                    val startY = (center.y + Math.sin(angle) * 48.dp.toPx()).toFloat()
                    val endX = (center.x + Math.cos(angle) * 65.dp.toPx()).toFloat()
                    val endY = (center.y + Math.sin(angle) * 65.dp.toPx()).toFloat()
                    drawLine(
                        color = CosmicAmber,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
            }
        },
        OnboardingSlide(
            title = "Multi-Phase Dynamic Balancers",
            desc = "Safeguard against severe imbalances. Analyze load allocations reactively per phase line, optimizing transformers and sequencer setups instantly."
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                val path = Path().apply {
                    moveTo(10.dp.toPx(), center.y)
                    cubicTo(40.dp.toPx(), 20.dp.toPx(), 50.dp.toPx(), size.height - 20.dp.toPx(), 80.dp.toPx(), center.y)
                    cubicTo(110.dp.toPx(), 20.dp.toPx(), 120.dp.toPx(), size.height - 20.dp.toPx(), 150.dp.toPx(), center.y)
                }
                drawPath(path, CosmicGreen, style = Stroke(width = 6f, cap = StrokeCap.Round))
                drawCircle(CosmicGreen, 8f, Offset(80.dp.toPx(), center.y))
                drawCircle(CosmicBlue, 8f, Offset(40.dp.toPx(), 30.dp.toPx()))
                drawCircle(CosmicPurple, 8f, Offset(120.dp.toPx(), size.height - 30.dp.toPx()))
            }
        },
        OnboardingSlide(
            title = "Real-Time Compliance Auditing",
            desc = "Automate diagnostic calculations instantly. Keep track of phantom leakage, surge startup spikes, and safety envelopes automatically."
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                drawCircle(color = CosmicBlue.copy(alpha = 0.15f), radius = 64.dp.toPx())
                val checkPath = Path().apply {
                    moveTo(45.dp.toPx(), center.y)
                    lineTo(68.dp.toPx(), center.y + 20.dp.toPx())
                    lineTo(115.dp.toPx(), center.y - 25.dp.toPx())
                }
                drawPath(checkPath, CosmicGreenLight, style = Stroke(width = 10f, cap = StrokeCap.Round))
            }
        }
    )

    val currentSlide = slides[currentSlideIndex]
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated visual element matching onboarding
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentSlideIndex,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() with
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "slideVisualAnim"
                ) { targetIndex ->
                    slides[targetIndex].vectorDraw()
                }
            }

            // Description block
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = currentSlideIndex,
                    transitionSpec = { fadeIn() with fadeOut() },
                    label = "slideTextAnim"
                ) { targetIndex ->
                    val slide = slides[targetIndex]
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = slide.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = slide.desc,
                            color = CosmicMute,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    slides.forEachIndexed { idx, _ ->
                        val active = idx == currentSlideIndex
                        val width by animateDpAsState(if (active) 24.dp else 8.dp, label = "dotWidthAnim")
                        Box(
                            modifier = Modifier
                                .size(width = width, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (active) CosmicOrange else CosmicBorder)
                        )
                    }
                }
            }

            // Navigation actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDismiss()
                    }
                ) {
                    Text("Skip", color = CosmicMute, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (currentSlideIndex < slides.size - 1) {
                            currentSlideIndex++
                        } else {
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (currentSlideIndex == slides.size - 1) "Get Started" else "Next",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
