package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.parser.SahalSmsParser
import com.example.ui.theme.SleekBlueContainer
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldBg
import com.example.ui.theme.SleekNavyDark
import com.example.ui.theme.SleekNavyPrimary
import com.example.ui.theme.SleekRose
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimulateSmsDialog(
    onDismiss: () -> Unit,
    onSimulate: (smsBody: String, sender: String) -> Unit
) {
    val presets = listOf(
        "1. Sent Money" to "[SAHAL] Tix: 7265543152, $ 3 ayaad u dirtay MUKHTAAR CABDINUUR MAXAMED(5595018) Tar 12/08/26 17:33:34, Haraagaagu waa $63.8775.",
        "2. Service Payment" to "[SAHAL] $10 Ayaad ku bixisay adeega Waafi Card **** 9060",
        "3. Balance Update" to "AMAAN(7103610) Tar 12/08/26 12:51:44, Haraagaagu waa $76.8775.",
        "4. Received Money" to "[SAHAL] Tix: 8392019481, $ 50 ayaad ka heshay CAASHA CALI WARSAME(5581290) Tar 11/08/26 10:15:20, Haraagaagu waa $126.8775."
    )

    var selectedSmsText by remember { mutableStateOf(presets[0].second) }
    var senderCode by remember { mutableStateOf("898") }

    val parsedPreview by remember(selectedSmsText) {
        derivedStateOf {
            SahalSmsParser.parse(senderCode, selectedSmsText)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("simulate_sms_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SleekBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = null,
                                tint = SleekNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Simulate Sahal SMS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SleekNavyDark
                            )
                            Text(
                                text = "Test Golis 898 Parser",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Presets Label
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presets.forEach { (label, text) ->
                        val isSelected = selectedSmsText == text
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SleekBlueContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                selectedSmsText = text
                            }
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SleekNavyDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SMS Text Input
                OutlinedTextField(
                    value = selectedSmsText,
                    onValueChange = { selectedSmsText = it },
                    label = { Text("SMS Message Body") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sms_input_field"),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SleekNavyPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Live Regex Parse Preview
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (parsedPreview != null) SleekEmeraldBg else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (parsedPreview != null) Icons.Default.CheckCircle else Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = if (parsedPreview != null) SleekEmerald else SleekRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (parsedPreview != null) "Parser Matched (${parsedPreview?.category})" else "No Regex match yet",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (parsedPreview != null) SleekEmerald else SleekRose
                            )
                        }

                        if (parsedPreview != null) {
                            val p = parsedPreview!!
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Type: ${p.type} | Amount: $${String.format(Locale.US, "%.2f", p.amount)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• Recipient/Source: ${p.recipientOrSource}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (p.transactionId != null) {
                                Text(
                                    text = "• Tix ID: ${p.transactionId}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (p.balance != null) {
                                Text(
                                    text = "• Remaining Balance: $${String.format(Locale.US, "%.4f", p.balance)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Button
                Button(
                    onClick = {
                        onSimulate(selectedSmsText, senderCode)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_simulate_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SleekNavyPrimary,
                        contentColor = Color.White
                    ),
                    enabled = parsedPreview != null
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Broadcast & Save to Room", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
