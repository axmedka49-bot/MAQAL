package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekBlueContainer
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldBg
import com.example.ui.theme.SleekNavyPrimary
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekRoseBg
import com.example.ui.viewmodel.MonthlyBreakdownData
import java.util.Locale
import kotlin.math.abs

@Composable
fun MonthlyBreakdownCard(
    breakdownData: MonthlyBreakdownData,
    onClickViewMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSpendingDecreased = breakdownData.spendingDifference <= 0
    val trendColor = if (isSpendingDecreased) SleekEmerald else SleekRose
    val trendBg = if (isSpendingDecreased) SleekEmeraldBg else SleekRoseBg
    val trendIcon = if (isSpendingDecreased) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp

    val barAnim = remember { Animatable(0f) }
    LaunchedEffect(breakdownData) {
        barAnim.snapTo(0f)
        barAnim.animateTo(1f, animationSpec = tween(700))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClickViewMore() }
            .testTag("monthly_breakdown_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Title + Comparison Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SleekBlueContainer, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = SleekNavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Monthly Spending Trends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${breakdownData.thisMonthName} vs ${breakdownData.prevMonthName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Trend Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = trendBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = trendIcon,
                            contentDescription = null,
                            tint = trendColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isSpendingDecreased) {
                                String.format(Locale.US, "%.1f%% less", abs(breakdownData.spendingChangePercent))
                            } else {
                                String.format(Locale.US, "+%.1f%% more", breakdownData.spendingChangePercent)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = trendColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Comparison Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // This Month Metric
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = breakdownData.thisMonthName.uppercase(Locale.US),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = SleekNavyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format(Locale.US, "%,.2f", breakdownData.thisMonthSpending)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${breakdownData.thisMonthTransactionCount} transactions",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Previous Month Metric
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = breakdownData.prevMonthName.uppercase(Locale.US),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format(Locale.US, "%,.2f", breakdownData.prevMonthSpending)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${breakdownData.prevMonthTransactionCount} transactions",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Comparative Ratio Bar
            val totalComparison = (breakdownData.thisMonthSpending + breakdownData.prevMonthSpending).coerceAtLeast(1.0)
            val thisMonthRatio = ((breakdownData.thisMonthSpending / totalComparison) * barAnim.value).toFloat().coerceIn(0.05f, 0.95f)
            val prevMonthRatio = (1f - thisMonthRatio)

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(thisMonthRatio)
                            .height(8.dp)
                            .background(if (isSpendingDecreased) SleekEmerald else SleekNavyPrimary)
                    )
                    Box(
                        modifier = Modifier
                            .weight(prevMonthRatio)
                            .height(8.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${breakdownData.thisMonthName}: ${(thisMonthRatio * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SleekNavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${breakdownData.prevMonthName}: ${(prevMonthRatio * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer row with Top Category snapshot & Click action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SleekBlueContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "Top: ${breakdownData.topSpendingCategory} ($${String.format(Locale.US, "%.2f", breakdownData.topCategoryAmount)})",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = SleekNavyPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "Full Breakdown",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = SleekNavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View full breakdown",
                        tint = SleekNavyPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
