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
import cafe.adriel.voyager.transitions.ScaleTransition
import com.dxgabalt.matrixcell.network.DeviceRepository
import com.dxgabalt.matrixcell.network.DeviceStatusStorage
import com.dxgabalt.matrixcell.network.SocketManager
import com.dxgabalt.matrixcell.viewmodels.UnlockRequestViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    MaterialTheme {
        Navigator(screen = UnlockRequestScreen()){
            navigator ->  //SlideTransition(navigator)
            //FadeTransition(navigator)
            ScaleTransition(navigator)
        }
          }
}
class UnlockRequestScreen:Screen{
    @Composable
    override fun Content() {
        val imei = GetAndroidId().getDeviceIdentifier()
        val navigator:Navigator = LocalNavigator.currentOrThrow
        var codigoId by remember { mutableStateOf("") }
        var voucherPago by remember { mutableStateOf("") }
        var emergencyCode by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        val socketManager = SocketManager(coroutineScope) // Instancia de SocketManager
        socketManager.initSocket("https://matrixcell.onrender.com", imei) // Iniciar socket con los parámetros
        val deviceRepository = DeviceRepository(socketManager) // Pasar la instancia de SocketManager al repository
        var unlockRequestViewModel = UnlockRequestViewModel(deviceRepository)
        // Observa los mensajes recibidos del WebSocket
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Solicitud de Desbloqueo", fontSize = 24.sp, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Android ID: ${imei}", fontSize = 14.sp, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(16.dp))
            // Código ID
            OutlinedTextField(
                value = codigoId,
                onValueChange = { codigoId = it },
                label = { Text("Código ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Voucher de Pago
            OutlinedTextField(
                value = voucherPago,
                onValueChange = { voucherPago = it },
                label = { Text("Voucher de Pago") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Código de Emergencia
           /* if (isEmergencyEnabled) {
                OutlinedTextField(
                    value = emergencyCode,
                    onValueChange = { emergencyCode = it },
                    label = { Text("Código de Emergencia") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }*/

            // Mostrar error si existe
           /* if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colors.error)
                Spacer(modifier = Modifier.height(8.dp))
            }*/

            // Botones
            Button(
                onClick = {
                    unlockRequestViewModel.handleSubmit(codigoId,voucherPago,imei,navigator)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Solicitud")
            }
            Spacer(modifier = Modifier.height(8.dp))

            /* if (isEmergencyEnabled) {
                Button(
                    onClick = { viewModel.handleEmergencyCode(emergencyCode, onNavigateToBlockScreen) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Código de Emergencia")
                }
            }*/
        }
    }
}


