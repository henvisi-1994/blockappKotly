package com.dxgabalt.matrixcell

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BackgroundMonitorService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BackgroundMonitorService", "Servicio iniciado")

        // Monitoreo continuo en segundo plano
        Thread {
            while (true) {
                try {
                    // Simula monitoreo cada 5 segundos
                    Thread.sleep(5000)

                    // Lógica de monitoreo: verifica el estado de la app
                    checkAppStatus()
                } catch (e: InterruptedException) {
                    Log.e("BackgroundMonitorService", "Error en el monitoreo: ${e.message}")
                }
            }
        }.start()

        return START_STICKY // Asegura que el servicio se reinicie si es eliminado
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun checkAppStatus() {
        // Aquí podrías verificar si el proceso de la app está corriendo
        Log.d("BackgroundMonitorService", "Verificando el estado de la app")

        // Simulación: si detectas anomalías, reinicia la app
        if (!isAppRunning()) {
            Log.d("BackgroundMonitorService", "App no está en ejecución, reiniciando...")
            restartApp()
        }
    }

    private fun isAppRunning(): Boolean {
        // Lógica para verificar si el proceso de la app está activo
        // Por ejemplo, puedes usar ActivityManager para verificarlo
        return true // Simulación: cambiar según la lógica real
    }

    private fun restartApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        Log.d("BackgroundMonitorService", "Servicio destruido")
        // Reiniciar el servicio automáticamente
        val intent = Intent(this, BackgroundMonitorService::class.java)
        startService(intent)
    }
}
