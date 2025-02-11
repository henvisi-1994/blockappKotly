package com.dxgabalt.matrixcell.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

class SocketManager(context: Context) {
    private var socket: Socket? = null
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)

    fun initSocket(serverUrl: String, androidId: String) {
        try {
            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
            }

            socket = IO.socket(serverUrl, options)
            setupSocketEvents(androidId)
            socket?.connect()
        } catch (e: Exception) {
            Log.e("SocketManager", "Error initializing socket: ${e.message}")
        }
    }

    private fun setupSocketEvents(androidId: String) {
        socket?.apply {
            on(Socket.EVENT_CONNECT) {
                emit("join-device", androidId)
                Log.d("SocketManager", "Connected to socket and joined device channel")
            }

            on("device-blocked") { args ->
                val data = args.firstOrNull()?.toString() ?: "Device blocked"
                saveDeviceStatus(true)
                Log.d("SocketManager", "Device blocked: $data")
            }

            on("device-unblocked") { args ->
                val data = args.firstOrNull()?.toString() ?: "Device unblocked"
                saveDeviceStatus(false)
                Log.d("SocketManager", "Device unblocked: $data")
            }

            on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketManager", "Disconnected from socket")
            }

            on(Socket.EVENT_CONNECT_ERROR) { args ->
                val error = args.firstOrNull()?.toString() ?: "Unknown error"
                Log.e("SocketManager", "Connection error: $error")
            }
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
        Log.d("SocketManager", "Socket disconnected")
    }

    fun isConnected(): Boolean = socket?.connected() == true

    private fun saveDeviceStatus(isBlocked: Boolean) {
        sharedPreferences.edit().putBoolean("device_blocked", isBlocked).apply()
        Log.d("SocketManager", "Device status saved: ${if (isBlocked) "Blocked" else "Unblocked"}")
    }

    fun getDeviceStatus(): Boolean {
        return sharedPreferences.getBoolean("device_blocked", false)
    }
}
