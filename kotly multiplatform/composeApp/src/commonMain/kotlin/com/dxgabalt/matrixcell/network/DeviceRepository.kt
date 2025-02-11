package com.dxgabalt.matrixcell.network

class DeviceRepository(
    private val socketManager: SocketManager
) {
    suspend fun connectToSocket(serverUrl: String, androidId: String) {
        socketManager.initSocket(serverUrl, androidId)
    }

    fun observeDeviceStatus() = socketManager.deviceStatus

    fun disconnectSocket() {
        socketManager.disconnect()
    }
}