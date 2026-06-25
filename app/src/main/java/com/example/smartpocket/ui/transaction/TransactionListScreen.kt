package com.example.smartpocket.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpocket.data.local.AppDatabase
import com.example.smartpocket.data.model.TransactionEntity
import com.example.smartpocket.data.repository.TransactionRepository
import com.example.smartpocket.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onNavigateToAdd: () -> Unit,
    onEditTransaction: (Int) -> Unit,
    viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(
            TransactionRepository(AppDatabase.getDatabase(LocalContext.current).transactionDao())
        )
    )
) {
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepBlack)
            )
        },
        containerColor = DeepBlack
    ) { padding ->
        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no hay transacciones", color = SecondaryText)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onEdit = { onEditTransaction(transaction.id) },
                        onDelete = { viewModel.deleteTransaction(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.dateMillis))
    
    val accentColor = when(transaction.priority) {
        "Vital" -> VitalGreen
        "Deseo" -> DeseoYellow
        "Innecesario" -> InnecesarioRed
        else -> PureWhite
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardGrey)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 40.dp)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description, style = MaterialTheme.typography.titleMedium, color = PureWhite, fontWeight = FontWeight.Bold)
                Text("${transaction.category} • $dateStr", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                if (!transaction.isIncome) {
                    Text(transaction.priority, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.isIncome) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                    color = if (transaction.isIncome) VitalGreen else InnecesarioRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp), tint = SecondaryText)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(20.dp), tint = InnecesarioRed.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}
