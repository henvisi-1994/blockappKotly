package com.dxgabalt.matrixcell.network

import com.dxgabalt.matrixcell.model.ApiResponse
import com.dxgabalt.matrixcell.model.UnlockRequestPayload
import com.dxgabalt.matrixcell.model.UnlockValidationPlayload
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("unlock-request")
    suspend fun unlockRequest(@Body requestData: UnlockRequestPayload): Response<ApiResponse> // Enviar datos en el cuerpo de la solicitud
    @POST("unlock-validate")
    suspend fun unlockValidate(@Body requestData: UnlockValidationPlayload): Response<ApiResponse>
}