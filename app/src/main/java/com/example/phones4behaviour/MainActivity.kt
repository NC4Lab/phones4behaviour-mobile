package com.example.phones4behaviour

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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FileInfo(
    val file_name: String,
    val file_path: String,
    val file_type: String
)

var serverIp = BuildConfig.SERVER_IP

class MainActivity : ComponentActivity() {
    private var imageUrl by mutableStateOf("")
    private var audioUrl by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchFiles(serverIp) { files ->
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
        setupSocket(serverIp) {
            fetchFiles(serverIp) { files ->
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
        }

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




