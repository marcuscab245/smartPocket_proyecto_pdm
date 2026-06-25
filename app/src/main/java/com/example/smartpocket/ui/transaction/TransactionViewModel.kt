package com.example.smartpocket.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpocket.data.model.TransactionEntity
import com.example.smartpocket.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertTransaction(
        amount: Double,
        description: String,
        category: String,
        priority: String,
        isIncome: Boolean
    ) {
        viewModelScope.launch {
            val newTransaction = TransactionEntity(
                amount = amount,
                description = description,
                category = category,
                priority = priority,
                isIncome = isIncome,
                dateMillis = System.currentTimeMillis()
            )
            repository.insert(newTransaction)
        }
    }

    fun updateTransaction(
        id: Int,
        amount: Double,
        description: String,
        category: String,
        priority: String,
        isIncome: Boolean,
        dateMillis: Long
    ) {
        viewModelScope.launch {
            val updatedTransaction = TransactionEntity(
                id = id,
                amount = amount,
                description = description,
                category = category,
                priority = priority,
                isIncome = isIncome,
                dateMillis = dateMillis
            )
            repository.update(updatedTransaction)
        }
    }

    fun getTransactionById(id: Int): TransactionEntity? {
        return transactions.value.find { it.id == id }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}