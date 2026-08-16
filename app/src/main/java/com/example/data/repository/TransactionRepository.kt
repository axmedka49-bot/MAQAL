package com.example.data.repository

import com.example.data.db.TransactionDao
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.parser.SahalSmsParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val totalMoneyIn: Flow<Double?> = transactionDao.getTotalMoneyIn()
    val totalMoneyOut: Flow<Double?> = transactionDao.getTotalMoneyOut()
    val latestBalance: Flow<Double?> = transactionDao.getLatestBalance()

    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransactions(limit)

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByType(type)

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> =
        transactionDao.searchTransactions(query)

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun insertFromSms(
        sender: String = "898",
        messageBody: String,
        timestamp: Long = System.currentTimeMillis()
    ): TransactionEntity? {
        val parsed = SahalSmsParser.parse(sender, messageBody, timestamp) ?: return null
        // Prevent duplicate Tix if already recorded
        if (parsed.transactionId != null) {
            val existing = transactionDao.getTransactionByTix(parsed.transactionId)
            if (existing != null) {
                return existing
            }
        }
        val insertedId = transactionDao.insertTransaction(parsed)
        return parsed.copy(id = insertedId)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransaction(id)
    }

    suspend fun clearAll() {
        transactionDao.deleteAll()
    }

    /**
     * Seeds initial sample Golis Sahal 898 data if database is empty.
     */
    suspend fun seedInitialDataIfEmpty() {
        val existing = transactionDao.getAllTransactions().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val hourMs = 3600_000L
            val dayMs = 86400_000L

            val sampleSmsList = listOf(
                // Current Month Transactions (August)
                Pair("[SAHAL] Tix: 7265543152, $ 3 ayaad u dirtay MUKHTAAR CABDINUUR MAXAMED(5595018) Tar 12/08/26 17:33:34, Haraagaagu waa $63.8775.", 4 * hourMs),
                Pair("[SAHAL] $10 Ayaad ku bixisay adeega Waafi Card **** 9060", 12 * hourMs),
                Pair("AMAAN(7103610) Tar 12/08/26 12:51:44, Haraagaagu waa $76.8775.", 16 * hourMs),
                Pair("[SAHAL] Tix: 8392019481, $ 50 ayaad ka heshay CAASHA CALI WARSAME(5581290) Tar 11/08/26 10:15:20, Haraagaagu waa $126.8775.", 1 * dayMs + 6 * hourMs),
                Pair("[SAHAL] Tix: 6192840112, $ 15 ayaad u dirtay SUPERMARKET IFTIN(5578120) Tar 10/08/26 19:40:12, Haraagaagu waa $76.8775.", 2 * dayMs + 4 * hourMs),
                Pair("[SAHAL] $5 Ayaad ku bixisay adeega Korontada Golis Power", 3 * dayMs),
                Pair("[SAHAL] Tix: 9948201248, $ 20 ayaad ka heshay MAXAMUUD AXMED(5534891) Tar 09/08/26 14:02:11, Haraagaagu waa $91.8775.", 4 * dayMs),
                Pair("[SAHAL] $4 Ayaad ku bixisay adeega Internet 4G Plus", 5 * dayMs),

                // Previous Month Transactions (July - ~26-32 days ago)
                Pair("[SAHAL] Tix: 5182940192, $ 25 ayaad u dirtay SUPERMARKET IFTIN(5578120) Tar 20/07/26 18:20:10, Haraagaagu waa $110.00.", 26 * dayMs),
                Pair("[SAHAL] $15 Ayaad ku bixisay adeega Waafi Card **** 9060", 27 * dayMs),
                Pair("[SAHAL] Tix: 4410293810, $ 75 ayaad ka heshay HOOYO MARYAN(5510293) Tar 15/07/26 11:30:00, Haraagaagu waa $150.00.", 29 * dayMs),
                Pair("[SAHAL] Tix: 3829104820, $ 18 ayaad u dirtay SOMGAS COOKING(5592810) Tar 12/07/26 14:15:00, Haraagaagu waa $93.00.", 31 * dayMs),
                Pair("[SAHAL] $8 Ayaad ku bixisay adeega Korontada Golis Power", 32 * dayMs)
            )

            val parsedEntities = sampleSmsList.mapIndexedNotNull { _, (sms, offset) ->
                val timeOffset = now - offset
                SahalSmsParser.parse("898", sms, timeOffset)
            }

            if (parsedEntities.isNotEmpty()) {
                transactionDao.insertAll(parsedEntities)
            }
        }
    }
}
