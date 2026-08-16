package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekBlueContainer
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekNavyPrimary
import com.example.ui.theme.SleekRose
import com.example.ui.viewmodel.WeeklyBarData
import java.util.Locale

@Composable
fun WeeklyComparisonChart(
    weeklyData: List<WeeklyBarData>,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(weeklyData) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 700))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_bar_chart"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header + Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cashflow Velocity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(SleekEmerald, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "In",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(SleekRose, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Out",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Chart
            val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
            val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    if (weeklyData.isEmpty()) return@Canvas

                    val maxVal = weeklyData.maxOfOrNull { maxOf(it.moneyIn, it.moneyOut) }?.coerceAtLeast(20.0) ?: 50.0
                    val totalSlots = weeklyData.size
                    val slotWidth = size.width / totalSlots
                    val chartHeight = size.height - 24.dp.toPx()
                    val barWidth = 7.dp.toPx()
                    val barCorner = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx())

                    // Draw subtle horizontal grid lines
                    val gridSteps = 2
                    for (i in 0..gridSteps) {
                        val y = chartHeight * (i.toFloat() / gridSteps)
                        drawLine(
                            color = surfaceVariantColor.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw bars for each day
                    weeklyData.forEachIndexed { index, data ->
                        val slotCenter = index * slotWidth + (slotWidth / 2)
                        val inBarHeight = (data.moneyIn / maxVal * chartHeight * animationProgress.value).toFloat().coerceAtLeast(3f)
                        val outBarHeight = (data.moneyOut / maxVal * chartHeight * animationProgress.value).toFloat().coerceAtLeast(3f)

                        val inLeft = slotCenter - barWidth - 2.dp.toPx()
                        val outLeft = slotCenter + 2.dp.toPx()

                        val inTop = chartHeight - inBarHeight
                        val outTop = chartHeight - outBarHeight

                        // Draw In bar (Green)
                        drawRoundRect(
                            color = SleekEmerald,
                            topLeft = Offset(inLeft, inTop),
                            size = Size(barWidth, inBarHeight),
                            cornerRadius = barCorner
                        )

                        // Draw Out bar (Rose)
                        drawRoundRect(
                            color = SleekRose,
                            topLeft = Offset(outLeft, outTop),
                            size = Size(barWidth, outBarHeight),
                            cornerRadius = barCorner
                        )

                        // Draw Day label below
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.argb(
                                    (onSurfaceVariant.alpha * 255).toInt(),
                                    (onSurfaceVariant.red * 255).toInt(),
                                    (onSurfaceVariant.green * 255).toInt(),
                                    (onSurfaceVariant.blue * 255).toInt()
                                )
                                textSize = 10.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            drawText(
                                data.dayLabel,
                                slotCenter,
                                size.height - 2.dp.toPx(),
                                paint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "WEEKLY IN/OUT VELOCITY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
