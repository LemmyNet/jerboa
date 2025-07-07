package com.jerboa.ui.components.imageviewer

import android.app.Activity
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.request.ImageRequest
import com.jerboa.JerboaAppState
import com.jerboa.JerboaApplication
import com.jerboa.PostLinkType
import com.jerboa.R
import com.jerboa.feat.shareMedia
import com.jerboa.feat.storeMedia
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.util.downloadprogress.DownloadProgress
import me.saket.telephoto.zoomable.OverzoomEffect
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

const val BACK_FADE_TIME = 300

@Composable
fun ImageViewerScreen(
    url: String,
    appState: JerboaAppState,
) {
    val ctx = LocalContext.current
    val backColor = MaterialTheme.colorScheme.scrim
    var showTopBar by remember { mutableStateOf(true) }

    val imageGifLoader = (ctx.applicationContext as JerboaApplication).imageViewerLoader

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
            if (!showTopBar) {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }

            controller.isAppearanceLightStatusBars = oldIcons
            window.statusBarColor = oldBarColor

            @Suppress("DEPRECATION")
            window.clearFlags(FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    var retryHash by remember { mutableIntStateOf(0) }

    var imageState by remember {
        mutableStateOf(ImageState.LOADING)
    }

    val image = remember {
        ImageRequest
            .Builder(ctx)
            .placeholder(null)
            .data(url)
            .setParameter("retry_hash", retryHash, memoryCacheKey = null)
            .listener(
                onSuccess = { _, _ -> imageState = ImageState.SUCCESS },
                onError = { _, _ -> imageState = ImageState.FAILED },
            ).build()
    }

    val zoomableState = rememberZoomableState(ZoomSpec(maxZoomFactor = 20F, overzoomEffect = OverzoomEffect.NoLimits))
    val zoomableImageState = rememberZoomableImageState(zoomableState)

    Scaffold(
        topBar = {
            ViewerHeader(showTopBar, url, appState)
        },
        content = {
            Box(
                Modifier.background(backColor),
            ) {
                if (imageState == ImageState.FAILED) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .clickable {
                                retryHash++
                                imageState = ImageState.LOADING
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
                    if (imageState == ImageState.LOADING) {
                        val currentProgress = DownloadProgress.downloadProgressFlow.collectAsStateWithLifecycle()

                        if (currentProgress.value.progressAvailable) {
                            LinearProgressIndicator(
                                progress = { currentProgress.value.progress },
                                modifier = Modifier
                                    .padding(it)
                                    .fillMaxWidth(),
                            )
                        } else {
                            LoadingBar(it)
                        }
                    }

                    ZoomableAsyncImage(
                        contentScale = ContentScale.Fit,
                        model = image,
                        imageLoader = imageGifLoader,
                        contentDescription = null,
                        state = zoomableImageState,
                        onClick = {
                            showTopBar = !showTopBar
                            if (showTopBar) {
                                controller.show(WindowInsetsCompat.Type.systemBars())
                            } else {
                                controller.hide(WindowInsetsCompat.Type.systemBars())
                            }

                            // Default behavior is that if navigation bar is hidden, the system will "steal" touches
                            // and show it again upon user's touch. We just want the user to be able to show the
                            // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
                            // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
                            controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerHeader(
    showTopBar: Boolean = true,
    url: String = "",
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
                    shareMedia(appState.coroutineScope, ctx, url, PostLinkType.Image)
                },
            ) {
                Icon(
                    Icons.Outlined.Share,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.share),
                )
            }

            IconButton(
                // TODO disable once it is busy
                onClick = {
                    storeMedia(appState.coroutineScope, ctx, url, PostLinkType.fromURL(url))
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

@Composable
@Preview
fun ImageActivityPreview() {
    ImageViewerScreen(url = "", appState = rememberJerboaAppState())
}

enum class ImageState {
    SUCCESS,
    LOADING,
    FAILED,
}
