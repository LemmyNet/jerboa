package com.jerboa.ui.components.post

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

const val minScale = .5f
const val maxScale = 3f

val backColor = Color.Black
val backColorTranslucent = Color.Black.copy(alpha = 0.4f)
val backFadeTime = 300

@Composable
fun ImageActivity(url: String) {
    var showTopBar by remember { mutableStateOf(true) }

    val topBarAlpha = animateFloatAsState(if (showTopBar) 1f else 0f, animationSpec = tween(backFadeTime))
    val backgroundColor = animateColorAsState(if (showTopBar) backColorTranslucent else backColor,
                                        animationSpec = tween(backFadeTime))

    Box(Modifier.background(backgroundColor.value)) {
        ImageViewer(url, onTap = { showTopBar = !showTopBar })

        Row(
            modifier = Modifier.align(Alignment.TopEnd).alpha(topBarAlpha.value),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text("Download")
            }
        }
    }
}

@Composable
fun ImageViewer(url: String, onTap: (() -> Unit)?) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var hasCentered by remember { mutableStateOf(false) }

    var outerSize by remember { mutableStateOf(IntSize.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    val painter = rememberAsyncImagePainter(url, onSuccess = {
        imageSize = IntSize(it.result.drawable.intrinsicWidth, it.result.drawable.intrinsicHeight)
    })

    Box(
        modifier = Modifier
            .clip(RectangleShape) // Clip the box content
            .fillMaxSize() // Give the size you want...
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val oldScale = scale
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                    offset = (offset + centroid / oldScale) - (centroid / newScale + pan / oldScale)
                    scale = newScale
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    onTap?.invoke()
                })
            }
            .onGloballyPositioned { coordinates ->
                outerSize = coordinates.size

                if (!hasCentered && imageSize != IntSize.Zero) {
                    hasCentered = true
                    scale = outerSize.width / imageSize.width.toFloat()

                    offset = -Offset(
                        outerSize.width / 2f - (imageSize.width * scale) / 2f,
                        outerSize.height / 2f - (imageSize.height * scale) / 2f
                    ) / scale
                }
            }
    ) {
        if (painter.state == AsyncImagePainter.State.Loading(painter)) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSurface,
            )
        } else {
            Image(painter,
                modifier = Modifier
                    .graphicsLayer(
                        translationX = -offset.x * scale,
                        translationY = -offset.y * scale,
                        scaleX = scale,
                        scaleY = scale,
                        transformOrigin = TransformOrigin(0f, 0f)
                    ),
                contentDescription = null,
            )
        }
    }
}

@Composable
@Preview
fun ImageActivityPreview() {
    ImageActivity(url = "")
}
