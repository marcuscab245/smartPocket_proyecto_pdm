package com.example.smartpocket.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpocket.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class DashboardViewModel(private val repository: TransactionRepository) : ViewModel() {

    val impulsivityCost: StateFlow<Double> = repository.unnecessaryExpenses
        .map { list -> list.sumOf { it.amount } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val totalExpenses: StateFlow<Double> = repository.allTransactions
        .map { list -> list.filter { !it.isIncome }.sumOf { it.amount } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val totalIncome: StateFlow<Double> = repository.allTransactions
        .map { list -> list.filter { it.isIncome }.sumOf { it.amount } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val monthlyExpenses: StateFlow<Double> = repository.allTransactions
        .map { list -> 
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            
            list.filter { transaction ->
                if (transaction.isIncome) return@filter false
                val transCal = Calendar.getInstance().apply { timeInMillis = transaction.dateMillis }
                transCal.get(Calendar.MONTH) == currentMonth && transCal.get(Calendar.YEAR) == currentYear
            }.sumOf { it.amount }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
}

class DashboardViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}