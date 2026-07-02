package com.example.smartpocket.data.repository

import com.example.smartpocket.data.local.TransactionDao
import com.example.smartpocket.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    val unnecessaryExpenses: Flow<List<TransactionEntity>> = transactionDao.getUnnecessaryExpenses()

    suspend fun insert(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun update(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun delete(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteAll() {
        transactionDao.deleteAllTransactions()
    }

    fun getTransactionsInRange(start: Long, end: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateRange(start, end)
    }
}