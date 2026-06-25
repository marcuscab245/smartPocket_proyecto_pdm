package com.example.smartpocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.smartpocket.ui.navigation.NavGraph
import com.example.smartpocket.ui.theme.SmartPocketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartPocketTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Agregamos un contenedor para manejar el padding del Scaffold si es necesario,
                    // pero NavGraph ya maneja sus propios Scaffolds internos.
                    NavGraph(
                        navController = navController
                    )
                }
            }
        }
    }
}