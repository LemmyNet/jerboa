package com.jerboa.ui.components.videoviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.jerboa.JerboaAppState
import com.jerboa.PostLinkType
import com.jerboa.R
import com.jerboa.feat.shareMedia
import com.jerboa.feat.storeMedia
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.util.downloadprogress.DownloadProgress
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

const val BACK_FADE_TIME = 300

enum class VideoState {
    SUCCESS,
    LOADING,
    FAILED,
}

@Composable
fun VideoViewerScreen(
    url: String,
    appState: JerboaAppState,
) {
    val ctx = LocalContext.current
    val backColor = MaterialTheme.colorScheme.scrim
    var showControls by remember { mutableStateOf(true) }

    val window = (ctx as Activity).window
    val controller = WindowCompat.getInsetsController(window, LocalView.current)

    val oldBarColor = window.statusBarColor
    val oldIcons = controller.isAppearanceLightStatusBars

    DisposableEffect(Unit) {
        controller.isAppearanceLightStatusBars = false
        window.statusBarColor = Color.Transparent.toArgb()

        // Unable to get the bottom navbar transparent without this
        @Suppress("DEPRECATION")
        window.addFlags(FLAG_TRANSLUCENT_NAVIGATION)

        onDispose {
            if (!showControls) {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }

            controller.isAppearanceLightStatusBars = oldIcons
            window.statusBarColor = oldBarColor

            @Suppress("DEPRECATION")
            window.clearFlags(FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    var videoState by remember { mutableStateOf(VideoState.LOADING) }
    var isPlaying by remember { mutableStateOf(true) }
    var playError by remember { mutableStateOf<PlaybackException?>(null) }

    val exoPlayer = remember {
        val player = ExoPlayer.Builder(ctx).build()
        player.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
        player.volume = if (appState.videoAppState.isVideoPlayerMuted.value) 0f else 1f
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> videoState = VideoState.SUCCESS
                    Player.STATE_BUFFERING -> videoState = VideoState.LOADING
                    Player.STATE_IDLE -> videoState = VideoState.FAILED
                    Player.STATE_ENDED -> isPlaying = false
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("VideoViewerScreen", "Video play failed", error)
                playError = error
            }
        })
        player
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Pauses the video when the app is in the background
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (isPlaying) {
                        exoPlayer.play()
                    }
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    val togglePlayPause = {
        isPlaying = !isPlaying
        if (isPlaying) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    Scaffold(
        topBar = {
            VideoViewerHeader(showControls, url, appState)
        },
        content = { paddingValues ->
            Box(
                Modifier.background(backColor),
            ) {
                if (playError != null) {
                    ApiErrorText(
                        msg = "${playError!!.errorCodeName}: ${playError!!.cause?.message}",
                        paddingValues = paddingValues,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (videoState == VideoState.FAILED) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .clickable {
                                videoState = VideoState.LOADING
                                exoPlayer.prepare()
                                exoPlayer.play()
                            },
                        Arrangement.Center,
                        Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = stringResource(id = R.string.image_error_icon),
                        )
                        Text(text = stringResource(id = R.string.image_failed_loading))
                    }
                } else {
                    if (videoState == VideoState.LOADING) {
                        val currentProgress = DownloadProgress.downloadProgressFlow.collectAsStateWithLifecycle()

                        if (currentProgress.value.progressAvailable) {
                            LinearProgressIndicator(
                                progress = { currentProgress.value.progress },
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxWidth(),
                            )
                        } else {
                            LoadingBar(paddingValues)
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    player = exoPlayer
                                    // Disables builtin player controls
                                    useController = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    showControls = !showControls
                                    if (showControls) {
                                        controller.show(WindowInsetsCompat.Type.systemBars())
                                    } else {
                                        controller.hide(WindowInsetsCompat.Type.systemBars())
                                    }

                                    controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                                },
                        )

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            VideoControlBar(
                                exoPlayer = exoPlayer,
                                isPlaying = isPlaying,
                                isMuted = appState.videoAppState.isVideoPlayerMuted.value,
                                showControls = showControls,
                                onPlayPauseClick = togglePlayPause,
                                onMuteClick = {
                                    appState.videoAppState.isVideoPlayerMuted.value = !appState.videoAppState.isVideoPlayerMuted.value
                                    exoPlayer.volume = if (appState.videoAppState.isVideoPlayerMuted.value) 0f else 1f
                                },
                            )
                        }
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoViewerHeader(
    showTopBar: Boolean,
    url: String,
    appState: JerboaAppState,
) {
    val topBarAlpha by animateFloatAsState(
        targetValue = if (showTopBar) 1f else 0f,
        animationSpec = tween(BACK_FADE_TIME),
        label = "topBarAlpha",
    )

    val ctx = LocalContext.current

    TopAppBar(
        colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.2f)),
        modifier = Modifier.alpha(topBarAlpha),
        title = {},
        navigationIcon = {
            IconButton(
                onClick = appState::navigateUp,
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.topAppBar_back),
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    shareMedia(appState.coroutineScope, ctx, url, PostLinkType.Video)
                },
            ) {
                Icon(
                    Icons.Outlined.Share,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.share),
                )
            }

            IconButton(
                onClick = {
                    storeMedia(appState.coroutineScope, ctx, url, PostLinkType.Video)
                },
            ) {
                Icon(
                    Icons.Outlined.Download,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.download),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoControlBar(
    exoPlayer: ExoPlayer,
    isPlaying: Boolean,
    isMuted: Boolean,
    showControls: Boolean,
    onPlayPauseClick: () -> Unit,
    onMuteClick: () -> Unit,
) {
    val controlsAlpha by animateFloatAsState(
        targetValue = if (showControls) 1f else 0f,
        animationSpec = tween(BACK_FADE_TIME),
        label = "controlsAlpha",
    )

    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    var bufferedPosition by remember { mutableLongStateOf(0L) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            bufferedPosition = exoPlayer.bufferedPosition.coerceAtLeast(currentPosition)
            totalDuration = exoPlayer.duration.coerceAtLeast(1L)
            sliderPosition = (currentPosition.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
            delay(500)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(controlsAlpha)
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
            .padding(horizontal = 4.dp)
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play),
                    tint = Color.White,
                )
            }

            Text(
                text = formatTime(currentPosition),
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            // Customized to enable buffered progress tracking + white theming
            val colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
            )
            val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    val newPosition = (it * totalDuration).toLong()
                    exoPlayer.seekTo(newPosition)
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f),
                interactionSource = interactionSource,
                colors = colors,
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        colors = colors,
                        thumbSize = DpSize(4.dp, 24.dp),
                    )
                },
                track = { sliderPositions ->
                    SliderDefaults.Track(
                        sliderState = sliderPositions,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                        ),
                        drawStopIndicator = null,
                        modifier = Modifier.height(4.dp),
                    )
                    // Add buffered progress track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((bufferedPosition.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.extraSmall,
                            ),
                    )
                },
            )

            Text(
                text = formatTime(totalDuration),
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            // TODO: hide if no audio available
            IconButton(
                onClick = onMuteClick,
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Outlined.VolumeOff else Icons.AutoMirrored.Outlined.VolumeUp,
                    contentDescription = stringResource(if (isMuted) R.string.unmute else R.string.mute),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
@Preview(name = "Video Viewer Header")
fun VideoViewerHeaderPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp),
        ) {
            VideoViewerHeader(
                showTopBar = true,
                url = "https://example.com/video.mp4",
                appState = rememberJerboaAppState(),
            )
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Composable
@Preview(name = "Video Viewer Screen")
fun VideoViewerScreenPreview() {
    VideoViewerScreen(url = "", appState = rememberJerboaAppState())
}
