package com.example.phones4behaviour

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
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
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoCapture.Builder
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder

data class FileInfo(
    val file_name: String,
    val file_path: String,
    val file_type: String
)

var serverIp = BuildConfig.SERVER_IP

class MainActivity : ComponentActivity() {
    private var imageUrl by mutableStateOf("")
    private var audioUrl by mutableStateOf("")

//    private lateinit var previewView: PreviewView
//    private lateinit var videoCapture: VideoCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchFiles(serverIp) { files ->
            if (files.isNotEmpty()) {
                if (files.isNotEmpty()) {
                    for (file in files) {
                        if (file.file_type.startsWith("audio/")) {
                            audioUrl = "http://$serverIp:5000${file.file_path}"
                        } else if (file.file_type.startsWith("image/")) {
                            imageUrl = "http://$serverIp:5000${file.file_path}"
                        }
                    }
                }

            } else {
                Log.d("MainActivity", "No files received")
            }
        }
        setupSocket(serverIp) {
            fetchFiles(serverIp) { files ->
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
        }
//        startCamera()

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
                            }
                    ) {
                        DisplayContent(imageUrl, audioUrl)
//                        CameraPreview { view ->
//                            previewView = view
//                            startCamera()
//                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting content: ${e.message}")
        }
    }

//    private fun startCamera() {
//        val streamName = "stream-recording-" +
//                SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault())
//                    .format(System.currentTimeMillis()) + ".mp4"
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Video.Media.DISPLAY_NAME, streamName)
//        }
//
//        val mediaStoreOutput = MediaStoreOutputOptions.Builder(this.contentResolver,
//            MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//            .setContentValues(contentValues)
//            .build()
//
//        val recording = videoCapture.output
//            .prepareRecording(context, mediaStoreOutput)
//            .withAudioEnabled()
//            .start(ContextCompat.getMainExecutor(this), captureListener)
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val recorder = Recorder.Builder().build()
//            val preview = Preview.Builder().build()
//            val videoCapture = VideoCapture.Builder(recorder).build()
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            cameraProvider.unbindAll()
//            cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
//        }, ContextCompat.getMainExecutor(this))
//    }


    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
    

}

@Composable
fun DisplayContent(imageUrl: String, audioUrl: String) {
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

@Composable
fun CameraPreview(onPreviewViewCreated: (PreviewView) -> Unit) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            onPreviewViewCreated(view)
        }
    )
}




