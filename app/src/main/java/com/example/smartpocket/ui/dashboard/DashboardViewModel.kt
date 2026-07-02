package com.example.smartpocket.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpocket.data.repository.ExchangeRateRepository
import com.example.smartpocket.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val repository: TransactionRepository,
    private val exchangeRateRepository: ExchangeRateRepository = ExchangeRateRepository()
) : ViewModel() {

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

    val expensesByCategory: StateFlow<Map<String, Double>> = repository.allTransactions
        .map { list ->
            list.filter { !it.isIncome }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _convertedImpulsivityCost = MutableStateFlow<Double?>(null)
    val convertedImpulsivityCost: StateFlow<Double?> = _convertedImpulsivityCost.asStateFlow()

    private val _currentCurrency = MutableStateFlow("USD") // Cambiado de COP a USD como moneda principal
    val currentCurrency: StateFlow<String> = _currentCurrency.asStateFlow()

    fun convertImpulsivityCost(targetCurrency: String) {
        viewModelScope.launch {
            val amount = impulsivityCost.value
            // Ahora la conversión asume que el monto original está en USD
            val result = exchangeRateRepository.convertAmount(amount, "USD", targetCurrency)
            result.onSuccess {
                _convertedImpulsivityCost.value = it
                _currentCurrency.value = targetCurrency
            }.onFailure {
                _convertedImpulsivityCost.value = null
            }
        }
    }

    fun resetCurrency() {
        _convertedImpulsivityCost.value = null
        _currentCurrency.value = "USD"
    }
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