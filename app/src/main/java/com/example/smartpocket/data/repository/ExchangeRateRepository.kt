package com.example.smartpocket.data.repository

import com.example.smartpocket.data.remote.ExchangeRateApiService
import com.example.smartpocket.data.remote.RetrofitClient
import com.example.smartpocket.data.remote.model.ExchangeRateResponse

/**
 * Resultado simplificado para exponer a la capa de UI/ViewModel sin filtrar
 * detalles de Retrofit (excepciones, códigos HTTP, etc.).
 */
sealed class ExchangeRateResult {
    data class Success(val data: ExchangeRateResponse) : ExchangeRateResult()
    data class Error(val message: String) : ExchangeRateResult()
}

/**
 * Repositorio encargado de obtener las tasas de cambio desde la API remota.
 * Sigue el mismo patrón que TransactionRepository, pero para datos de red.
 */
class ExchangeRateRepository(
    private val apiService: ExchangeRateApiService = RetrofitClient.exchangeRateApiService
) {

    suspend fun getLatestRates(baseCurrency: String): ExchangeRateResult {
        return try {
            val response = apiService.getLatestRates(baseCurrency)

            if (response.result == "success") {
                ExchangeRateResult.Success(response)
            } else {
                ExchangeRateResult.Error("La API no pudo procesar la solicitud.")
            }

        } catch (e: java.io.IOException) {
            ExchangeRateResult.Error("Sin conexión a internet. Verifica tu red.")
        } catch (e: retrofit2.HttpException) {
            ExchangeRateResult.Error("Error del servidor (${e.code()}). Intenta más tarde.")
        } catch (e: Exception) {
            ExchangeRateResult.Error("Ocurrió un error inesperado: ${e.localizedMessage}")
        }
    }

    /**
     * Convierte un monto utilizando la tasa de cambio obtenida desde la API.
     * Este método podrá ser utilizado por el Dashboard para convertir el
     * Costo de Impulsividad a otra moneda.
     */
    suspend fun convertAmount(
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Result<Double> {

        return when (val result = getLatestRates(fromCurrency)) {

            is ExchangeRateResult.Success -> {

                val rate = result.data.rates[toCurrency]

                if (rate != null) {
                    Result.success(amount * rate)
                } else {
                    Result.failure(
                        Exception("No se encontró la moneda $toCurrency")
                    )
                }
            }

            is ExchangeRateResult.Error -> {
                Result.failure(Exception(result.message))
            }
        }
    }
}