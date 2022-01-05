package com.jerboa.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunitySafe

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CircularIcon(icon: String) {
    Image(
        painter = rememberImagePainter(
            data = icon,
            builder = {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(CircleCropTransformation())
            }
        ),
        contentDescription = null,
        modifier = Modifier.size(20.dp)
    )
}

@Preview
@Composable
fun CircularIconPreview() {
    CircularIcon(icon = sampleCommunitySafe.icon!!)
}
