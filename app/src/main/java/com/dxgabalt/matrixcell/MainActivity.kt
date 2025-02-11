package com.dxgabalt.matrixcell

import android.app.ActivityManager
import android.os.UserManager
import android.view.KeyEvent
import android.view.View
import android.annotation.SuppressLint
import android.provider.Settings
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.dxgabalt.matrixcell.model.UnlockRequestPayload
import com.dxgabalt.matrixcell.network.ApiService
import com.dxgabalt.matrixcell.network.HttpClientProvider
import com.dxgabalt.matrixcell.network.SocketManager
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val deviceManager = DeviceManager()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Inicializar el contexto global
        AndroidContext.appContext = applicationContext

        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // Obtener referencias de los elementos del layout
        val androidIdTextView: TextView = findViewById(R.id.androidIdTextView)
        val codigoIdInput: EditText = findViewById(R.id.codigoIdInput)
        val voucherPagoInput: EditText = findViewById(R.id.voucherPagoInput)
        val submitButton: Button = findViewById(R.id.submitButton)
        // Obtener el Android ID real
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        androidIdTextView.text = "Android ID: $androidId"
        val socketManager = SocketManager(applicationContext) // Instancia de SocketManager
        socketManager.initSocket("https://matrixcell.onrender.com", androidId) // Iniciar socket con los parámetros
        // Manejo del evento de clic en el botón
        submitButton.setOnClickListener {
            val codigoId = codigoIdInput.text.toString()
            val voucherPago = voucherPagoInput.text.toString()
            val requestData = UnlockRequestPayload(codigoId, voucherPago, androidId, "")
            sendPostRequest(requestData)
        }
        // Verificar si la app es Device Owner y activar modo kiosco
        if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
            devicePolicyManager.setLockTaskPackages(componentName, arrayOf(packageName))

            if (!activityManager.isInLockTaskMode) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window.setFlags(
                        android.view.WindowManager.LayoutParams.FLAG_SECURE,
                        android.view.WindowManager.LayoutParams.FLAG_SECURE
                    )
                    window.decorView.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            )
                    startLockTask() // Iniciar modo kiosco en versiones modernas
                    println("🔒 Modo Kiosco activado")
                }
            }

            // 🔒 Aplicar restricciones adicionales
            applyDeviceRestrictions(devicePolicyManager, componentName)

        } else {
            Toast.makeText(
                this,
                "Debe configurar la app como Device Owner para bloquear el dispositivo.",
                Toast.LENGTH_LONG
            ).show()
            requestDeviceAdmin()
        }


    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendPostRequest(requestData: UnlockRequestPayload) {
        if (deviceManager.getInternetConnection(applicationContext)) {
            lifecycleScope.launch {
                try {
                    val apiService = HttpClientProvider.getClient().create(ApiService::class.java)
                    val response = apiService.unlockRequest(requestData)

                    if (response.isSuccessful) {
                        startActivity(Intent(applicationContext, BlockAppActivity::class.java))
                    } else {
                        Toast.makeText(applicationContext, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(applicationContext, "No hay conexión a internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestDeviceAdmin() {
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Esta aplicación necesita permisos administrativos para funcionar correctamente."
        )
        startActivity(intent)
    }

    private fun applyDeviceRestrictions(
        devicePolicyManager: DevicePolicyManager,
        componentName: ComponentName
    ) {
        // 🔐 Bloquear arranque en modo seguro
        devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_SAFE_BOOT)

        // 🔐 Bloquear restablecimiento de fábrica
        devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_FACTORY_RESET)

        // 🔐 Bloquear la desinstalación de la app
        devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_UNINSTALL_APPS)

        // 🔐 Bloquear cambios en configuraciones
        devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_MODIFY_ACCOUNTS)

        // 🔐 Bloquear acceso a configuraciones de desarrollo
        devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_DEBUGGING_FEATURES)

        // 🔐 Bloquear instalación de apps de fuentes desconocidas
        devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)

        // 🔐 Desactivar acceso a Play Store y configuración
        disableSystemApp("com.android.vending") // Play Store
        disableSystemApp("com.android.settings") // Configuración

        println("✅ Restricciones aplicadas correctamente")
    }

    private fun disableSystemApp(packageName: String) {
        try {
            val command = "pm disable-user --user 0 $packageName"
            Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            println("❌ $packageName deshabilitada correctamente.")
        } catch (e: Exception) {
            println("⚠️ No se pudo deshabilitar $packageName: ${e.message}")
        }
    }

    override fun onPause() {
        super.onPause()
        startLockTask() // Asegurar que la app no se minimice
    }

    override fun onStop() {
        super.onStop()
        startLockTask()  // Reforzar la reactivación inmediata
    }
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this, "No puedes salir de la aplicación.", Toast.LENGTH_SHORT).show()
    }
    // Interceptar las teclas físicas
    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Interceptar las teclas físicas
        when (event.keyCode) {
            KeyEvent.KEYCODE_BACK,  // Botón "Atrás"
            KeyEvent.KEYCODE_HOME,  // Botón "Inicio"
            KeyEvent.KEYCODE_APP_SWITCH,  // Botón "Multitarea"
            KeyEvent.KEYCODE_POWER,  // Botón de encendido
            KeyEvent.KEYCODE_VOLUME_UP,  // Subir volumen
            KeyEvent.KEYCODE_VOLUME_DOWN -> {  // Bajar volumen
                Toast.makeText(this, "Acción bloqueada.", Toast.LENGTH_SHORT).show()
                return true  // Ignorar el evento de la tecla
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
