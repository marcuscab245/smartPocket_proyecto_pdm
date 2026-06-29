package com.example.smartpocket.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Auth : Screen("auth", "Acceso")
    object Dashboard : Screen("dashboard", "Resumen", Icons.Default.Home)
    object TransactionList : Screen("transactions", "Historial", Icons.Default.List)
    object AddTransaction : Screen("add_transaction", "Nuevo", Icons.Default.Add)
    object EditTransaction : Screen("edit_transaction/{transactionId}", "Editar") {
        fun createRoute(id: Int) = "edit_transaction/$id"
    }
}