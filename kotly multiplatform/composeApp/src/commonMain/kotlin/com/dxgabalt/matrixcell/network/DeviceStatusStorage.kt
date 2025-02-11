package com.dxgabalt.matrixcell.network



import com.russhwolf.settings.Settings


class DeviceStatusStorage(settings: Settings) {

    private val flowSettings: Settings = settings

    companion object {
        private const val KEY_IS_BLOCKED = "isBlocked"
        private const val KEY_IMEI = "deviceImei"
    }

    // Almacenar el estado del dispositivo (bloqueado o desbloqueado)
    suspend fun saveDeviceStatus(isBlocked: Boolean) {
        flowSettings.putBoolean(KEY_IS_BLOCKED, isBlocked)
    }

    // Obtener el estado del dispositivo (por defecto bloqueado)
    fun getDeviceStatus() = flowSettings.getBoolean(KEY_IS_BLOCKED, true)

    // Almacenar el IMEI
    suspend fun saveDeviceImei(imei: String) {
        flowSettings.putString(KEY_IMEI, imei)
    }

    // Obtener el IMEI almacenado
    fun getDeviceImei() = flowSettings.getString(KEY_IMEI, null.toString())
}
