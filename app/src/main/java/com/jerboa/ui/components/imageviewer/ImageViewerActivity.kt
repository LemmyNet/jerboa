package com.jerboa.ui.components.imageviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jerboa.R
import com.jerboa.saveBitmap
import com.jerboa.saveBitmapP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.net.URL

const val backFadeTime = 300

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ImageViewer(url: String, onBackRequest: () -> Unit) {
    val backColor = MaterialTheme.colorScheme.scrim
    var showTopBar by remember { mutableStateOf(true) }

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    var debounce by remember {
        mutableStateOf(false)
    }

    val systemUiController = rememberSystemUiController()

    val window = (LocalContext.current as Activity).window
    val controller = WindowCompat.getInsetsController(window, LocalView.current)
    val oldBarColor = Color(window.statusBarColor)
    val oldIcons = controller.isAppearanceLightStatusBars

    DisposableEffect(systemUiController) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.clearFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // Unable to get the bottom navbar transparent without this
        @Suppress("DEPRECATION")
        window.addFlags(FLAG_TRANSLUCENT_NAVIGATION)

        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = false,
        )

        onDispose { // Restore previous system bars
            WindowCompat.setDecorFitsSystemWindows(window, true)

            systemUiController.setStatusBarColor(
                color = oldBarColor,
                darkIcons = oldIcons,
            )

            systemUiController.isSystemBarsVisible = true
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            @Suppress("DEPRECATION")
            window.clearFlags(FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    Scaffold(
        topBar = {
            ViewerHeader(showTopBar, onBackRequest, url)
        },
        content = {
            Box(
                Modifier
                    .background(backColor)
                    .scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState(
                            consumeScrollDelta = {
                                if (it < -70 && !debounce) {
                                    debounce = true
                                    onBackRequest()
                                }
                                it
                            },
                        ),
                    ),
            ) {
                Image(
                    painter = rememberAsyncImagePainter(url, imageLoader = imageLoader),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(
                            zoomState = rememberZoomState(),
                            onTap = {
                                showTopBar = !showTopBar
                                systemUiController.isSystemBarsVisible = showTopBar

                                // Default behavior is that if navigation bar is hidden, the system will "steal" touches
                                // and show it again upon user's touch. We just want the user to be able to show the
                                // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
                                // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
                                systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                            },
                        ),
                )
            }
        },
    )
}

// Needs to check for permission before this for API 29 and below
suspend fun SaveImage(url: String, context: Context) {
    Toast.makeText(context, context.getString(R.string.saving_image), Toast.LENGTH_SHORT).show()

    val fileName = Uri.parse(url).pathSegments.last()

    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

    withContext(Dispatchers.IO) {
        URL(url).openStream().use {
            if (SDK_INT < 29) {
                saveBitmapP(context, it, mimeType, fileName)
            } else {
                saveBitmap(context, it, mimeType, fileName)
            }
        }
    }

    Toast.makeText(context, context.getString(R.string.saved_image), Toast.LENGTH_SHORT).show()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ViewerHeader(
    showTopBar: Boolean = true,
    onBackRequest: () -> Unit = {},
    url: String = "",
) {
    val topBarAlpha by animateFloatAsState(
        targetValue = if (showTopBar) 1f else 0f,
        animationSpec = tween(backFadeTime),
        label = "topBarAlpha",
    )

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val onTap: () -> Unit = if (SDK_INT < 29) {
        val storagePermissionState = rememberPermissionState(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) {
            if (it) {
                coroutineScope.launch {
                    SaveImage(url, context)
                }
            } else {
                Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

        storagePermissionState::launchPermissionRequest
    } else {
        {
            coroutineScope.launch {
                SaveImage(url, context)
            }
        }
    }

    TopAppBar(
        colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.2f)),
        modifier = Modifier.alpha(topBarAlpha),
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onBackRequest,
            ) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.login_back),
                )
            }
        },
        actions = {
            IconButton(
                onClick = onTap,
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
    ImageViewer(url = "", onBackRequest = { })
}
