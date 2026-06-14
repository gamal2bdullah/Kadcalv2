package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicMute
import com.example.ui.theme.CosmicOrange
import com.example.ui.theme.CosmicPanel
import com.example.ui.theme.CosmicText
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CosmicPanel)
            .padding(16.dp)
    ) {
        val maxValue = (data.maxOrNull() ?: 1.0).coerceAtLeast(0.1)

        Canvas(modifier = Modifier.fillMaxSize()) {
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
                drawString(
                    text = String.format("%.1f", value) + unit,
                    x = padL - 10f,
                    y = y,
                    alignRight = true
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
                        colors = listOf(strokeColor.copy(alpha = 0.4f), Color.Transparent),
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
                    style = Stroke(width = 5f, join = StrokeJoin.Round, cap = StrokeCap.Round)
                )

                // Vertex data circles
                points.forEachIndexed { idx, point ->
                    if (idx % (data.size / 6).coerceAtLeast(1) == 0 || idx == data.size - 1) {
                        drawCircle(
                            color = strokeColor,
                            radius = 6f,
                            center = point
                        )
                    }
                }

                // Horizontal X Labels (Every 3 steps to prevent overlap)
                val labelStep = (data.size / 6).coerceAtLeast(1)
                labels.forEachIndexed { idx, label ->
                    if (idx % labelStep == 0 || idx == labels.size - 1) {
                        val x = padL + idx * stepX
                        drawString(
                            text = label,
                            x = x,
                            y = padT + chartH + 25f,
                            alignRight = false,
                            centerHoriz = true
                        )
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawString(
    text: String,
    x: Float,
    y: Float,
    alignRight: Boolean = false,
    centerHoriz: Boolean = false
) {
    // Basic canvas text drawer falling back to standard canvas helper.
    // To ensure complete platform-agnostic performance we can use direct standard draw context native Canvas
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#94A3B8")
        textSize = 28f
        isAntiAlias = true
        typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.NORMAL)
    }
    val bounds = android.graphics.Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    val drawX = if (alignRight) {
        x - bounds.width()
    } else if (centerHoriz) {
        x - bounds.width() / 2f
    } else {
        x
    }
    val drawY = y + bounds.height() / 2f
    drawContext.canvas.nativeCanvas.drawText(text, drawX, drawY, paint)
}

@Composable
fun SimpleDonutChart(
    items: List<Pair<String, Double>>,
    colors: List<Color>,
    modifier: Modifier = Modifier.size(200.dp),
    strokeWidth: Dp = 26.dp
) {
    val total = items.sumOf { it.second }.coerceAtLeast(1.0)
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.width - strokeWidth.toPx()) / 2f
            var startAngle = -90f

            items.forEachIndexed { idx, item ->
                val sweepAngle = ((item.second / total) * 360f).toFloat()
                val color = colors.getOrElse(idx) { Color.Gray }
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
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

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    colors = listOf(color, Color(0xFFFBBF24))
                                )
                            )
                    )
                }
            }
        }
    }
}
