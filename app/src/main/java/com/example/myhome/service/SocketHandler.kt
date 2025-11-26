package com.example.myhome.service

import io.socket.client.IO
import io.socket.client.Socket

object SocketHandler {
    private lateinit var socket: Socket

    @Synchronized
    fun setSocket() {
        try {
            val opts = IO.Options().apply {
                reconnection = true
                forceNew = true
                transports = arrayOf("websocket") // BẮT BUỘC CHO ANDROID
            }

            socket = IO.socket("http://192.168.1.122:3000", opts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSocket() = socket

    fun connect() = socket.connect()

    fun disconnect() = socket.disconnect()
}
