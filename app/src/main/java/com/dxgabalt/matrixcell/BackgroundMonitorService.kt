package com.dxgabalt.matrixcell

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BackgroundMonitorService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Aquí puedes agregar la lógica del servicio
        // Ejemplo: Monitorear el estado del dispositivo
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // No se usa en este caso
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "monitor_service_channel"
            val channelName = "Monitor Service"
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)

            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Monitor de servicio")
                .setContentText("El servicio de monitoreo está en ejecución.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()

            startForeground(1, notification)
        }
    }
}