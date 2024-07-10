package com.example.phones4behaviour

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object NetworkClient {
    private val client = OkHttpClient()

    fun post(url: String, json: String): String? {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body: RequestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string()
        }
    }
}