package com.jerboa.ui.components.common

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jerboa.util.BlurTransformation

@Composable
fun AsyncImageWithBlur(
    url: String,
    blur: Boolean,
    modifier: Modifier = Modifier,
    contentScale: ContentScale,
    contentDescription: String? = null,
) {
    val context = LocalContext.current

    val builder = remember {
        var temp = ImageRequest
            .Builder(context)
            .data(url)
            .crossfade(true)

        if (blur && Build.VERSION.SDK_INT < 31) {
            temp = temp.transformations(
                listOf(
                    BlurTransformation(
                        scale = 0.5f,
                        radius = 100,
                    ),
                ),
            )
        }
        temp.build()
    }

    AsyncImage(
        model = builder,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.getBlurredOrRounded(blur = blur),
    )
}
