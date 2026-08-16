package com.example.data.parser

import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

object SahalSmsParser {

    private val PATTERN_TIX = Pattern.compile("""(?i)Tix:\s*([A-Za-z0-9]+)""")
    private val PATTERN_BALANCE = Pattern.compile("""(?i)Haraagaagu\s*waa\s*\$\s*(\d+(?:\.\d+)?)""")
    private val PATTERN_DATE = Pattern.compile("""(?i)Tar\s*(\d{2}/\d{2}/\d{2}\s+\d{2}:\d{2}:\d{2})""")

    // Pattern for Sent Money:
    // e.g. "[SAHAL] Tix: 7265543152, $ 3 ayaad u dirtay MUKHTAAR CABDINUUR MAXAMED(5595018) Tar 12/08/26 17:33:34, Haraagaagu waa $63.8775."
    private val PATTERN_SENT = Pattern.compile(
        """(?i)\$\s*(\d+(?:\.\d+)?)\s*ayaad\s*u\s*dirtay\s+(.+?)(?=\s+Tar|\s*,\s*Haraagaagu|\s*$)"""
    )

    // Pattern for Service Payment:
    // e.g. "[SAHAL] $10 Ayaad ku bixisay adeega Waafi Card **** 9060"
    private val PATTERN_SERVICE = Pattern.compile(
        """(?i)\$\s*(\d+(?:\.\d+)?)\s*Ayaad\s*ku\s*bixisay\s*adeega\s+(.+?)(?=\s+Tar|\s*,\s*Haraagaagu|\s*$)"""
    )

    // Pattern for Received Money:
    // e.g. "[SAHAL] Tix: 8392019481, $ 50 ayaad ka heshay CAASHA CALI WARSAME(5581290) Tar 11/08/26 10:15:20, Haraagaagu waa $126.8775."
    private val PATTERN_RECEIVED = Pattern.compile(
        """(?i)\$\s*(\d+(?:\.\d+)?)\s*ayaad\s*(?:ka\s*)?heshay\s+(.+?)(?=\s+Tar|\s*,\s*Haraagaagu|\s*$)"""
    )

    // Pattern for Balance Update with Account header before Tar:
    // e.g. "AMAAN(7103610) Tar 12/08/26 12:51:44, Haraagaagu waa $76.8775."
    private val PATTERN_BALANCE_HEADER = Pattern.compile(
        """(?i)^(?:\[?SAHAL\]?\s*)?([A-Za-z0-9_\s\(\)\*\-]+?)\s+Tar\s*(\d{2}/\d{2}/\d{2}\s+\d{2}:\d{2}:\d{2})[,\s]+Haraagaagu\s*waa\s*\$\s*(\d+(?:\.\d+)?)"""
    )

    /**
     * Checks if an incoming SMS originated from SAHAL shortcode (898) or contains Sahal identifiers.
     */
    fun isSahalMessage(sender: String?, body: String?): Boolean {
        if (sender == null && body == null) return false
        val cleanSender = sender?.trim() ?: ""
        if (cleanSender == "898" || cleanSender.contains("898", ignoreCase = true) || cleanSender.contains("SAHAL", ignoreCase = true) || cleanSender.contains("GOLIS", ignoreCase = true)) {
            return true
        }
        val cleanBody = body?.trim() ?: ""
        return cleanBody.startsWith("[SAHAL]", ignoreCase = true) ||
                cleanBody.contains("Haraagaagu waa $", ignoreCase = true) ||
                cleanBody.contains("ayaad u dirtay", ignoreCase = true) ||
                cleanBody.contains("Ayaad ku bixisay adeega", ignoreCase = true)
    }

