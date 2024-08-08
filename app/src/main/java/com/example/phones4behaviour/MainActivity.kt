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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

data class FileInfo(
    val filename: String,
    val filepath: String
)

var serverIp = BuildConfig.SERVER_IP

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val gson = Gson()

    private var imageUrl by mutableStateOf("")
//    private var imageUrl = "http://$serverIp:5000/display/test5.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchFiles()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (imageUrl.isNotEmpty()) {
                        ShowImage(imageUrl)
                    } else {
                        ShowText()
                    }
                }
            }
        }
    }

    private fun fetchFiles() {
        val request = Request.Builder()
            .url("http://$serverIp:5000/display")
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
                            val firstFileUrl = "http://$serverIp:5000/display/" + files[0].filename
                            Log.d("MainActivity", "First file URL: $firstFileUrl")

                            runOnUiThread {
                                imageUrl = firstFileUrl
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




