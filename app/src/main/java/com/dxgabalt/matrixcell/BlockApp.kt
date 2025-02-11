package com.dxgabalt.matrixcell

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.dxgabalt.matrixcell.model.UnlockRequestPayload
import com.dxgabalt.matrixcell.model.UnlockValidationPlayload
import com.dxgabalt.matrixcell.network.ApiService
import com.dxgabalt.matrixcell.network.HttpClientProvider
import kotlinx.coroutines.launch

class BlockAppActivity : ComponentActivity() {
    private val deviceManager = DeviceManager()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_app)  // Usando XML como layout

        // Obtener referencias de los elementos del layout
        val statusTextView: TextView = findViewById(R.id.statusTextView)
        val unlockCodeInput: EditText = findViewById(R.id.unlockCodeInput)
        val unlockButton: Button = findViewById(R.id.unlockButton)
        val dataWifiButton: Button = findViewById(R.id.dataWifiButton)
        val paymentScreenButton: Button = findViewById(R.id.paymentScreenButton)
        val supportButton: Button = findViewById(R.id.supportButton)
        val paymentInfoTextView: TextView = findViewById(R.id.paymentScreenInfo)
        val supportInfoTextView: TextView = findViewById(R.id.supportInfo)

        // Simular el estado bloqueado
        var isBlocked = true
        statusTextView.text = if (isBlocked) "Dispositivo Bloqueado" else "Dispositivo Desbloqueado"

        // Evento para el botón de desbloqueo
        unlockButton.setOnClickListener {
            val unlockCode = unlockCodeInput.text.toString()
            println("Código de desbloqueo ingresado: $unlockCode")
            // Aquí puedes manejar la lógica para enviar el código al servidor
            // Crear los datos a enviar
            val requestData = UnlockValidationPlayload(unlockCode, deviceManager.getAndroidId(this))
            sendPostValidation(requestData)
        }

        // Evento para el botón de Datos/WiFi
        dataWifiButton.setOnClickListener {
            checkInternetConnection()
        }

        // Evento para el botón de pantalla de pagos
        paymentScreenButton.setOnClickListener {
            paymentInfoTextView.text = "Ir a la pantalla de pagos: https://matrix-cell.com/payments"
        }

        // Evento para el botón de soporte
        supportButton.setOnClickListener {
            supportInfoTextView.text = "Soporte Técnico: +593987808614"
        }
    }

    // Método para verificar la conexión a Internet y mostrar un mensaje
    private fun checkInternetConnection() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } else {
            throw UnsupportedOperationException("VERSION.SDK_INT < M no está soportado")
        }
        val isOnline = networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

        val message = if (isOnline) "Conexión: Online" else "Conexión: Offline"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendPostValidation(requestData: UnlockValidationPlayload) {
        // Usamos lifecycleScope para ejecutar la solicitud en segundo plano
        lifecycleScope.launch {
            try {
                if (deviceManager.getInternetConnection(applicationContext)) {
                    val apiService = HttpClientProvider.getClient().create(ApiService::class.java)
                    val response = apiService.unlockValidate(requestData)

                    if (response.isSuccessful) {
                        val deviceManager = DeviceManager()
                        deviceManager.unblockDevice()
                        val intent = Intent(applicationContext, BlockAppActivity::class.java)
                        // Abrir la siguiente actividad
                        startActivity(intent)

                    } else {
                        // Manejar error en la respuesta
                        println("Error en la respuesta: ${response.code()}")
                        val message =  "Error en la respuesta: ${response.message()}"

                        // Corregido: Usar applicationContext para el Toast
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    }
                }else if(requestData.code =="matrixcell2025"){
                    val deviceManager = DeviceManager()
                    deviceManager.unblockDevice()
                }
            } catch (e: Exception) {
                // Manejar excepciones de red
                println("Excepción: ${e.message}")
            }
        }
    }

}
