package com.example.phones4behaviour
//
//import android.util.Log
//import okhttp3.*
//import org.json.JSONException
//import org.json.JSONObject
//import java.util.concurrent.TimeUnit
//
//class WebSocketListener(private val activity: MainActivity) : okhttp3.WebSocketListener() {
//    override fun onOpen(webSocket: WebSocket, response: Response) {
//        Log.d("WebSocket", "Connected")
//    }
//
//    override fun onMessage(webSocket: WebSocket, text: String) {
//        Log.d("WebSocket", "Message received: $text")
//        try {
//            val json = JSONObject(text)
//            val files = json.getJSONArray("files")
//            for (i in 0 until files.length()) {
//                val filename = files.getString(i)
//                activity.loadMediaFile(filename)
//            }
//        } catch (e: JSONException) {
//            Log.e("WebSocket", "Received non-JSON message: $text")
//        }
//    }
//
//    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//        t.printStackTrace()
//        Log.e("WebSocket", "Error: ${t.message}")
//    }
//}
//
//fun connectWebSocket(activity: MainActivity) {
//    val client = OkHttpClient.Builder()
//        .readTimeout(3, TimeUnit.SECONDS)
//        .build()
//
//    val request = Request.Builder()
//        .url("ws://10.0.2.2:5000/socket.io/?EIO=4&transport=websocket")
//        .build()
//
//    val listener = WebSocketListener(activity)
//    client.newWebSocket(request, listener)
//    client.dispatcher.executorService.shutdown()
//}


