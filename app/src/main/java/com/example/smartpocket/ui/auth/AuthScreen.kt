package com.example.smartpocket.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartpocket.data.local.PinDataStore
import com.example.smartpocket.ui.theme.*

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(PinDataStore(LocalContext.current))
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    // En cuanto el ViewModel marca isAuthenticated, navegamos hacia la app
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
        }
    }

    val (titulo, subtitulo) = when (uiState.mode) {
        AuthMode.LOADING -> "" to ""
        AuthMode.SETUP -> "Crea tu PIN" to "Lo usarás para proteger el acceso a SmartPocket"
        AuthMode.CONFIRM -> "Confirma tu PIN" to "Ingresa nuevamente el PIN para confirmarlo"
        AuthMode.LOGIN -> "Bienvenido de nuevo" to "Ingresa tu PIN para continuar"
    }

    Scaffold(containerColor = DeepBlack) { padding ->
        if (uiState.mode == AuthMode.LOADING) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LavenderAccent)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(CardGrey),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = LavenderAccent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(targetState = titulo, label = "titulo") { texto ->
                Text(
                    text = texto,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(32.dp))

            PinDotsIndicator(
                pinLength = uiState.pinLength,
                filledCount = uiState.pinInput.length,
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reservamos espacio fijo para el mensaje de error y así evitar saltos en el layout
            Text(
                text = uiState.errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = InnecesarioRed,
                modifier = Modifier.height(20.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            NumericKeypad(
                onDigit = viewModel::onDigitEntered,
                onBackspace = viewModel::onBackspace
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PinDotsIndicator(pinLength: Int, filledCount: Int, isError: Boolean) {
    val dotColor = if (isError) InnecesarioRed else LavenderAccent
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(pinLength) { index ->
            val isFilled = index < filledCount
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isFilled) dotColor else CardGrey)
            )
        }
    }
}

@Composable
private fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val filas = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        filas.forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                fila.forEach { digito ->
                    KeypadButton(label = digito, onClick = { onDigit(digito) })
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Spacer(modifier = Modifier.size(72.dp))
            KeypadButton(label = "0", onClick = { onDigit("0") })
            KeypadIconButton(icon = Icons.AutoMirrored.Filled.Backspace, onClick = onBackspace)
        }
    }
}

@Composable
private fun KeypadButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(CardGrey)
            .clip(RoundedCornerShape(36.dp)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick, modifier = Modifier.size(72.dp)) {
            Text(
                text = label,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                color = PureWhite
            )
        }
    }
}

@Composable
private fun KeypadIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(72.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(72.dp)) {
            Icon(imageVector = icon, contentDescription = "Borrar", tint = SecondaryText)
        }
    }
}
