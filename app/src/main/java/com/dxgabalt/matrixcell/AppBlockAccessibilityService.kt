package com.dxgabalt.matrixcell

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AppBlockAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Detectar si se está intentando abrir Configuración
            if (packageName.contains("com.android.settings")) {
                performGlobalAction(GLOBAL_ACTION_BACK)  // Volver atrás automáticamente
                Toast.makeText(this, "Acceso bloqueado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInterrupt() {}
}