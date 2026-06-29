package com.example.smartpocket.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartpocket.data.local.PinDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val PIN_LENGTH = 4

/** Representa en qué punto del flujo de autenticación se encuentra la pantalla. */
enum class AuthMode {
    LOADING,   // Aún se está leyendo el DataStore para saber si hay PIN guardado
    SETUP,     // Primer ingreso: el usuario debe crear su PIN
    CONFIRM,   // Segundo paso del registro: confirmar el PIN recién creado
    LOGIN      // Ya existe un PIN: se solicita para autenticar
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOADING,
    val pinInput: String = "",
    val firstPinAttempt: String = "",
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val pinLength: Int = PIN_LENGTH
)

class AuthViewModel(private val pinDataStore: PinDataStore) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val pinYaRegistrado = pinDataStore.isPinSet.first()
            _uiState.value = _uiState.value.copy(
                mode = if (pinYaRegistrado) AuthMode.LOGIN else AuthMode.SETUP
            )
        }
    }

    /** Se llama cada vez que el usuario toca un dígito del teclado numérico. */
    fun onDigitEntered(digit: String) {
        val current = _uiState.value
        if (current.pinInput.length >= current.pinLength) return

        val newInput = current.pinInput + digit
        _uiState.value = current.copy(pinInput = newInput, errorMessage = null)

        if (newInput.length == current.pinLength) {
            procesarPinCompleto(newInput)
        }
    }

    /** Borra el último dígito ingresado (botón de borrar del teclado). */
    fun onBackspace() {
        val current = _uiState.value
        if (current.pinInput.isEmpty()) return
        _uiState.value = current.copy(
            pinInput = current.pinInput.dropLast(1),
            errorMessage = null
        )
    }

    private fun procesarPinCompleto(pin: String) {
        val current = _uiState.value
        when (current.mode) {
            AuthMode.SETUP -> {
                // Primer PIN ingresado: pasamos a pedir confirmación
                _uiState.value = current.copy(
                    mode = AuthMode.CONFIRM,
                    firstPinAttempt = pin,
                    pinInput = ""
                )
            }
            AuthMode.CONFIRM -> {
                if (pin == current.firstPinAttempt) {
                    guardarPinYAutenticar(pin)
                } else {
                    // No coinciden: reiniciamos el flujo de registro
                    _uiState.value = current.copy(
                        mode = AuthMode.SETUP,
                        firstPinAttempt = "",
                        pinInput = "",
                        errorMessage = "Los PIN no coinciden. Intenta de nuevo."
                    )
                }
            }
            AuthMode.LOGIN -> validarPinExistente(pin)
            AuthMode.LOADING -> Unit
        }
    }

    private fun guardarPinYAutenticar(pin: String) {
        viewModelScope.launch {
            pinDataStore.savePin(pin)
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                pinInput = ""
            )
        }
    }

    private fun validarPinExistente(pin: String) {
        viewModelScope.launch {
            val esValido = pinDataStore.validatePin(pin)
            if (esValido) {
                _uiState.value = _uiState.value.copy(isAuthenticated = true, pinInput = "")
            } else {
                _uiState.value = _uiState.value.copy(
                    pinInput = "",
                    errorMessage = "PIN incorrecto. Intenta de nuevo."
                )
            }
        }
    }
}

class AuthViewModelFactory(private val pinDataStore: PinDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(pinDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
