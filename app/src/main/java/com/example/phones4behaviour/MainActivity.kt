package com.example.phones4behaviour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.phones4behaviour.ui.theme.Phones4BehaviourTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var serverIp = BuildConfig.SERVER_IP
        var port = BuildConfig.PORT

        setContent {
            Phones4BehaviourTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Placeholder(text = "Phones4Behaviour")
                        ButtonWithTimestamp(serverIp, port)
                    }
                }
            }
        }
    }
}

@Composable
fun Placeholder(text: String, modifier: Modifier = Modifier) {
    Surface(color = Color.White) {
        Text(
            text = text,
            modifier = modifier
        )
    }
}

@Composable
fun ButtonWithTimestamp(serverIp: String, port: String) {
    val coroutineScope = rememberCoroutineScope()
    Button(onClick = {
        val timestamp = getCurrentTimestamp()
        sendTimestampToServer(coroutineScope, timestamp, serverIp, port)
    }) {
        Text("press")
    }
}

fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    println(sdf)
    return sdf.format(Date())
}

fun sendTimestampToServer(coroutineScope: CoroutineScope, timestamp: String, serverIp: String, port: String) {
    coroutineScope.launch(Dispatchers.IO) {
        val json = JSONObject().put("timestamp", timestamp).toString()
        val response = NetworkClient.post("http://$serverIp:$port/times", json)
        withContext(Dispatchers.Main) {
        }
    }
}

@Preview(showBackground = true)
@Composable
fun setPlaceholder() {
    Phones4BehaviourTheme {
        Column {
            Placeholder("Phones4Behaviour")
            ButtonWithTimestamp(serverIp = "127.0.0.1", port = "5000")
        }
    }
}
