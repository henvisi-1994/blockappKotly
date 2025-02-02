package com.dxgabalt.matrixcell.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.dxgabalt.matrixcell.ApiClient
import com.dxgabalt.matrixcell.BlockAppScreen
import com.dxgabalt.matrixcell.network.DeviceRepository
import com.dxgabalt.matrixcell.network.DeviceStatusStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.russhwolf.settings.Settings
import com.dxgabalt.matrixcell.DeviceManager

class UnlockRequestViewModel(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val apiClient = ApiClient()

    private val _errorMessage = MutableStateFlow("")
    private val _webSocketMessages = MutableStateFlow("")
    private val _isEmergencyEnabled = MutableStateFlow(false)
    val settings = Settings()  // Inicializamos Settings
    val storage = DeviceStatusStorage(settings)  // Pasamos Settings al almacenamiento
    fun handleSubmit(
        codigoId: String,
        voucherPago: String,
        imei: String,
        navigator: Navigator,
    ) {
        if (codigoId.isBlank() || voucherPago.isBlank()) {
            _errorMessage.value = "Por favor, complete todos los campos."
            return
        }

        // Usa el viewModelScope para manejar el ciclo de vida
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ip = ""      // Dinámico
                val online = DeviceManager().getInternetConnection()
                if(online){
                    val response = apiClient.unlockRequest(codigoId, voucherPago, imei, ip)
                    if (response.status == "success") {
                        navigator.push(BlockAppScreen(storage))
                        storage.saveDeviceStatus(response.status_device == "Bloqueado")
                        storage.saveDeviceImei(imei)
                    } else {
                        _errorMessage.value = response.message
                        _isEmergencyEnabled.value = true
                    }
                }else{
                    navigator.push(BlockAppScreen(storage))
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al conectar con el servidor. Inténtelo de nuevo."
                _isEmergencyEnabled.value = true
            }
        }
    }

    fun handleValidationSubmit(
        codigo: String,
        imei: String,
        navigator: Navigator,
    ) {
        if (codigo.isBlank()) {
            _errorMessage.value = "Por favor, complete todos los campos."
            return
        }

        // Usa el viewModelScope para manejar el ciclo de vida
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ip = ""      // Dinámico
                val online = DeviceManager().getInternetConnection()
                if(online){
                    val response = apiClient.unlockValidate(codigo, imei)

                    if (response.status == "success") {
                        navigator.push(BlockAppScreen(storage))
                    } else {
                        _errorMessage.value = response.message
                        _isEmergencyEnabled.value = true
                    }
                }else{
                    if (codigo == "Matrixcell2025") {
                        storage.saveDeviceStatus(false)
                        DeviceManager().unblockDevice()
                    }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al conectar con el servidor. Inténtelo de nuevo."
                _isEmergencyEnabled.value = true
            }
        }
    }

    fun handleEmergencyCode(emergencyCode: String, navigator: Navigator) {
        if (emergencyCode == "Matrixcell2025") {
            navigator.push(BlockAppScreen(storage))
        } else {
            _errorMessage.value = "Código de emergencia incorrecto."
        }
    }

    fun connectToSocket(serverUrl: String, androidId: String) {
        viewModelScope.launch {
            deviceRepository.connectToSocket(serverUrl, androidId)
        }
    }

    override fun onCleared() {
        deviceRepository.disconnectSocket()
        super.onCleared()
    }


}
