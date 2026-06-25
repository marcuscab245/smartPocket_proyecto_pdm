package com.example.smartpocket.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartpocket.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateMillis BETWEEN :startMillis AND :endMillis ORDER BY dateMillis DESC")
    fun getTransactionsByDateRange(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE priority = 'Innecesario' AND isIncome = 0")
    fun getUnnecessaryExpenses(): Flow<List<TransactionEntity>>
}