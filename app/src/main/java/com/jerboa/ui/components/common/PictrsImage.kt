package com.jerboa.ui.components.common

import BlurTransformation
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
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
import com.jerboa.datatypes.sampleCommunity
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
    modifier: Modifier = Modifier,
    icon: String,
    contentDescription: String?,
    size: Dp = ICON_SIZE,
    thumbnailSize: Int = ICON_THUMBNAIL_SIZE,
) {
    AsyncImage(
        model = getImageRequest(
            context = LocalContext.current,
            path = icon,
            size = thumbnailSize,
            nsfw = false,
        ),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = getBlurredRoundedModifier(
            modifier = modifier,
            rounded = true,
            nsfw = false,
        ).size(size),
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
        icon = sampleCommunity.icon!!,
        contentDescription = "",
    )
}

fun getBlurredRoundedModifier(
    modifier: Modifier = Modifier,
    rounded: Boolean,
    nsfw: Boolean,
): Modifier {
    var lModifier = modifier

    if (rounded) {
        lModifier = lModifier.clip(RoundedCornerShape(12f))
    }
    if (nsfw && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        lModifier = lModifier.blur(radius = 100.dp)
    }
    return lModifier
}

fun getImageRequest(
    context: Context,
    path: String,
    size: Int,
    nsfw: Boolean,
): ImageRequest {
    val builder = ImageRequest.Builder(context)
        .data(pictrsImageThumbnail(path, size))
        .crossfade(true)

    if (nsfw && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        builder.transformations(
            listOf(
                BlurTransformation(
                    scale = 0.5f,
                    radius = 100,
                ),
            ),
        )
    }

    return builder.build()
}

@Composable
fun PictrsThumbnailImage(
    thumbnail: String,
    nsfw: Boolean,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = getImageRequest(
            context = LocalContext.current,
            path = thumbnail,
            size = THUMBNAIL_SIZE,
            nsfw = nsfw,
        ),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = getBlurredRoundedModifier(
            modifier = modifier,
            rounded = true,
            nsfw = nsfw,
        ),
    )
}

@Composable
fun PictrsUrlImage(
    url: String,
    nsfw: Boolean,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = getImageRequest(
            context = LocalContext.current,
            path = url,
            size = MAX_IMAGE_SIZE,
            nsfw = nsfw,
        ),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = getBlurredRoundedModifier(
            modifier = modifier,
            rounded = false,
            nsfw = nsfw,
        ).fillMaxWidth(),
    )
}

@Composable
fun PictrsBannerImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    AsyncImage(
        model = getImageRequest(
            context = LocalContext.current,
            path = url,
            size = MAX_IMAGE_SIZE,
            nsfw = false,
        ),
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
