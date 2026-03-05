package com.example.myhome.repository

interface SocketRepository {

    fun connect()

    fun disconnect()

    fun sendMessage(event: String, data: Any)

    fun listenEvent(event: String, onMessage: (Any) -> Unit)
}