package com.jerboa.ui.components.common

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunitySafe
import com.jerboa.decodeUriToBitmap
import com.jerboa.pictrsImageThumbnail
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.LARGER_ICON_SIZE
import com.jerboa.ui.theme.LARGER_ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.MAX_IMAGE_SIZE
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.THUMBNAIL_SIZE
import com.jerboa.ui.theme.muted

@Composable
fun CircularIcon(
    icon: String,
    contentDescription: String?,
    size: Dp = ICON_SIZE,
    thumbnailSize: Int = ICON_THUMBNAIL_SIZE,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(icon, thumbnailSize))
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = pictureBlurOrRounded(modifier, true, false)
            .size(size),
    )
}

@Composable
fun LargerCircularIcon(modifier: Modifier = Modifier, icon: String, contentDescription: String? = null) {
    CircularIcon(
        modifier = modifier,
        icon = icon,
        contentDescription = contentDescription,
        size = LARGER_ICON_SIZE,
        thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE,
    )
}

@Preview
@Composable
fun CircularIconPreview() {
    CircularIcon(
        icon = sampleCommunitySafe.icon!!,
        contentDescription = "",
    )
}

fun pictureBlurOrRounded(
    modifier: Modifier = Modifier,
    rounded: Boolean,
    nsfw: Boolean,
): Modifier {
    var modifier_ = modifier

    if (rounded) {
        modifier_ = modifier_.clip(RoundedCornerShape(12f))
    }
    if (nsfw) {
        modifier_ = modifier_.blur(radius = 100.dp)
    }
    return modifier_
}

@Composable
fun PictrsThumbnailImage(
    thumbnail: String,
    nsfw: Boolean,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(thumbnail, THUMBNAIL_SIZE))
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = pictureBlurOrRounded(modifier, true, nsfw),
    )
}

@Composable
fun PictrsUrlImage(
    url: String,
    nsfw: Boolean,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(url, MAX_IMAGE_SIZE))
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = pictureBlurOrRounded(modifier, false, nsfw)
            .fillMaxWidth(),
    )
}

@Composable
fun PictrsBannerImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pictrsImageThumbnail(url, MAX_IMAGE_SIZE))
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillWidth,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun PickImage(
    modifier: Modifier = Modifier,
    onPickedImage: (image: Uri) -> Unit,
    image: Uri? = null,
    showImage: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val ctx = LocalContext.current
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    if (image != null) {
        LaunchedEffect(image) {
            imageUri = image
            bitmap.value = decodeUriToBitmap(ctx, imageUri!!)
            Log.d("jerboa", "Uploading image...")
            Log.d("jerboa", imageUri.toString())
            onPickedImage(image)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            imageUri = it
            bitmap.value = decodeUriToBitmap(ctx, it)
            Log.d("jerboa", "Uploading image...")
            Log.d("jerboa", imageUri.toString())
            onPickedImage(it)
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    ) {
        OutlinedButton(onClick = {
            launcher.launch("image/*")
        }) {
            Text(
                text = stringResource(R.string.pictrs_image_upload_image),
                color = MaterialTheme.colorScheme.onBackground.muted,
            )
        }

        if (showImage) {
            Spacer(modifier = Modifier.height(SMALL_PADDING))
            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = stringResource(R.string.pickImage_imagePreview),
                )
            }
        }
    }
}
