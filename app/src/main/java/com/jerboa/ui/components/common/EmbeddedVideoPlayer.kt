package com.jerboa.ui.components.common

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.ui.components.videoviewer.VideoState
import com.jerboa.ui.components.videoviewer.formatTime
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * A composable that displays an embedded video player in the feed.
 *
 * @param url The URL of the video to play
 * @param appState The JerboaAppState instance
 * @param modifier The modifier to apply to the composable
 */

// TODO aspect ratio based on url metadata
// whole custom video hosts extract video stuff

@Composable
fun EmbeddedVideoPlayer(
    id: Long,
    url: String,
    title: String,
    appState: JerboaAppState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoAppState = appState.videoAppState

    var videoState by remember { mutableStateOf(VideoState.LOADING) }
    var playError by remember { mutableStateOf<PlaybackException?>(null) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    var isVisible by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(false) }

    // Get or create the ExoPlayer instance
    val exoPlayer = remember { videoAppState.getOrCreateExoPlayer(context) }

    // Set up the player when the URL changes
//    LaunchedEffect(id) {
//        // If we're already playing this URL, don't reload it
//        if (appState.videoAppState.activeEmbedId.value != id) {
//            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
//            exoPlayer.prepare()
//            // Pause immediately to show the first frame
//            exoPlayer.pause()
//            appState.videoAppState.activeEmbedId.value = id
//        }
//    }

    // Check if this video is the active one
    LaunchedEffect(videoAppState.activeEmbedId.value) {
        isActive = videoAppState.activeEmbedId.value == id
    }

    // Handle visibility and active state changes
    LaunchedEffect(isActive) {
        if (isActive) {
            if (videoAppState.activeEmbedId.value == id) {
                exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
                exoPlayer.prepare()
                exoPlayer.play()
                Log.d("EmbeddedVideoPlayer", "Playing video $title")
            }
        } else {
            if (videoAppState.activeEmbedId.value == id) {
                exoPlayer.pause()
            }
        }
    }

    // Update position and duration
    LaunchedEffect(Unit) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            totalDuration = exoPlayer.duration.coerceAtLeast(1L)
            delay(500)
        }
    }

    // Handle lifecycle events
    DisposableEffect(lifecycleOwner, id) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (videoAppState.activeEmbedId.value == id && isVisible && isActive) {
                        exoPlayer.play()
                    }
                }

                else -> {}
            }
        }

        // Add player listener
        val playerListener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> videoState = VideoState.SUCCESS
                    Player.STATE_BUFFERING -> videoState = VideoState.LOADING
                    Player.STATE_IDLE -> videoState = VideoState.FAILED
                    Player.STATE_ENDED -> {}
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("EmbeddedVideoPlayer", "Video play failed", error)
                playError = error
                videoState = VideoState.FAILED
            }
        }

        exoPlayer.addListener(playerListener)
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.removeListener(playerListener)

            // Don't release the player here as it's managed by JerboaAppState
            if (videoAppState.activeEmbedId.value == id) {
                exoPlayer.pause()
            }
            videoAppState.removeVideoDistance(id)
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .combinedClickable(
                onClick = { appState.openVideoViewer(url) },
                onLongClick = { appState.showLinkPopup(url) },
            )
            .onGloballyPositioned { coordinates ->
                val windowBounds = coordinates.boundsInWindow()
                val visibleHeight = windowBounds.height
                val height = coordinates.size.height
                val visible = visibleHeight > height * 0.75f
                isVisible = visible

                val distFromTop = windowBounds.top

                videoAppState.updateVideoDistance(id, distFromTop, isVisible)
            }
    ) {

        if (isActive) {
            // Video player
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )


            // Mute button with scrim (left corner)
            // Custom IconButton because needed click to apply to padding to
            // This makes the touch area a lot bigger and thus better
            val muted = videoAppState.isVideoMuted.value
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(if (muted) R.string.unmute else R.string.mute),
                    ) { videoAppState.toggleVideoMute() }
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .padding(3.dp)
            ) {

                Icon(
                    imageVector = if (muted)
                        Icons.AutoMirrored.Outlined.VolumeOff
                    else
                        Icons.AutoMirrored.Outlined.VolumeUp,
                    contentDescription = stringResource(if (muted) R.string.unmute else R.string.mute),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }


            // Countdown timer (right corner)
            when (videoState) {
                VideoState.LOADING ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .alpha(if (isActive) 1f else 0f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }

                VideoState.SUCCESS ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = formatTime(totalDuration - currentPosition),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }

                VideoState.FAILED -> {}
            }

        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {}
        }
    }
}
