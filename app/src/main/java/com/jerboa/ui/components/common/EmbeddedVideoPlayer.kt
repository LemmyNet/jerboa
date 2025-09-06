package com.jerboa.ui.components.common

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.AsyncImage
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.datatypes.getAspectRatio
import com.jerboa.toHttps
import com.jerboa.ui.components.videoviewer.EmbeddedData
import com.jerboa.ui.components.videoviewer.VideoHostComposer
import com.jerboa.ui.components.videoviewer.VideoState
import com.jerboa.ui.components.videoviewer.formatTime
import com.jerboa.ui.components.videoviewer.hosts.DirectFileVideoHost
import it.vercruysse.lemmyapi.datatypes.ImageDetails
import it.vercruysse.lemmyapi.datatypes.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmbeddedDataLoader(
    post: Post,
    imageDetails: ImageDetails?,
    placeholder: (@Composable () -> Unit)? = null,
    content: @Composable (Result<EmbeddedData>) -> Unit,
) {
    val url = post.url?.toHttps()
    val embeddedVideoUrl = post.embed_video_url?.toHttps()

    // Custom logic must have precedence over default embeddedVideoUrl handling
    if (url != null && VideoHostComposer.isVideo(url)) {
        val scope = rememberCoroutineScope()
        // Cache: must have dimensions on first render/frame, to prevent backscroll or return navigation from jumping the feed
        var videoDataState by remember { mutableStateOf(VideoHostComposer.getVideoDataFromCache(url)) }

        LaunchedEffect(post) {
            scope.launch(Dispatchers.Main) {
                videoDataState = VideoHostComposer.getVideoData(url)
            }
        }

        val state = videoDataState
        if (state != null) {
            content(state)
        } else if (placeholder != null) {
            placeholder()
        }

        // Only support video URLs (Not links to html pages)
    } else if (embeddedVideoUrl != null && DirectFileVideoHost.isDirectUrl(embeddedVideoUrl)) {
        content(
            Result.success(
                EmbeddedData(
                    thumbnailUrl = imageDetails?.link ?: post.thumbnail_url?.toHttps(),
                    videoUrl = embeddedVideoUrl,
                    aspectRatio = imageDetails?.getAspectRatio(),
                    height = imageDetails?.height?.toInt(),
                    width = imageDetails?.width?.toInt(),
                    title = post.embed_title ?: post.name,
                    typeName = null,
                ),
            ),
        )
    } else {
        content(
            Result.success(
                EmbeddedData(
                    thumbnailUrl = post.thumbnail_url?.toHttps() ?: imageDetails?.link,
                    videoUrl = null,
                    aspectRatio = imageDetails?.getAspectRatio(),
                    height = imageDetails?.height?.toInt(),
                    width = imageDetails?.width?.toInt(),
                    title = post.name,
                    typeName = null,
                ),
            ),
        )
    }
}

@Composable
fun EmbeddedVideoPlayer(
    id: Long,
    thumbnailUrl: String?,
    videoUrl: String,
    aspectRatio: Float,
    title: String?,
    hostId: String?,
    blur: Boolean,
    disableVideoAutoplay: Boolean,
    appState: JerboaAppState,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoAppState = appState.videoAppState

    var videoState by remember { mutableStateOf(VideoState.LOADING) }
    var playError by remember { mutableStateOf<Exception?>(null) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    var isActive by remember { mutableStateOf(false) }
    var isExoPlayerInitialized by remember { mutableStateOf(false) }

    val exoPlayer = remember { videoAppState.getOrCreateExoPlayer(context) }

    // Check if this video is the active one
    if (!disableVideoAutoplay) {
        LaunchedEffect(videoAppState.activeEmbedId.value) {
            isActive = videoAppState.activeEmbedId.value == id
            isExoPlayerInitialized = false
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            try {
                exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                exoPlayer.prepare()
                exoPlayer.play()
                Log.d("EmbeddedVideoPlayer", "Playing video $title")
            } catch (e: Exception) {
                Log.e("EmbeddedVideoPlayer", "Failed to play video $title", e)
                videoState = VideoState.FAILED
                playError = e
            }
        }
    }

    // Update position and duration
    LaunchedEffect(isActive) {
        while (isActive) {
            currentPosition = exoPlayer.currentPosition
            totalDuration = exoPlayer.duration.coerceAtLeast(1L)
            delay(500)
        }
    }

    // Handle lifecycle events: Pauze when not active..., Listen to EXO events
    DisposableEffect(lifecycleOwner, isActive) {
        if (!isActive) {
            return@DisposableEffect onDispose { }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }

        val playerListener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        videoState = VideoState.SUCCESS
                        isExoPlayerInitialized = true
                    }

                    Player.STATE_BUFFERING -> videoState = VideoState.LOADING
                    Player.STATE_IDLE, Player.STATE_ENDED -> {}
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
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoAppState.removeVideoDistance(id)
        }
    }

    // Only show blurred thumbnail
    if (blur) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .background(Color.Black)
                .combinedClickable(
                    onClick = { appState.openVideoViewer(videoUrl) },
                    onLongClick = { appState.showLinkPopup(videoUrl) },
                ),
        ) {
            if (thumbnailUrl != null) {
                AsyncImageWithBlur(
                    url = thumbnailUrl,
                    blur = true,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .background(Color.Black)
            .combinedClickable(
                onClick = { appState.openVideoViewer(videoUrl) },
                onLongClick = { appState.showLinkPopup(videoUrl) },
            ).onGloballyPositioned { coordinates ->
                val windowBounds = coordinates.boundsInWindow()
                val visibleHeight = windowBounds.height
                val height = coordinates.size.height
                val visible = visibleHeight > height * 0.5f
                val distFromTop = windowBounds.top

                videoAppState.updateVideoDistance(id, distFromTop, visible)
            },
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (hostId != null && !isExoPlayerInitialized) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                    .padding(horizontal = 2.dp),
            ) {
                Text(
                    text = hostId,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
            }
        }

        if (isActive) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isExoPlayerInitialized) 1f else 0f),
            )

            // Mute button with scrim (left corner)
            // Custom IconButton because needed click to apply to padding too
            // This makes the touch area a lot bigger and thus better
            val muted = videoAppState.isEmbedVideoMuted.value
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(if (muted) R.string.unmute else R.string.mute),
                    ) { videoAppState.toggleVideoMute() }
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .padding(3.dp),
            ) {
                Icon(
                    imageVector = if (muted) {
                        Icons.AutoMirrored.Outlined.VolumeOff
                    } else {
                        Icons.AutoMirrored.Outlined.VolumeUp
                    },
                    contentDescription = stringResource(if (muted) R.string.unmute else R.string.mute),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
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
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                    }

                VideoState.SUCCESS ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                            .padding(horizontal = 2.dp),
                    ) {
                        Text(
                            text = formatTime(totalDuration - currentPosition),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }

                VideoState.FAILED -> {}
            }
        }
    }
}
