package com.jerboa.ui.components.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import java.lang.Math.PI
import java.lang.Math.cos
import java.lang.Math.sin

const val minScale = .5f
const val maxScale = 3f

@Composable
fun ImageActivity(url: String) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    var outerSize by remember { mutableStateOf(IntSize.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    val painter = rememberAsyncImagePainter(url, onSuccess = {
        imageSize = IntSize(it.result.drawable.intrinsicWidth, it.result.drawable.intrinsicHeight)
    })

    Box(
        modifier = Modifier
            .clip(RectangleShape) // Clip the box content
            .fillMaxSize() // Give the size you want...
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val oldScale = scale
                    val newScale = scale * zoom

                    offset = (offset + centroid / oldScale) - (centroid / newScale + pan / oldScale)
                    scale = newScale
                }
            }
            .onGloballyPositioned { coordinates ->
                outerSize = coordinates.size

                if (imageSize != IntSize.Zero)
                    scale = outerSize.width / imageSize.width.toFloat()

                offset = -Offset(outerSize.width  / 2f - (imageSize.width  * scale) / 2f,
                                 outerSize.height / 2f - (imageSize.height * scale) / 2f) / scale
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