    /**
     * Main parse function converting raw SMS text into a structured TransactionEntity.
     */
    fun parse(
        sender: String = "898",
        messageBody: String,
        receivedTimestamp: Long = System.currentTimeMillis()
    ): TransactionEntity? {
        val trimmed = messageBody.trim()
        if (trimmed.isEmpty()) return null

        val tix = extractTix(trimmed)
        val balance = extractBalance(trimmed)
        val dateTimestamp = extractDate(trimmed, receivedTimestamp)

        // 1. Check Sent Money
        val sentMatcher = PATTERN_SENT.matcher(trimmed)
        if (sentMatcher.find()) {
            val amount = sentMatcher.group(1)?.toDoubleOrNull() ?: 0.0
            val recipient = sentMatcher.group(2)?.trim() ?: "Unknown Recipient"

            return TransactionEntity(
                transactionId = tix,
                amount = amount,
                type = TransactionType.OUT.code,
                recipientOrSource = recipient,
                balance = balance,
                timestamp = dateTimestamp,
                rawSms = trimmed,
                category = TransactionCategory.TRANSFER_SENT.title,
                sender = sender
            )
        }

        // 2. Check Service / Card Payment
        val serviceMatcher = PATTERN_SERVICE.matcher(trimmed)
        if (serviceMatcher.find()) {
            val amount = serviceMatcher.group(1)?.toDoubleOrNull() ?: 0.0
            val serviceName = serviceMatcher.group(2)?.trim() ?: "Adeeg"

            return TransactionEntity(
                transactionId = tix,
                amount = amount,
                type = TransactionType.OUT.code,
                recipientOrSource = "Adeega $serviceName",
                balance = balance,
                timestamp = dateTimestamp,
                rawSms = trimmed,
                category = TransactionCategory.SERVICE_PAYMENT.title,
                sender = sender
            )
        }

        // 3. Check Received Money
        val receivedMatcher = PATTERN_RECEIVED.matcher(trimmed)
        if (receivedMatcher.find()) {
            val amount = receivedMatcher.group(1)?.toDoubleOrNull() ?: 0.0
            val source = receivedMatcher.group(2)?.trim() ?: "Unknown Sender"

            return TransactionEntity(
                transactionId = tix,
                amount = amount,
                type = TransactionType.IN.code,
                recipientOrSource = source,
                balance = balance,
                timestamp = dateTimestamp,
                rawSms = trimmed,
                category = TransactionCategory.TRANSFER_RECEIVED.title,
                sender = sender
            )
        }

        // 4. Check Balance Update (Header before Tar + Haraagaagu waa)
        val balanceHeaderMatcher = PATTERN_BALANCE_HEADER.matcher(trimmed)
        if (balanceHeaderMatcher.find()) {
            val accountSource = balanceHeaderMatcher.group(1)?.trim() ?: "Sahal Account"
            val parsedBal = balanceHeaderMatcher.group(3)?.toDoubleOrNull() ?: balance

            return TransactionEntity(
                transactionId = tix,
                amount = 0.0,
                type = TransactionType.BALANCE_UPDATE.code,
                recipientOrSource = accountSource,
                balance = parsedBal,
                timestamp = dateTimestamp,
                rawSms = trimmed,
                category = TransactionCategory.BALANCE_SYNC.title,
                sender = sender
            )
        }

        // 5. Fallback Extraction if general Sahal/financial format
        if (balance != null || trimmed.contains("Haraagaagu waa", ignoreCase = true)) {
            val fallbackAmount = extractAmount(trimmed) ?: 0.0
            val isOut = trimmed.contains("dirtay", ignoreCase = true) ||
                    trimmed.contains("bixisay", ignoreCase = true)
            val isIn = trimmed.contains("heshay", ignoreCase = true)

            val type = when {
                isIn -> TransactionType.IN.code
                isOut -> TransactionType.OUT.code
                fallbackAmount > 0 -> TransactionType.OUT.code
                else -> TransactionType.BALANCE_UPDATE.code
            }

            val category = when (type) {
                TransactionType.IN.code -> TransactionCategory.TRANSFER_RECEIVED.title
                TransactionType.OUT.code -> if (trimmed.contains("adeeg", ignoreCase = true) || trimmed.contains("card", ignoreCase = true)) {
                    TransactionCategory.SERVICE_PAYMENT.title
                } else {
                    TransactionCategory.TRANSFER_SENT.title
                }
                else -> TransactionCategory.BALANCE_SYNC.title
            }

            return TransactionEntity(
                transactionId = tix,
                amount = fallbackAmount,
                type = type,
                recipientOrSource = extractSenderOrRecipientName(trimmed),
                balance = balance,
                timestamp = dateTimestamp,
                rawSms = trimmed,
                category = category,
                sender = sender
            )
        }

        return null
    }

    private fun extractTix(text: String): String? {
        val matcher = PATTERN_TIX.matcher(text)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun extractAmount(text: String): Double? {
        val matcher = Pattern.compile("""\$\s*(\d+(?:\.\d+)?)""").matcher(text)
        return if (matcher.find()) matcher.group(1)?.toDoubleOrNull() else null
    }

    private fun extractBalance(text: String): Double? {
        val matcher = PATTERN_BALANCE.matcher(text)
        return if (matcher.find()) matcher.group(1)?.toDoubleOrNull() else null
    }

    private fun extractDate(text: String, defaultTime: Long): Long {
        val matcher = PATTERN_DATE.matcher(text)
        if (matcher.find()) {
            val dateStr = matcher.group(1)
            val parsed = parseDate(dateStr)
            if (parsed != null) return parsed
        }
        return defaultTime
    }

    private fun extractSenderOrRecipientName(text: String): String {
        val phoneNameMatch = Pattern.compile("""([A-Za-z0-9_\s]+\(\d+\))""").matcher(text)
        if (phoneNameMatch.find()) {
            return phoneNameMatch.group(1)?.trim() ?: "Sahal 898"
        }
        if (text.contains("adeega", ignoreCase = true)) {
            val idx = text.indexOf("adeega", ignoreCase = true)
            return text.substring(idx).take(40).trim()
        }
        return "Sahal 898"
    }

    private fun parseDate(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null
        val patterns = listOf(
            "dd/MM/yy HH:mm:ss",
            "yy/MM/dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss"
        )
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                sdf.isLenient = false
                val date = sdf.parse(dateStr.trim())
                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) {
                // Try next
            }
        }
        return null
    }
}
