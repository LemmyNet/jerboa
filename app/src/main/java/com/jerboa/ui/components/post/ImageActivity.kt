package com.jerboa.ui.components.post

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import com.jerboa.saveBitmap
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

val backColor = Color.Black
val backColorTranslucent = Color.Black.copy(alpha = 0.4f)
const val backFadeTime = 300

@Composable
fun ImageActivity(url: String, onBackRequest: () -> Unit) {
    @Composable
    fun BarIcon(icon: ImageVector, name: String, modifier: Modifier = Modifier, onTap: () -> Unit) {
        Box(
            Modifier
                .size(40.dp)
                .clickable(onClick = onTap)
                .then(modifier)) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = icon,
                tint = Color.White,
                contentDescription = name,
            )
        }
    }

    var showTopBar by remember { mutableStateOf(true) }

    val topBarAlpha = animateFloatAsState(if (showTopBar) 1f else 0f, animationSpec = tween(
        backFadeTime
    ))
    val backgroundColor = animateColorAsState(if (showTopBar) backColorTranslucent else backColor,
                                        animationSpec = tween(backFadeTime))

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(Modifier.background(backgroundColor.value)) {
        ImageViewer(url, onTap = { showTopBar = !showTopBar })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(topBarAlpha.value)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BarIcon(icon = Icons.Filled.ArrowBack, name = "Back") {
                onBackRequest()
            }

            Spacer(Modifier.weight(1f))

            BarIcon(icon = Icons.Outlined.Download, name = "Download") {
                coroutineScope.launch {
                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .crossfade(true)
                        .target(
                            onSuccess = {
                                val fileName = Uri.parse(url).pathSegments.last()
                                saveBitmap(context, it.toBitmap(), Bitmap.CompressFormat.WEBP, "image/webp", fileName)

                                Toast.makeText(context, "Saved image", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                            }
                        )
                        .build()

                    context.imageLoader.execute(request)
                }
            }
        }
    }
}

@Composable
fun ImageViewer(url: String, onTap: (() -> Unit)?) {
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .zoomable(rememberZoomState())
    )
}

@Composable
@Preview
fun ImageActivityPreview() {
    ImageActivity(url = "", onBackRequest = { })
}
