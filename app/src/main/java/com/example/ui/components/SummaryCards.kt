package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekEmeraldLight
import com.example.ui.theme.SleekNavyPrimary
import com.example.ui.theme.SleekRoseLight
import com.example.ui.viewmodel.TransactionFilter
import java.util.Locale

@Composable
fun SummaryCards(
    currentBalance: Double?,
    totalMoneyIn: Double,
    totalMoneyOut: Double,
    selectedFilter: TransactionFilter = TransactionFilter.ALL,
    onSelectFilter: (TransactionFilter) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isMoneyInActive = selectedFilter == TransactionFilter.MONEY_IN
    val isMoneyOutActive = selectedFilter == TransactionFilter.MONEY_OUT

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("summary_hero_card"),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = SleekNavyPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header Row: Current Balance + SAHAL SMS Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectFilter(TransactionFilter.ALL) }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "CURRENT BALANCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp,
                                fontSize = 11.sp
                            ),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        if (selectedFilter != TransactionFilter.ALL) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "Reset All",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentBalance != null) "$${String.format(Locale.US, "%,.2f", currentBalance)}" else "$0.00",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(SleekEmeraldLight, CircleShape)
                        )
                        Text(
                            text = "SAHAL SMS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Money In / Money Out Grid Cards (Interactive)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Money In Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isMoneyInActive) Color.White.copy(alpha = 0.22f)
                            else Color.White.copy(alpha = 0.12f)
                        )
                        .then(
                            if (isMoneyInActive) Modifier.border(1.5.dp, SleekEmeraldLight, RoundedCornerShape(20.dp))
                            else Modifier
                        )
                        .clickable {
                            if (isMoneyInActive) onSelectFilter(TransactionFilter.ALL)
                            else onSelectFilter(TransactionFilter.MONEY_IN)
                        }
                        .padding(14.dp)
                        .testTag("summary_money_in")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CallReceived,
                                contentDescription = null,
                                tint = SleekEmeraldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "MONEY IN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                ),
                                fontWeight = FontWeight.Bold,
                                color = if (isMoneyInActive) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+$${String.format(Locale.US, "%,.2f", totalMoneyIn)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                            fontWeight = FontWeight.Bold,
                            color = SleekEmeraldLight
                        )
                    }
                }

                // Money Out Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isMoneyOutActive) Color.White.copy(alpha = 0.22f)
                            else Color.White.copy(alpha = 0.12f)
                        )
                        .then(
                            if (isMoneyOutActive) Modifier.border(1.5.dp, SleekRoseLight, RoundedCornerShape(20.dp))
                            else Modifier
                        )
                        .clickable {
                            if (isMoneyOutActive) onSelectFilter(TransactionFilter.ALL)
                            else onSelectFilter(TransactionFilter.MONEY_OUT)
                        }
                        .padding(14.dp)
                        .testTag("summary_money_out")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NorthEast,
                                contentDescription = null,
                                tint = SleekRoseLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "MONEY OUT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                ),
                                fontWeight = FontWeight.Bold,
                                color = if (isMoneyOutActive) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "-$${String.format(Locale.US, "%,.2f", totalMoneyOut)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                            fontWeight = FontWeight.Bold,
                            color = SleekRoseLight
                        )
                    }
                }
            }
        }
    }
}
