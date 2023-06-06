package com.jerboa.ui.components.post

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

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
            modifier = Modifier
                .align(Alignment.TopEnd)
                .alpha(topBarAlpha.value),
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
    ImageActivity(url = "")
}
