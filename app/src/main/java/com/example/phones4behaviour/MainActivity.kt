package com.example.phones4behaviour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.phones4behaviour.ui.theme.Phones4BehaviourTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Phones4BehaviourTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Placeholder(
                        text = "Phones4Behaviour",
                        modifier = Modifier.padding(innerPadding)
                    )
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

@Preview(showBackground = true)
@Composable
fun setPlaceholder() {
    Phones4BehaviourTheme {
        Placeholder("Phones4Behaviour")
    }
}