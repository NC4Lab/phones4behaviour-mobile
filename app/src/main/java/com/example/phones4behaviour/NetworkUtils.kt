package com.example.phones4behaviour

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()

fun postLog(tag: String, desc: String, timestamp: String, deviceId: String) {
    val logInfo = LogFile(tag, desc, timestamp, deviceId)
    val json = Gson().toJson(logInfo)

    val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)
    val request = Request.Builder()
        .url("http://$serverIp:5000/logs")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("NetworkUtils", "Failed to post log: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("NetworkUtils", "Log posted successfully")
            } else {
                Log.e("NetworkUtils", "Failed to post log, response code: ${response.code}")
            }
        }
    })
}

fun fetchFiles(serverIp: String, onResponse: (List<FileInfo>) -> Unit) {
    val request = Request.Builder()
        .url("http://$serverIp:5000/uploads/display")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            e.printStackTrace()
            Log.e("NetworkUtils", "Network request failed: ${e.message}")
        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            if (response.isSuccessful) {
                response.body?.let { responseBody ->
                    val fileListType = object : TypeToken<List<FileInfo>>() {}.type
                    val files: List<FileInfo> = Gson().fromJson(responseBody.string(), fileListType)

                    onResponse(files)
//                    Log.d("trying to display", "$files")
                }
            } else {
                Log.e("NetworkUtils", "Response not successful: ${response.code}")
            }
        }
    })
}

fun postDevice(id: String, model: String) {
    val deviceInfo = DeviceInfo(id, model)
    val json = Gson().toJson(deviceInfo)

//    Log.d("Device JSON", json)

    val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)
    val request = Request.Builder()
        .url("http://$serverIp:5000/devices")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("NetworkUtils", "Failed to post device: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("NetworkUtils", "Device posted successfully")
            } else {
                Log.e("NetworkUtils", "Failed to post device, response code: ${response.code}")
            }
        }
    })
}

fun postFrame(image: String, timestamp: String, deviceId: String) {
    val frameInfo = FrameFile(image, timestamp, deviceId)
    val json = Gson().toJson(frameInfo)

    val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
        .url("http://$serverIp:5000/frames")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("NetworkUtils", "Failed to post frame: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("NetworkUtils", "Frame posted successfully")
            } else {
                Log.e("NetworkUtils", "Failed to post frame, response code: ${response.code}")
            }
        }
    })
}