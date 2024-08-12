package com.example.phones4behaviour

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.logging.HttpLoggingInterceptor
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType

data class FileInfo(
    val file_name: String,
    val file_path: String,
    val file_type: String
)

var serverIp = BuildConfig.SERVER_IP

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()
    private val gson = Gson()

    private var imageUrl by mutableStateOf("")
//    private var imageUrl = "http://$serverIp:5000/display/test5.jpg"
    private var audioUrl by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchFiles()
        setupSocket()

        try {
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { offset ->
                                        val description = "Screen touched at x: ${offset.x}, y: ${offset.y}"
                                        val timestamp = getCurrentTimestamp()
                                        Log.d("TouchEvent", "$description at $timestamp")
                                        postLog(description, timestamp)
                                    }
                                )
                            }) {
                        when {
                            imageUrl.isNotEmpty() -> ShowImage(imageUrl)
                            else -> ShowText()
                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting content: ${e.message}")
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun postLog(description: String, timestamp: String) {
        val json = """
            {
                "tag": "Touch Event",
                "desc": "$description",
                "time": "$timestamp"
            }
        """.trimIndent()

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)
        val request = Request.Builder()
            .url("http://$serverIp:5000/logs")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Failed to post log: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Log posted successfully")
                } else {
                    Log.e("MainActivity", "Failed to post log, response code: ${response.code}")
                }
            }
        })
    }

    private fun fetchFiles() {
        val request = Request.Builder()
            .url("http://$serverIp:5000/uploads/display")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Log.e("MainActivity", "Network request failed: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val fileListType = object : TypeToken<List<FileInfo>>() {}.type
                        val files: List<FileInfo> = gson.fromJson(responseBody.string(), fileListType)

                        Log.d("MainActivity", "Files received: $files")

                        if (files.isNotEmpty()) {
                            val firstFile = files[0]
                            val firstFileUrl = "http://$serverIp:5000${firstFile.file_path}"
                            Log.d("MainActivity", "First file URL: $firstFileUrl")
                            Log.d("MainActivity", "File type: ${firstFile.file_type}")

                            runOnUiThread {
                                if (firstFile.file_type.startsWith("audio/")) {
                                    audioUrl = firstFileUrl
                                    playAudio(audioUrl)
                                } else {
                                    imageUrl = firstFileUrl
                                }
                            }
                        } else {
                            Log.d("MainActivity", "No files received")
                        }
                    }
                } else {
                    Log.e("MainActivity", "Response not successful: ${response.code}")
                }
            }
        })
    }

    private var mediaPlayer: MediaPlayer? = null

    private fun playAudio(url: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener { release() }
                setOnErrorListener { _, what, extra ->
                    Log.e("MediaPlayer", "Error occurred: what=$what, extra=$extra")
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error playing audio: ${e.message}")
        }
    }

    private fun setupSocket() {
        val options = IO.Options.builder().setTransports(arrayOf(io.socket.engineio.client.transports.WebSocket.NAME)).build()
        val socket: Socket = IO.socket("http://$serverIp:5000", options)

        socket.on(Socket.EVENT_CONNECT) {
            Log.d("Socket", "Socket connected")
        }.on("new_file") {
            runOnUiThread {
                fetchFiles()
            }
        }.on(Socket.EVENT_DISCONNECT) {
            Log.d("Socket", "Socket disconnected")
        }

        socket.connect()
    }
}


@Composable
fun ShowImage(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl),
        contentDescription = "Displayed Image",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ShowText() {
    Text(text = "Select file to display")
}




