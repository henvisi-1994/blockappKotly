package com.dxgabalt.matrixcell

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dxgabalt.matrixcell.network.DeviceRepository
import com.dxgabalt.matrixcell.network.DeviceStatusStorage
import com.dxgabalt.matrixcell.network.SocketManager
import com.dxgabalt.matrixcell.viewmodels.UnlockRequestViewModel

import kotlinx.coroutines.launch

class BlockAppScreen(private val storage: DeviceStatusStorage) : Screen {

    @Composable
    override fun Content() {
        val imei = GetAndroidId().getDeviceIdentifier()
        val navigator: Navigator = LocalNavigator.currentOrThrow
        var isBlocked by remember { mutableStateOf(true) }
        var unlockCode by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        val socketManager = SocketManager(coroutineScope) // Instancia de SocketManager
        socketManager.initSocket("https://matrixcell.onrender.com", imei) // Iniciar socket con los parámetros
        val deviceRepository = DeviceRepository(socketManager) // Pasar la instancia de SocketManager al repository
        var unlockRequestViewModel = UnlockRequestViewModel(deviceRepository)
        // Recuperar el estado almacenado localmente al iniciar
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                isBlocked = storage.getDeviceStatus()
            }
        }

        // Mostrar la interfaz basada en el estado del dispositivo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isBlocked) "Dispositivo Bloqueado" else "Dispositivo Desbloqueado",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isBlocked) {
                // Campo para el código de desbloqueo
                OutlinedTextField(
                    value = unlockCode,
                    onValueChange = { unlockCode = it },
                    label = { Text("Código de Desbloqueo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Botón para desbloquear
                Button(
                    onClick = {
                       unlockRequestViewModel.handleValidationSubmit(unlockCode,imei,navigator)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Desbloquear")
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Mensaje de pagos
                Text(
                    text = "Por favor ponte al corriente con los pagos para poder desbloquearlo",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Botón para verificar conexión a internet
                Button(
                    onClick = {
                        DeviceManager().checkInternetConnection()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Datos/WiFi")
                }

                // Botón para ir a la pantalla de pagos
                Button(
                    onClick = {
                        DeviceManager().navigateToPayments()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Ir a la pantalla de pagos")
                }

                // Botón para llamar a soporte técnico
                Button(
                    onClick = {
                        DeviceManager().callSupport()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Llamar a soporte")
                }
            } else {
                Text("El dispositivo está desbloqueado", fontSize = 18.sp)
            }
        }
    }
}
