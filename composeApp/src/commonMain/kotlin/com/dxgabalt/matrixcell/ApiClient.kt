package com.dxgabalt.matrixcell

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    init {
        println("HttpClient creado")
    }

    suspend fun unlockRequest(
        codigoId: String,
        voucherPago: String,
        imei: String,
        ip: String
    ): ApiResponse {
        return try {
            println("Iniciando solicitud a unlock-request")
            client.post("https://matrixcell.onrender.com/devices/unlock-request") {
                contentType(ContentType.Application.Json)
                setBody(
                    UnlockRequestPayload(
                        CODIGO_ID_SUJETO = codigoId,
                        VOUCHER_PAGO = voucherPago,
                        imei = imei,
                        ip = ip
                    )
                )
            }.body()
        } catch (e: Exception) {
            println("Error en la solicitud: ${e.message}")
            ApiResponse(status = "Error", message = "Error: ${e.message}",status_device = "Bloqueado")
        }
    }

    suspend fun unlockValidate(
        codigo: String,
        imei: String,
    ): ApiResponse {
        return try {
            println("Iniciando solicitud a unlock-request")
            client.post("https://matrixcell.onrender.com/device/unlock-validate") {
                contentType(ContentType.Application.Json)
                setBody(
                    UnlockValidationPlayload(
                        code = codigo,
                        imei = imei,
                    )
                )
            }.body()
        } catch (e: Exception) {
            println("Error en la solicitud: ${e.message}")
            ApiResponse(status = "Error", message = "Error: ${e.message}", status_device = "Bloqueado")
        }
    }
}

@Serializable
data class UnlockRequestPayload(
    val CODIGO_ID_SUJETO: String,
    val VOUCHER_PAGO: String,
    val imei: String,
    val ip: String
)
@Serializable
data class UnlockValidationPlayload(
val code: String,
val imei: String,
)

@Serializable
data class ApiResponse(
    val status: String,
    val message: String,
    val status_device:String,
)
