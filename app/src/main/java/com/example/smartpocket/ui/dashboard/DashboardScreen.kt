package com.example.smartpocket.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpocket.data.local.AppDatabase
import com.example.smartpocket.data.repository.TransactionRepository
import com.example.smartpocket.ui.theme.*
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(
            TransactionRepository(AppDatabase.getDatabase(LocalContext.current).transactionDao())
        )
    )
) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val impulsivityCost by viewModel.impulsivityCost.collectAsState()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsState()
    val expensesByCategory by viewModel.expensesByCategory.collectAsState()
    val convertedCost by viewModel.convertedImpulsivityCost.collectAsState()
    val currentCurrency by viewModel.currentCurrency.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SmartPocket", fontWeight = FontWeight.Black, fontSize = 24.sp, color = PureWhite) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepBlack)
            )
        },
        containerColor = DeepBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            item { MonthlySummaryCard(monthlyExpenses) }

            item { CategoryChartCard(expensesByCategory) }

            item { BalanceCard(totalIncome, totalExpenses) }

            item {
                ImpulsivityCard(
                    cost = impulsivityCost,
                    convertedCost = convertedCost,
                    currency = currentCurrency,
                    onConvert = { viewModel.convertImpulsivityCost(it) },
                    onReset = { viewModel.resetCurrency() }
                )
            }

            item { ActionButtons(onNavigateToAdd, onNavigateToTransactions) }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun CategoryChartCard(expensesByCategory: Map<String, Double>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    
    LaunchedEffect(expensesByCategory) {
        if (expensesByCategory.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries { series(expensesByCategory.values.map { it.toFloat() }) }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardGrey)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Gastos por Categoría",
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (expensesByCategory.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("No hay datos suficientes", color = SecondaryText)
                }
            } else {
                val color = Color(0xFF81C784)
                val lineComponent = rememberLineComponent(
                    color = color,
                    thickness = 16.dp,
                    shape = com.patrykandpatrick.vico.core.common.shape.Shape.rounded(allDp = 4f)
                )
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(
                            columnProvider = remember(lineComponent) {
                                com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer.ColumnProvider.series(lineComponent)
                            }
                        ),
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier.height(200.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                expensesByCategory.keys.forEach { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(category, color = SecondaryText, fontSize = 12.sp)
                        Text("$${String.format(Locale.US, "%.2f", expensesByCategory[category])}", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ImpulsivityCard(
    cost: Double,
    convertedCost: Double?,
    currency: String,
    onConvert: (String) -> Unit,
    onReset: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = InnecesarioRedContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Costo de Impulsividad", fontWeight = FontWeight.Bold, color = InnecesarioRed)
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.CurrencyExchange, contentDescription = "Convertir", tint = InnecesarioRed)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Ver en Dólares (USD)") },
                            onClick = { onReset(); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Ver en Euros (EUR)") },
                            onClick = { onConvert("EUR"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Ver en Pesos Mexicanos (MXN)") },
                            onClick = { onConvert("MXN"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Ver en Yenes (JPY)") },
                            onClick = { onConvert("JPY"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Ver en Pesos Colombianos (COP)") },
                            onClick = { onConvert("COP"); showMenu = false }
                        )
                    }
                }
            }

            AnimatedContent(targetState = convertedCost ?: cost, label = "costAnim") { displayAmount ->
                val prefix = when (currency) {
                    "USD" -> "$"
                    "EUR" -> "€"
                    "MXN" -> "MXN $"
                    "JPY" -> "¥"
                    "COP" -> "COP $"
                    else -> "$currency "
                }
                Text(
                    text = "$prefix${String.format(Locale.US, "%.2f", displayAmount)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = InnecesarioRed
                )
            }
            
            Text(
                "Dinero en gastos innecesarios",
                style = MaterialTheme.typography.bodySmall,
                color = InnecesarioRed.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun MonthlySummaryCard(monthlyExpenses: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = VitalGreenContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Gastos del Mes (USD)", style = MaterialTheme.typography.labelMedium, color = VitalGreen)
            Text(
                "$${String.format(Locale.US, "%.2f", monthlyExpenses)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = VitalGreen
            )
            Text("Estás bajo el límite sugerido", style = MaterialTheme.typography.bodySmall, color = VitalGreen.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun BalanceCard(income: Double, expenses: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardGrey)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Resumen General (USD)", style = MaterialTheme.typography.titleMedium, color = PureWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Ingresos", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                    Text("$${String.format(Locale.US, "%.2f", income)}", color = VitalGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Gastos", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                    Text("$${String.format(Locale.US, "%.2f", expenses)}", color = InnecesarioRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun ActionButtons(onNavigateToAdd: () -> Unit, onNavigateToTransactions: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onNavigateToAdd,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = DeepBlack)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nuevo", color = DeepBlack, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onNavigateToTransactions,
            modifier = Modifier.weight(1f).height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CardGrey)
        ) {
            Icon(Icons.Default.History, contentDescription = null, tint = LavenderAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Historial", color = LavenderAccent, fontWeight = FontWeight.Bold)
        }
    }
}
