package com.dxgabalt.matrixcell
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo encendido, iniciando servicio de monitoreo")
            val serviceIntent = Intent(context, BackgroundMonitorService::class.java)
            context.startService(serviceIntent)
        }
    }
}
