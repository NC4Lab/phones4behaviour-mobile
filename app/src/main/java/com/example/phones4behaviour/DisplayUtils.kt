package com.example.phones4behaviour

import android.media.MediaPlayer
import android.util.Log

private var mediaPlayer: MediaPlayer? = null

fun playAudio(url: String) {
    try {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { start() }
            setOnCompletionListener { release() }
            setOnErrorListener { _, what, extra ->
                Log.e("DisplayUtils", "Error occurred: what=$what, extra=$extra")
                true
            }
        }
    } catch (e: Exception) {
        Log.e("DisplayUtils", "Error playing audio: ${e.message}")
    }
}