package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SimpleAreaLineChart(
    data: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier.fillMaxWidth().height(260.dp),
    strokeColor: Color = CosmicOrange,
    fillColor: Color = CosmicOrange.copy(alpha = 0.2f),
    unit: String = " kW"
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CosmicPanel)
            .border(1.dp, CosmicBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .semantics {
                contentDescription = "24-Hour Cumulative Load Curve Chart. Interactive. Tap points to view exact power ratings."
            }
    ) {
        val maxValue = (data.maxOrNull() ?: 1.0).coerceAtLeast(0.1)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        val width = size.width
                        val padL = 50f
                        val padR = 20f
                        val chartW = width - padL - padR
                        val stepX = chartW / (data.size - 1).coerceAtLeast(1)

                        val relativeX = offset.x - padL
                        val index = (relativeX / stepX).plus(0.5f).toInt().coerceIn(0, data.size - 1)
                        if (index != selectedIndex) {
                            selectedIndex = index
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val padL = 50f
            val padR = 20f
            val padT = 20f
            val padB = 40f

            val chartW = width - padL - padR
            val chartH = height - padT - padB

            // Y-Axis labels (5 divisions)
            for (i in 0..4) {
                val ratio = i / 4f
                val y = padT + chartH - (ratio * chartH)
                val value = maxValue * ratio
                val labelText = String.format("%.1f", value) + unit

                val measuredText = textMeasurer.measure(
                    text = labelText,
                    style = TextStyle(color = CosmicMute, fontSize = 9.sp)
                )

                drawText(
                    textLayoutResult = measuredText,
                    topLeft = Offset(padL - measuredText.size.width - 8f, y - measuredText.size.height / 2f)
                )

                // Gridlines
                drawLine(
                    color = CosmicBorder,
                    start = Offset(padL, y),
                    end = Offset(width - padR, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            if (data.isNotEmpty()) {
                val stepX = chartW / (data.size - 1).coerceAtLeast(1)
                val points = data.mapIndexed { idx, value ->
                    val x = padL + idx * stepX
                    val y = padT + chartH - ((value / maxValue).toFloat() * chartH)
                    Offset(x, y)
                }

                // Draw solid background area
                val fillPath = Path().apply {
                    moveTo(padL, padT + chartH)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(padL + chartW, padT + chartH)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(strokeColor.copy(alpha = 0.35f), Color.Transparent),
                        startY = padT,
                        endY = padT + chartH
                    )
                )

                // Draw line path
                val strokePath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(
                    path = strokePath,
                    color = strokeColor,
                    style = Stroke(width = 4f, join = StrokeJoin.Round, cap = StrokeCap.Round)
                )

                // Draw standard vertices
                points.forEachIndexed { idx, point ->
                    if (idx % (data.size / 6).coerceAtLeast(1) == 0 || idx == data.size - 1) {
                        drawCircle(
                            color = strokeColor,
                            radius = 5f,
                            center = point
                        )
                    }
                }

                // Selected point crosshair & tracking indicator
                selectedIndex?.let { idx ->
                    val point = points[idx]
                    // Vertical tracker line
                    drawLine(
                        color = CosmicAmber.copy(alpha = 0.60f),
                        start = Offset(point.x, padT),
                        end = Offset(point.x, padT + chartH),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                    // Highlighting dot
                    drawCircle(
                        color = CosmicAmber,
                        radius = 8f,
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = point
                    )
                }

                // Horizontal X Labels
                val labelStep = (data.size / 6).coerceAtLeast(1)
                labels.forEachIndexed { idx, label ->
                    if (idx % labelStep == 0 || idx == labels.size - 1) {
                        val x = padL + idx * stepX
                        val measuredText = textMeasurer.measure(
                            text = label,
                            style = TextStyle(color = CosmicMute, fontSize = 9.sp)
                        )
                        drawText(
                            textLayoutResult = measuredText,
                            topLeft = Offset(x - measuredText.size.width / 2f, padT + chartH + 12f)
                        )
                    }
                }
            }
        }

        // Selected interactive point tooltips
        selectedIndex?.let { idx ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(CosmicPanel2, RoundedCornerShape(6.dp))
                    .border(1.dp, CosmicBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${labels[idx]}: ${String.format("%.2f", data[idx])}$unit",
                    color = CosmicAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun SimpleDonutChart(
    items: List<Pair<String, Double>>,
    colors: List<Color>,
    modifier: Modifier = Modifier.size(200.dp),
    strokeWidth: Dp = 26.dp
) {
    val total = items.sumOf { it.second }.coerceAtLeast(1.0)
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier.semantics {
            contentDescription = "Energy share donut chart categorization. Click segments to see portions."
        },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(items) {
                    detectTapGestures { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = offset.x - center.x
                        val dy = offset.y - center.y
                        var angleRad = Math.atan2(dy.toDouble(), dx.toDouble())
                        var angleDeg = Math.toDegrees(angleRad)
                        if (angleDeg < 0) angleDeg += 360.0
                        
                        // Shift angle to align with drawing start (-90 degrees)
                        var adjustedAngle = (angleDeg + 90.0) % 360.0
                        
                        var cumulativeRatio = 0.0
                        var targetIdx: Int? = null
                        
                        items.forEachIndexed { index, pair ->
                            val shareAngle = (pair.second / total) * 360.0
                            val limit = cumulativeRatio + shareAngle
                            if (adjustedAngle >= cumulativeRatio && adjustedAngle < limit) {
                                targetIdx = index
                            }
                            cumulativeRatio = limit
                        }
                        
                        targetIdx?.let { idx ->
                            if (idx != selectedIndex) {
                                selectedIndex = idx
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.width - strokeWidth.toPx()) / 2f
            var startAngle = -90f

            items.forEachIndexed { idx, item ->
                val sweepAngle = ((item.second / total) * 360f).toFloat()
                val color = colors.getOrElse(idx) { Color.Gray }
                val isSelected = idx == selectedIndex
                val activeWidth = if (isSelected) strokeWidth.toPx() + 10f else strokeWidth.toPx()

                drawArc(
                    color = if (isSelected) color else color.copy(alpha = 0.82f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = activeWidth, cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }

        // Middle overlay text for selection details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            selectedIndex?.let { idx ->
                val (name, value) = items[idx]
                val pct = (value / total) * 100.0
                Text(
                    text = name,
                    color = CosmicMute,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = String.format("%.1f%%", pct),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            } ?: run {
                Text(
                    text = "Total Energy",
                    color = CosmicMute,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (total >= 1000) String.format("%.1f kWh", total/1000.0) else "${total.toInt()} Wh",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun HorizontalBarsChart(
    items: List<Pair<String, Double>>,
    color: Color = CosmicOrange,
    modifier: Modifier = Modifier.fillMaxWidth(),
    formatter: (Double) -> String
) {
    val maxVal = (items.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(0.1)

    Column(
        modifier = modifier.semantics {
            contentDescription = "Horizontal bars progress chart representation."
        },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (name, value) ->
            val fraction = (value / maxVal).toFloat().coerceIn(0.01f, 1.0f)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        fontSize = 12.sp,
                        color = CosmicMute,
                        maxLines = 1,
                        modifier = Modifier.weight(1f).padding(end = 6.dp)
                    )
                    Text(
                        text = formatter(value),
                        fontSize = 11.sp,
                        color = CosmicText,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(CosmicBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(color, color.copy(alpha = 0.5f))
                                )
                            )
                    )
                }
            }
        }
    }
}
