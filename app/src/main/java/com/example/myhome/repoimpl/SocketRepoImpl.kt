package com.example.myhome.repoimpl

import com.example.myhome.repository.SocketRepository
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SocketRepoImpl @Inject constructor(
    private val socket: Socket // Được inject từ Hilt Module bạn đã viết
) : SocketRepository {
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus = _connectionStatus.asStateFlow()

    override fun connect() {
        if (!socket.connected()) {
            socket.connect()
        }

        socket.on(Socket.EVENT_CONNECT) {
            _connectionStatus.value = true
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            _connectionStatus.value = false
        }
    }

    override fun disconnect() {
        socket.disconnect()
        socket.off() // Hủy bỏ tất cả listener để tránh leak
    }

    override fun sendMessage(event: String, data: Any) {
        if (socket.connected()) {
            socket.emit(event, data)
        }
    }

    // Lắng nghe dữ liệu từ Server
    override fun listenEvent(event: String, onMessage: (Any) -> Unit) {
        socket.on(event) { args ->
            if (args.isNotEmpty()) {
                onMessage(args[0])
            }
        }
    }
}