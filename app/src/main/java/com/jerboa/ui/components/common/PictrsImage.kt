package com.jerboa.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunitySafe
import com.jerboa.pictrsImageThumbnail
import com.jerboa.ui.theme.*

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CircularIcon(
    icon: String,
    size: Dp = ICON_SIZE,
    thumbnailSize: Int = ICON_THUMBNAIL_SIZE,
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(icon, thumbnailSize),
            builder = {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(CircleCropTransformation())
            }
        ),
        contentDescription = null,
        modifier = Modifier.size(size)
    )
}

@Composable
fun LargerCircularIcon(icon: String) {
    CircularIcon(
        icon = icon,
        size = LARGER_ICON_SIZE,
        thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE,
    )
}

@Preview
@Composable
fun CircularIconPreview() {
    CircularIcon(icon = sampleCommunitySafe.icon!!)
}

@Composable
fun PictrsThumbnailImage(
    thumbnail: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(thumbnail, THUMBNAIL_SIZE),
            builder = {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(RoundedCornersTransformation(12f))
            },
        ),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
fun PictrsUrlImage(
    url: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(url, MAX_IMAGE_SIZE),
            builder = {
                size(OriginalSize)
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(RoundedCornersTransformation(12f))
            },
        ),
        contentScale = ContentScale.FillWidth,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
fun PictrsBannerImage(
    url: String,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = pictrsImageThumbnail(url, MAX_IMAGE_SIZE),
            builder = {
                size(OriginalSize)
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
            },
        ),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier,
    )
}
