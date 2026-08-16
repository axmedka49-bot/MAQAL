package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekAmberBg
import com.example.ui.theme.SleekBlueContainer
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldBg
import com.example.ui.theme.SleekNavyPrimary
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleBg
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekRoseBg
import com.example.ui.viewmodel.CategorySpending
import com.example.ui.viewmodel.MonthlyBreakdownData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBreakdownSheet(
    breakdownData: MonthlyBreakdownData,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val isSpendingDecreased = breakdownData.spendingDifference <= 0
    val trendColor = if (isSpendingDecreased) SleekEmerald else SleekRose
    val trendBg = if (isSpendingDecreased) SleekEmeraldBg else SleekRoseBg
    val trendIcon = if (isSpendingDecreased) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp

    val barAnim = remember { Animatable(0f) }
    LaunchedEffect(breakdownData) {
        barAnim.snapTo(0f)
        barAnim.animateTo(1f, animationSpec = tween(600))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
        },
        modifier = Modifier.testTag("monthly_breakdown_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Monthly Spending Trends",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${breakdownData.thisMonthName} vs ${breakdownData.prevMonthName} ${breakdownData.thisMonthYear}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_monthly_breakdown_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // High-level Comparison Hero Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SleekNavyPrimary
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "NET SPENDING SHIFT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", breakdownData.thisMonthSpending)}",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = trendBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = trendIcon,
                                        contentDescription = null,
                                        tint = trendColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isSpendingDecreased) {
                                            String.format(Locale.US, "%.1f%% less", abs(breakdownData.spendingChangePercent))
                                        } else {
                                            String.format(Locale.US, "+%.1f%% more", breakdownData.spendingChangePercent)
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = trendColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Previous Month (${breakdownData.prevMonthName})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%,.2f", breakdownData.prevMonthSpending)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Difference",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = if (breakdownData.spendingDifference >= 0) {
                                        "+$${String.format(Locale.US, "%,.2f", breakdownData.spendingDifference)}"
                                    } else {
                                        "-$${String.format(Locale.US, "%,.2f", abs(breakdownData.spendingDifference))}"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSpendingDecreased) SleekEmeraldBg else SleekRoseBg
                                )
                            }
                        }
                    }
                }
            }

            // 4-Quadrant Metric Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Money In
                    BreakdownStatTile(
                        title = "TOTAL INFLOW",
                        currentVal = "+$${String.format(Locale.US, "%,.2f", breakdownData.thisMonthIncome)}",
                        prevVal = "$${String.format(Locale.US, "%,.2f", breakdownData.prevMonthIncome)}",
                        icon = Icons.AutoMirrored.Filled.CallReceived,
                        accentColor = SleekEmerald,
                        bgColor = SleekEmeraldBg,
                        modifier = Modifier.weight(1f)
                    )

                    // Daily Average Spend
                    BreakdownStatTile(
                        title = "DAILY AVERAGE",
                        currentVal = "$${String.format(Locale.US, "%,.2f", breakdownData.dailyAverageSpending)}/day",
                        prevVal = "${breakdownData.thisMonthTransactionCount} txs this month",
                        icon = Icons.Default.DateRange,
                        accentColor = SleekPurple,
                        bgColor = SleekPurpleBg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Shift Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Spending by Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${breakdownData.categoryBreakdowns.size} Categories",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Category Shift Items
            if (breakdownData.categoryBreakdowns.isEmpty()) {
                item {
                    Text(
                        text = "No category spending recorded yet for this period.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(breakdownData.categoryBreakdowns) { cat ->
                    CategoryComparisonRow(
                        categorySpending = cat,
                        thisMonthName = breakdownData.thisMonthName,
                        prevMonthName = breakdownData.prevMonthName
                    )
                }
            }

            // Largest Single Expense Highlight
            if (breakdownData.largestExpense != null) {
                item {
                    val tx = breakdownData.largestExpense
                    val dateFormatted = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(tx.timestamp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(SleekRoseBg, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = SleekRose,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "LARGEST SINGLE EXPENSE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        letterSpacing = 0.8.sp
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = tx.recipientOrSource,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$dateFormatted • ${tx.category}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "-$${String.format(Locale.US, "%,.2f", tx.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekRose
                            )
                        }
                    }
                }
            }

            // Done Button
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("done_monthly_breakdown_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SleekNavyPrimary
                    )
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun BreakdownStatTile(
    title: String,
    currentVal: String,
    prevVal: String,
    icon: ImageVector,
    accentColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(bgColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 0.8.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentVal,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Last month: $prevVal",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryComparisonRow(
    categorySpending: CategorySpending,
    thisMonthName: String,
    prevMonthName: String
) {
    val isDecreased = categorySpending.thisMonthAmount <= categorySpending.prevMonthAmount
    val diff = categorySpending.thisMonthAmount - categorySpending.prevMonthAmount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                when {
                                    categorySpending.category.contains("Shopping", ignoreCase = true) -> SleekAmberBg
                                    categorySpending.category.contains("Service", ignoreCase = true) -> SleekBlueContainer
                                    categorySpending.category.contains("Transfer", ignoreCase = true) || categorySpending.category.contains("Sent", ignoreCase = true) -> SleekPurpleBg
                                    else -> SleekEmeraldBg
                                },
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                categorySpending.category.contains("Shopping", ignoreCase = true) -> Icons.Default.ShoppingCart
                                categorySpending.category.contains("Service", ignoreCase = true) -> Icons.Default.CreditCard
                                categorySpending.category.contains("Transfer", ignoreCase = true) || categorySpending.category.contains("Sent", ignoreCase = true) -> Icons.AutoMirrored.Filled.Send
                                else -> Icons.Default.AccountBalance
                            },
                            contentDescription = null,
                            tint = when {
                                categorySpending.category.contains("Shopping", ignoreCase = true) -> SleekAmber
                                categorySpending.category.contains("Service", ignoreCase = true) -> SleekNavyPrimary
                                categorySpending.category.contains("Transfer", ignoreCase = true) || categorySpending.category.contains("Sent", ignoreCase = true) -> SleekPurple
                                else -> SleekEmerald
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = categorySpending.category,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Last month: $${String.format(Locale.US, "%,.2f", categorySpending.prevMonthAmount)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format(Locale.US, "%,.2f", categorySpending.thisMonthAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (diff >= 0) {
                            "+$${String.format(Locale.US, "%,.2f", diff)}"
                        } else {
                            "-$${String.format(Locale.US, "%,.2f", abs(diff))}"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDecreased) SleekEmerald else SleekRose
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { categorySpending.percentOfTotal.coerceIn(0.02f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (isDecreased) SleekEmerald else SleekNavyPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
