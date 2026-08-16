package com.example.ui.components

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekAmberBg
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldBg
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleBg
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekRoseBg
import com.example.ui.theme.SleekTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isService = transaction.category.contains("Service", ignoreCase = true) ||
            transaction.recipientOrSource.contains("adeeg", ignoreCase = true) ||
            transaction.recipientOrSource.contains("card", ignoreCase = true)

    val (icon, iconBgColor, iconTint) = when {
        transaction.type == TransactionType.IN.code -> Triple(
            Icons.Default.CallReceived,
            SleekEmeraldBg,
            SleekEmerald
        )
        isService -> Triple(
            Icons.Default.CreditCard,
            SleekPurpleBg,
            SleekPurple
        )
        transaction.type == TransactionType.OUT.code -> Triple(
            Icons.Default.NorthEast,
            SleekRoseBg,
            SleekRose
        )
        else -> Triple(
            Icons.Default.AccountBalanceWallet,
            SleekAmberBg,
            SleekAmber
        )
    }

    val (amountPrefix, amountColor) = when (transaction.type) {
        TransactionType.IN.code -> "+$" to SleekEmerald
        TransactionType.OUT.code -> "-$" to SleekRose
        else -> "$" to SleekTextSecondary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Squircle Category / Direction Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = transaction.category,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.recipientOrSource.ifBlank { "Sahal 898" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "$amountPrefix${String.format(Locale.US, "%.2f", transaction.amount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val subtitle = buildString {
                        if (!transaction.transactionId.isNullOrBlank()) {
                            append("Tix: ${transaction.transactionId} • ")
                        }
                        append(transaction.category)
                    }

                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (transaction.balance != null) {
                        Text(
                            text = "Bal: $${String.format(Locale.US, "%.2f", transaction.balance)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
