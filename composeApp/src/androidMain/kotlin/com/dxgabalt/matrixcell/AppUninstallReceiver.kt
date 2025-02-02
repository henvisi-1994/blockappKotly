package com.dxgabalt.matrixcell

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AppUninstallReceiver : BroadcastReceiver() {
   override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        if (packageName == context.packageName) {
            Toast.makeText(context, "❗ Desinstalación detectada. Dispositivo en riesgo.", Toast.LENGTH_LONG).show()
        }
    }
}


