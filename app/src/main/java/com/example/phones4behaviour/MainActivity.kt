package com.example.phones4behaviour

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.video.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.phones4behaviour.ui.theme.Phones4BehaviourTheme
import io.socket.client.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.core.StreamVideo

data class FileInfo(
    val file_name: String,
    val file_path: String,
    val file_type: String,
    val device_id: String,
    val timestamp: String
)

data class DeviceInfo(
    val id: String,
    val model: String,
)

data class LogFile(
    val tag: String,
    val desc: String,
    val time: String,
    val device: String
)

data class FrameFile(
    val image: String,
    val time: String,
    val device: String
)


var serverIp = BuildConfig.SERVER_IP

class MainActivity : ComponentActivity() {
    private var imageUrl by mutableStateOf("")
    private var audioUrl by mutableStateOf("")

    private lateinit var socket: Socket
    private lateinit var device: DeviceInfo
    private var targetDeviceId by mutableStateOf("")
    private var currentDeviceId by mutableStateOf("")

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setCameraPreview()
            } else {
                Log.d("Camera", "Camera permission denied")
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//        when (PackageManager.PERMISSION_GRANTED) {
//            ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.CAMERA
//            ) -> {
//                setCameraPreview()
//            }
//            else -> {
//                cameraPermissionRequest.launch(android.Manifest.permission.CAMERA)
//            }
//        }
        registerDevice()

        fetchFiles(serverIp,currentDeviceId) { files ->
            if (files.isNotEmpty()) {
                for (file in files) {
                    if (file.file_type.startsWith("audio/")) {
                        audioUrl = "http://$serverIp:5000${file.file_path}"
                    } else if (file.file_type.startsWith("image/")) {
                        imageUrl = "http://$serverIp:5000${file.file_path}"
                    }
                }

            } else {
                Log.d("MainActivity", "No files received")
            }
        }
        socket = setupSocket(serverIp) {
            fetchFiles(serverIp,currentDeviceId) { files ->
                if (files.isNotEmpty()) {
                    for (file in files) {
                        if (file.file_type.startsWith("audio/")) {
                            audioUrl = "http://$serverIp:5000${file.file_path}"
                        } else if (file.file_type.startsWith("image/")) {
                            imageUrl = "http://$serverIp:5000${file.file_path}"
                        }
                        targetDeviceId = file.device_id
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
                                        val description =
                                            "Screen touched at x: ${offset.x}, y: ${offset.y}"
                                        val timestamp = getCurrentTimestamp()
                                        Log.d("TouchEvent", "$description at $timestamp")
                                        postLog(
                                            "Touch event",
                                            description,
                                            timestamp,
                                            currentDeviceId
                                        )
                                    }
                                )
                            }
                    ) {
                        DisplayContent(imageUrl, audioUrl, targetDeviceId, currentDeviceId)
                    }
                }

            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting content: ${e.message}")
        }
    }

    private fun setCameraPreview() {
        setContent {
            Phones4BehaviourTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraPreviewScreen()
                }
            }
        }
    }


    private fun registerDevice() {
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val deviceModel = Build.MODEL
        Log.d("Devices", "Registered $deviceModel with ID $deviceId")

        postDevice(deviceId, deviceModel)

        val timestamp = getCurrentTimestamp()
        postLog(
            "Device registered",
            "Registered $deviceModel with ID $deviceId",
            timestamp,
            deviceId
        )
//        return device
    }

    private fun sendVideoStream(data: VideoCapture<Recorder>) {
        socket.emit("stream", data)
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

@Composable
fun DisplayContent(imageUrl: String, audioUrl: String, targetDeviceId: String, currentDeviceId: String) {
    if (targetDeviceId == currentDeviceId) {
        if (imageUrl.isNotEmpty() && audioUrl.isNotEmpty()) {
            ShowImage(imageUrl)
            playAudio(audioUrl)
        } else if (imageUrl.isNotEmpty()) {
            ShowImage(imageUrl)
        } else if (audioUrl.isNotEmpty()) {
            playAudio(audioUrl)
        } else {
            ShowText()
        }
    }
}

@Composable
fun ShowImage(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl),
        contentDescription = "Displayed Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ShowText() {
    Text(text = "Select file to display")
}



