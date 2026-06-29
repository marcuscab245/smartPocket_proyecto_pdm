package com.example.smartpocket.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

// Extensión a nivel de Context para crear una única instancia de Preferences DataStore
private val Context.securityDataStore by preferencesDataStore(name = "security_prefs")

/**
 * Repositorio encargado de la persistencia local del PIN de acceso usando
 * Preferences DataStore. El PIN nunca se guarda en texto plano: se almacena
 * únicamente su hash SHA-256.
 */
class PinDataStore(private val context: Context) {

    private object Keys {
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val IS_PIN_SET = booleanPreferencesKey("is_pin_set")
    }

    /** Indica si ya existe un PIN registrado (true) o si es el primer ingreso (false). */
    val isPinSet: Flow<Boolean> = context.securityDataStore.data
        .map { prefs -> prefs[Keys.IS_PIN_SET] ?: false }

    /** Guarda el PIN del usuario (registro inicial), almacenando su hash. */
    suspend fun savePin(pin: String) {
        context.securityDataStore.edit { prefs ->
            prefs[Keys.PIN_HASH] = hashPin(pin)
            prefs[Keys.IS_PIN_SET] = true
        }
    }

    /** Compara el PIN ingresado contra el hash guardado. */
    suspend fun validatePin(pin: String): Boolean {
        val savedHash = context.securityDataStore.data.first()[Keys.PIN_HASH]
        return savedHash != null && savedHash == hashPin(pin)
    }

    /** Elimina el PIN registrado (útil para "olvidé mi PIN" o reset de seguridad). */
    suspend fun clearPin() {
        context.securityDataStore.edit { prefs ->
            prefs.remove(Keys.PIN_HASH)
            prefs[Keys.IS_PIN_SET] = false
        }
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
