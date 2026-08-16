package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC, id DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC, id DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'IN'")
    fun getTotalMoneyIn(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'OUT'")
    fun getTotalMoneyOut(): Flow<Double?>

    @Query("SELECT balance FROM transactions WHERE balance IS NOT NULL ORDER BY timestamp DESC, id DESC LIMIT 1")
    fun getLatestBalance(): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE recipientOrSource LIKE '%' || :query || '%' OR transactionId LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transactionId = :tix LIMIT 1")
    suspend fun getTransactionByTix(tix: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
