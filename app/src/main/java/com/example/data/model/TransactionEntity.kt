package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType(val label: String, val code: String) {
    IN("Money In", "IN"),
    OUT("Money Out", "OUT"),
    BALANCE_UPDATE("Balance Sync", "BALANCE_UPDATE");

    companion object {
        fun fromCode(code: String): TransactionType {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: OUT
        }
    }
}

enum class TransactionCategory(val title: String) {
    TRANSFER_SENT("Money Sent"),
    TRANSFER_RECEIVED("Money Received"),
    SERVICE_PAYMENT("Service / Card Payment"),
    BALANCE_SYNC("Balance Update"),
    OTHER("Other")
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionId: String? = null, // Tix: 7265543152
    val amount: Double = 0.0,
    val type: String = "OUT", // "IN", "OUT", "BALANCE_UPDATE"
    val recipientOrSource: String = "", // e.g. "MUKHTAAR CABDINUUR MAXAMED(5595018)"
    val balance: Double? = null, // Remaining balance: e.g. $63.8775
    val timestamp: Long = System.currentTimeMillis(),
    val rawSms: String? = null,
    val category: String = TransactionCategory.OTHER.title,
    val sender: String = "898"
) {
    val transactionType: TransactionType
        get() = TransactionType.fromCode(type)
}
