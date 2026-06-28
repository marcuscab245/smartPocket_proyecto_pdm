package com.example.smartpocket.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de respuesta del endpoint "latest" de ExchangeRate-API (open.er-api.com).
 * Documentación: https://www.exchangerate-api.com/docs/free
 *
 * Ejemplo de respuesta:
 * {
 *   "result": "success",
 *   "base_code": "USD",
 *   "time_last_update_utc": "Sun, 28 Jun 2026 00:02:31 +0000",
 *   "time_next_update_utc": "Mon, 29 Jun 2026 00:02:31 +0000",
 *   "rates": { "EUR": 0.86, "GBP": 0.74, "JPY": 159.2, ... }
 * }
 */
data class ExchangeRateResponse(
    @SerializedName("result")
    val result: String,

    @SerializedName("base_code")
    val baseCode: String,

    @SerializedName("time_last_update_utc")
    val timeLastUpdateUtc: String,

    @SerializedName("time_next_update_utc")
    val timeNextUpdateUtc: String,

    @SerializedName("rates")
    val rates: Map<String, Double>
)
