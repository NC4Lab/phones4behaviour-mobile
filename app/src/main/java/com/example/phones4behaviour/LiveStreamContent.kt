package com.example.phones4behaviour

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.video.VideoRenderer
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.RealtimeConnection
import kotlinx.coroutines.launch


@Composable
fun LiveStreamContent(call: Call) {
    val connection by call.state.connection.collectAsState()
    val totalParticipants by call.state.totalParticipants.collectAsState()
    val backstage by call.state.backstage.collectAsState()
    val localParticipant by call.state.localParticipant.collectAsState()
    val videoState = localParticipant?.video?.collectAsState() // This gets the video state
    val video = videoState?.value // Extract the video from state
    val duration by call.state.duration.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(VideoTheme.colors.baseSheetPrimary)
            .padding(6.dp),
        contentColor = VideoTheme.colors.baseTertiary,
        topBar = {
            if (connection == RealtimeConnection.Connected) {
                if (!backstage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .background(
                                    color = VideoTheme.colors.brandPrimary,
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            text = "Live $totalParticipants",
                            color = Color.White,
                        )

                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Live for $duration",
                            color = VideoTheme.colors.basePrimary,
                        )
                    }
                } else {
                    Text(
                        text = "The livestream is not started yet",
                        color = VideoTheme.colors.basePrimary,
                    )
                }
            } else if (connection is RealtimeConnection.Failed) {
                Text(
                    text = "Connection failed",
                    color = VideoTheme.colors.basePrimary,
                )
            }
        },
        bottomBar = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = VideoTheme.colors.brandPrimary,
                    containerColor = VideoTheme.colors.brandPrimary
                ),
                onClick = {
                    scope.launch {
                        if (backstage) call.goLive() else call.stopLive()
                    }
                },
            ) {
                Text(
                    text = if (backstage) "Start Broadcast" else "Stop Broadcast",
                    color = Color.White,
                )
            }
        },
    ) {
//        VideoRenderer(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(it)
//                .clip(RoundedCornerShape(6.dp)),
//            call = call,
//            video = video,
//            videoFallbackContent = {
//                Text(text = "Video rendering failed")
//           },
//        )
        if (video != null) {
            VideoRenderer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .clip(RoundedCornerShape(6.dp)),
                call = call,
                video = video, // Ensure video is non-null
                videoFallbackContent = {
                    Text(text = "Video rendering failed")
                },
            )
        } else {
            // You can also show a loading state here while waiting for the video stream to initialize
            Text(text = "Waiting for video...", modifier = Modifier.fillMaxSize(), color = Color.White)
        }

    }
}