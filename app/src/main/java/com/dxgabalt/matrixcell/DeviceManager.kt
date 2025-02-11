package com.dxgabalt.matrixcell

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi

class DeviceManager {

     fun blockDevice() {
          val context = AndroidContext.appContext
          val devicePolicyManager =
             context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
          val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
         if (devicePolicyManager.isAdminActive(componentName)) {
            Toast.makeText(context, "🔒 Dispositivo Bloqueado", Toast.LENGTH_SHORT).show()

            // Bloquear dispositivo con DevicePolicyManager (lock the screen)
            devicePolicyManager.lockNow()
        } else {
            Toast.makeText(context, "❌ No tienes permisos de administrador", Toast.LENGTH_SHORT).show()
        }
    }

     fun unblockDevice() {
         val context = AndroidContext.appContext
         val devicePolicyManager =
             context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
         val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
        if (devicePolicyManager.isAdminActive(componentName)) {
            try {
                // Finalizar el modo kiosco si está activo
                devicePolicyManager.setLockTaskPackages(componentName, emptyArray())
                Toast.makeText(context, "🔓 Modo kiosco desactivado.", Toast.LENGTH_SHORT).show()
                // Verificar que el permiso de Administrador sigue activo
                if (!devicePolicyManager.isAdminActive(componentName)) {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Es necesario para seguir administrando el dispositivo.")
                    context.startActivity(intent)
                }

                Toast.makeText(context, "✅ Dispositivo Desbloqueado", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "❌ Error al salir del modo kiosco: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "❌ No tienes permisos de administrador para desbloquear.", Toast.LENGTH_SHORT).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun getInternetConnection(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isOnline = networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

        return isOnline

    }
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

}
