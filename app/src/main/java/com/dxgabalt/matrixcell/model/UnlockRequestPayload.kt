package com.dxgabalt.matrixcell.model


data class UnlockRequestPayload(
    val CODIGO_ID_SUJETO: String,
    val VOUCHER_PAGO: String,
    val imei: String,
    val ip: String
)