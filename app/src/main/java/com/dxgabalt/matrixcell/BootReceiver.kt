package com.dxgabalt.matrixcell

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo encendido, verificando APK...")
            installApk(context)
        }
    }

    private fun installApk(context: Context) {
        // Ruta donde se guarda la APK en el almacenamiento interno (dentro del directorio de archivos de la app)
        val apkPath = File(context.filesDir, "Download/matrixcell.apk")

        if (apkPath.exists()) {
            Log.d("BootReceiver", "APK encontrada, iniciando instalación...")

            // Si es Android 7+ (API 24 o superior), usar FileProvider
            val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, "com.dxgabalt.matrixcell.fileprovider", apkPath)
            } else {
                Uri.fromFile(apkPath)
            }

            // Crear un Intent para instalar la APK
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Permiso para leer el archivo
            }

            // Iniciar la actividad de instalación
            context.startActivity(installIntent)
        } else {
            Log.e("BootReceiver", "APK no encontrada en: $apkPath")
        }
    }
}
