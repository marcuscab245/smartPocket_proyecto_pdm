package com.example.smartpocket.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpocket.data.local.AppDatabase
import com.example.smartpocket.data.repository.TransactionRepository
import com.example.smartpocket.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    transactionId: Int = -1,
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(
            TransactionRepository(AppDatabase.getDatabase(LocalContext.current).transactionDao())
        )
    )
) {
    val isEditMode = transactionId != -1
    val transactions by viewModel.transactions.collectAsState()
    val existingTransaction = remember(transactionId, transactions) {
        if (isEditMode) transactions.find { it.id == transactionId } else null
    }

    var amount by remember(existingTransaction) { mutableStateOf(existingTransaction?.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var description by remember(existingTransaction) { mutableStateOf(existingTransaction?.description ?: "") }
    var category by remember(existingTransaction) { mutableStateOf(existingTransaction?.category ?: "Comida") }
    var priority by remember(existingTransaction) { mutableStateOf(existingTransaction?.priority ?: "Vital") }
    var isIncome by remember(existingTransaction) { mutableStateOf(existingTransaction?.isIncome ?: false) }

    val categories = listOf(
        CategoryItem("Comida", Icons.Default.Restaurant),
        CategoryItem("Transporte", Icons.Default.DirectionsCar),
        CategoryItem("Compras", Icons.Default.ShoppingBag),
        CategoryItem("Ocio", Icons.Default.Movie),
        CategoryItem("Hogar", Icons.Default.Home)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEditMode) "Editar Transacción" else "Añadir Transacción", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepBlack)
            )
        },
        containerColor = DeepBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("MONTO", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Text("$", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                BasicTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 48.sp, 
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.widthIn(min = 100.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Categoría", fontWeight = FontWeight.Bold, color = PureWhite)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categories) { item ->
                        CategorySquare(
                            item = item,
                            isSelected = category == item.name,
                            onClick = { category = item.name }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Prioridad", fontWeight = FontWeight.Bold, color = PureWhite)
                Text(
                    "Categoriza este gasto para analizar tus hábitos de consumo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                PriorityCard(
                    title = "Vital",
                    subtitle = "Necesidades, facturas, víveres",
                    icon = Icons.Default.FavoriteBorder,
                    isSelected = priority == "Vital",
                    accentColor = VitalGreen,
                    containerColor = VitalGreenContainer,
                    onClick = { priority = "Vital" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PriorityCard(
                    title = "Deseo",
                    subtitle = "Antojos, cenas fuera, pasatiempos",
                    icon = Icons.Default.StarBorder,
                    isSelected = priority == "Deseo",
                    accentColor = DeseoYellow,
                    containerColor = DeseoYellowContainer,
                    onClick = { priority = "Deseo" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PriorityCard(
                    title = "Innecesario",
                    subtitle = "Compras impulsivas, gastos evitables",
                    icon = Icons.Default.WarningAmber,
                    isSelected = priority == "Innecesario",
                    accentColor = InnecesarioRed,
                    containerColor = InnecesarioRedContainer,
                    onClick = { priority = "Innecesario" }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Añadir nota", color = SecondaryText) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardGrey,
                    focusedContainerColor = CardGrey,
                    unfocusedBorderColor = BorderGrey,
                    focusedBorderColor = LavenderAccent
                ),
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = SecondaryText) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (isEditMode && existingTransaction != null) {
                        viewModel.updateTransaction(
                            transactionId, amt, description, category, priority, isIncome, existingTransaction.dateMillis
                        )
                    } else {
                        viewModel.insertTransaction(amt, description, category, priority, isIncome)
                    }
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LavenderAccent)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = DeepBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditMode) "Actualizar Transacción" else "Guardar Transacción", color = DeepBlack, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class CategoryItem(val name: String, val icon: ImageVector)

@Composable
fun CategorySquare(item: CategoryItem, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(if (isSelected) LavenderAccent else CardGrey, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                item.icon, 
                contentDescription = item.name, 
                tint = if (isSelected) DeepBlack else LavenderText,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                item.name, 
                fontSize = 12.sp, 
                color = if (isSelected) DeepBlack else LavenderText,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun PriorityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(if (isSelected) containerColor else DeepBlack, RoundedCornerShape(12.dp))
            .border(1.dp, if (isSelected) accentColor else BorderGrey, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = accentColor, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = accentColor.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(PureWhite)
    )
}
