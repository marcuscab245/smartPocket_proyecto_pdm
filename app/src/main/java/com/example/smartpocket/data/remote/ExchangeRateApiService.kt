package com.example.smartpocket.data.remote

import com.example.smartpocket.data.remote.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Definición de los endpoints de la API de divisas (ExchangeRate-API, versión open access).
 * Base URL: https://open.er-api.com/
 *
 * No requiere API Key. Devuelve las tasas de cambio actuales con respecto a una moneda base.
 */
interface ExchangeRateApiService {

    /**
     * Obtiene las tasas de cambio más recientes para una moneda base dada.
     * Ejemplo: GET https://open.er-api.com/v6/latest/USD
     *
     * @param baseCurrency código ISO 4217 de la moneda base (ej. "USD", "EUR").
     */
    @GET("v6/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRateResponse
}
