package com.example.smartpocket.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.smartpocket.ui.dashboard.DashboardScreen
import com.example.smartpocket.ui.transaction.TransactionListScreen
import com.example.smartpocket.ui.transaction.AddEditTransactionScreen
import com.example.smartpocket.ui.auth.AuthScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Auth.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = { navController.navigate(Screen.TransactionList.route) },
                onNavigateToAdd = { navController.navigate(Screen.AddTransaction.route) }
            )
        }
        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onNavigateToAdd = { navController.navigate(Screen.AddTransaction.route) },
                onEditTransaction = { id -> 
                    navController.navigate(Screen.EditTransaction.createRoute(id)) 
                }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddEditTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: -1
            AddEditTransactionScreen(
                transactionId = transactionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}