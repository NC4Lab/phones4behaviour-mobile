package com.example.phones4behaviour

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

fun setupSocket(serverIp: String, onNewFile: () -> Unit): Socket {
    val options = IO.Options.builder().setTransports(arrayOf(io.socket.engineio.client.transports.WebSocket.NAME)).build()
    val socket: Socket = IO.socket("http://$serverIp:5000", options)

    socket.on(Socket.EVENT_CONNECT) {
        Log.d("SocketUtils", "Socket connected")
    }.on("new_file") {
        onNewFile();
    }.on(Socket.EVENT_DISCONNECT) {
        Log.d("SocketUtils", "Socket disconnected")
    }

    socket.connect()
    return socket
}
