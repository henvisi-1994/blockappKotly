package com.dxgabalt.matrixcell

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SettingsBlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MAIN) {
            Toast.makeText(context, "Acceso a los ajustes bloqueado", Toast.LENGTH_SHORT).show()
            abortBroadcast()  // Detener el broadcast
        }
    }
}
