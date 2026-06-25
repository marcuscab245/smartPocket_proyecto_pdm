package com.example.smartpocket.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SmartPocket", fontWeight = FontWeight.Black, fontSize = 24.sp) },
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
            item {
                MonthlySummaryCard(monthlyExpenses)
            }
            item {
                BalanceCard(totalIncome, totalExpenses)
            }
            item {
                ImpulsivityCard(impulsivityCost)
            }
            item {
                EducationalTipCard()
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
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
                        Text("Añadir", color = DeepBlack, fontWeight = FontWeight.Bold)
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
        }
    }
}

@Composable
fun MonthlySummaryCard(monthlyExpenses: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardGrey)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Gastos del Mes", style = MaterialTheme.typography.labelMedium, color = SecondaryText)
            Text(
                "$${String.format("%.2f", monthlyExpenses)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
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
            Text("Balance", style = MaterialTheme.typography.titleMedium, color = PureWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Ingresos", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                    Text("$${String.format("%.2f", income)}", color = VitalGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Gastos", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                    Text("$${String.format("%.2f", expenses)}", color = InnecesarioRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun ImpulsivityCard(cost: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = InnecesarioRedContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Costo de Impulsividad", fontWeight = FontWeight.Bold, color = InnecesarioRed)
            Text(
                "$${String.format("%.2f", cost)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = InnecesarioRed
            )
            Text(
                "Dinero que pudiste haber ahorrado",
                style = MaterialTheme.typography.bodySmall,
                color = InnecesarioRed.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EducationalTipCard() {
    val tips = listOf(
        "Los pequeños gastos diarios pueden sumar una gran cantidad al mes.",
        "Identificar gastos 'Innecesarios' es el primer paso para ahorrar.",
        "Ahorrar el 10% de tus ingresos te dará libertad financiera.",
        "Pregúntate antes de comprar: ¿Es una necesidad o un deseo?"
    )
    val randomTip = remember { tips.random() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardGrey)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💡 $randomTip",
                style = MaterialTheme.typography.bodyMedium,
                color = LavenderText
            )
        }
    }
}
